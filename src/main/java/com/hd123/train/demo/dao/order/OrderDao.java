package com.hd123.train.demo.dao.order;

import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.sql.Expr;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.train.demo.controller.order.Order;
import com.hd123.train.demo.controller.order.OrderLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public class OrderDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 创建订单
     * @param order 订单信息
     * @return 订单是否创建成功
     */
    public synchronized boolean createOrder(Order order) {
        String uuid = UUID.randomUUID().toString().substring(0, 20);
        String billNumber = UUID.randomUUID().toString().substring(0, 30);
        BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
        InsertStatement insertOrder = new InsertBuilder()
                .table("SR_SKU_ORDER")
                .addValue("UUID", order.getUuid())
                .addValue("BILLNUMBER", order.getBillNumber())
                .addValue("ORDERTIME", new Date())
                .addValue("BUYER", order.getBuyer())
                .addValue("STATE", OrderStateEnum.getSubmitted())
                .addValue("AMOUNT", order.getAmount())
                .addValue("DELIVERTYPE", order.getDeliverType().getValue())
                .addValue("REMARK", order.getRemark())
                .build();
        batchUpdater.add(insertOrder);
        List<OrderLine> orderLineList = order.getLines();
        int size = orderLineList.size();
        boolean isSkuEnough = true;
        for (int i = 0; i < size; i++) {
            OrderLine orderLine = orderLineList.get(i);
            SelectStatement selectOrderLineQty = new SelectBuilder()
                    .select("stockQty")
                    .from("SR_SKU")
                    .where(Predicates.equals("id", orderLine.getSkuId()))
                    .build();
            Object[] params = new Object[]{
                    orderLine.getSkuId()
            };
            int skuQty = 0;
            try {
                skuQty = jdbcTemplate.queryForObject(selectOrderLineQty.getSql(), params, Integer.class);
            } catch (EmptyResultDataAccessException excepted) {
                // Do Nothing
            }
            if (skuQty < orderLine.getQty().intValue()) {
                isSkuEnough = false;
                break;
            }
            InsertStatement insertOrderLine = new InsertBuilder()
                    .table("SR_SKU_ORDERLINE")
                    .addValue("UUID", orderLine.getUuid())
                    .addValue("ORDERUUID", orderLine.getOrderUuid())
                    .addValue("LINENO", orderLine.getLineNo())
                    .addValue("SKUID", orderLine.getSkuId())
                    .addValue("SKUNAME", orderLine.getSkuName())
                    .addValue("QTY", orderLine.getQty())
                    .addValue("AMOUNT", orderLine.getAmount())
                    .build();
            batchUpdater.add(insertOrderLine);
        }
        if (isSkuEnough) {
            batchUpdater.update();

        }
        return isSkuEnough;
    }

    /**
     * 保存订单信息
     * @param order 订单信息
     */
    @Async
    public void updateOrder(Order order) {
        BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
        UpdateStatement updateOrder = new UpdateBuilder()
                .table("SR_SKU_ORDER")
                .addValue("ORDERTIME", order.getOrderTime())
                .addValue("BUYER", order.getBuyer())
                .addValue("AMOUNT", order.getAmount())
                .addValue("DELIVERTYPE", order.getDeliverType().getValue())
                .addValue("REMARK", order.getRemark())
                .where(Predicates.equals("BILLNUMBER", order.getBillNumber()))
                .build();
        batchUpdater.add(updateOrder);
        List<OrderLine> orderLineList = order.getLines();
        int size = orderLineList.size();
        for (int i = 0; i < size; i++) {
            OrderLine orderLine = orderLineList.get(i);
            SelectStatement selectOrderLineQty = new SelectBuilder()
                    .select("stockQty")
                    .from("SR_SKU")
                    .where(Predicates.equals("id", orderLine.getSkuId()))
                    .build();
            Object[] params = new Object[]{
                    orderLine.getSkuId()
            };
            int skuQty = 0;
            try {
                skuQty = jdbcTemplate.queryForObject(selectOrderLineQty.getSql(), params, Integer.class);
            } catch (EmptyResultDataAccessException excepted) {
                // Do Nothing
            }
            boolean isSkuEnough = orderLine.getQty().intValue() > skuQty;
            if (isSkuEnough) {
                UpdateStatement updateSku = new UpdateBuilder()
                        .table("SR_SKU_ORDERLINE")
                        .addValue("ORDERUUID", orderLine.getOrderUuid())
                        .addValue("LINENO", orderLine.getLineNo())
                        .addValue("SKUID", orderLine.getSkuId())
                        .addValue("SKUNAME", orderLine.getSkuName())
                        .addValue("QTY", orderLine.getQty())
                        .addValue("AMOUNT", orderLine.getAmount())
                        .where(Predicates.equals("UUID", orderLine.getUuid()))
                        .build();
                batchUpdater.add(updateSku);
            }
        }
        batchUpdater.update();
    }

    /**
     * 审核订单功能
     * @param billNumber 订单号
     * @return 审核订单是否成功
     */
    public synchronized boolean checkOrder(String billNumber){
        BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
        String orderUuid = getOrderUuidByBillNumber(billNumber);
        // 获取订单中商品数量大于库存的订单明细数
        SelectStatement selectOrderLine = new SelectBuilder()
                .select("count(*)").from("SR_SKU")
                .where(Predicates.greaterOrEquals("stockQty",new SelectBuilder()
                        .select("QTY").from("SR_SKU_ORDERLINE")
                        .where(Predicates.equals("ORDERUUID",orderUuid))))
                .build();
        int falseOrderCount = 0;
        Object[] params = new Object[]{
                orderUuid
        };
        falseOrderCount = jdbcTemplate.queryForObject(selectOrderLine.getSql(),params,Integer.class);
        if (falseOrderCount > 0){
            return false;
        }
        // 修改订单信息
        UpdateStatement updateOrder = new UpdateBuilder()
                .table("SR_SKU_ORDER")
                .addValue("STATE",OrderStateEnum.AUDITED)
                .where(Predicates.equals("BILLNUMBER",billNumber))
                .build();
        batchUpdater.add(updateOrder);
        // 扣减对应商品库存
        UpdateStatement updateOrderLine = new UpdateBuilder()
                .table("SR_SKU")
                .addValue("stockQty",
                        Expr.valueOf("stockQty - (Select QTY from SR_SKU_ORDERLINE where BILLNUMBER ="+billNumber+" )"))
                .build();
        batchUpdater.add(updateOrderLine);
        batchUpdater.update();
        return true;
    }

    /**
     * 作废订单
     * @param billNumber 订单号
     */
    public synchronized void abortOrder(String billNumber){
        BatchUpdater batchUpdater = new BatchUpdater(jdbcTemplate);
        UpdateStatement updateOrder = new UpdateBuilder()
                .table("SR_SKU_ORDER")
                .addValue("STATE",OrderStateEnum.ABORTED)
                .where(Predicates.equals("BILLNUMBER",billNumber))
                .build();
        UpdateStatement updateOrderLine = new UpdateBuilder()
                .table("SR_SKU")
                .addValue("stockQty",
                        Expr.valueOf("stockQty + (Select QTY from SR_SKU_ORDERLINE where BILLNUMBER ="+billNumber+" )"))
                .build();
        batchUpdater.add(updateOrderLine);
        batchUpdater.update();
    }

    public String getOrderUuidByBillNumber(String billNumber){
        String orderUuid = "";
        SelectStatement selectStatement = new SelectBuilder()
                .select("UUID").from("ORDER")
                .where(Predicates.equals("BILLNUMBER",billNumber))
                .build();
        try {
            orderUuid = jdbcTemplate.queryForObject(selectStatement.getSql(),new Object[]{billNumber},String.class);
        } catch (EmptyResultDataAccessException excepted) {
            // Do Nothing
        }
        return orderUuid;
    }

    /**
     * 判断该订单号是否存在
     * @param billNumber 订单号
     * @return 是否存在订单号
     */
    public boolean hasOrder(String billNumber) {
        int orderCount = 0;
        SelectStatement selectOrder = new SelectBuilder()
                .select("count(*)").from("SR_SKU_ORDER")
                .where(Predicates.equals("BILLNUMBER", billNumber))
                .build();
        try {
            orderCount = jdbcTemplate.queryForObject(selectOrder.getSql(), new Object[]{billNumber}, Integer.class);
        } catch (EmptyResultDataAccessException excepted) {
            // Do Nothing
        }
        return orderCount > 0;
    }
}
