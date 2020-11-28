package com.hd123.train.demo.controller.order;

import com.hd123.train.demo.dao.order.DeliverTypeEnum;
import com.hd123.train.demo.dao.order.OrderDao;
import com.hd123.train.demo.dao.order.OrderStateEnum;
import com.hd123.train.demo.infrastructure.biz.BaseResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1/demo/order", produces = "application/json;charset=utf-8")
public class OrderController {
    @Autowired
    private OrderDao orderDao;

    /**
     * 创建订单
     * 1. 订单存在则不可保存
     * 2. 订单中商品不存在不可以创建订单
     * 3. 商品库存不足不可以创建订单
     * @param order 订单信息
     * @return BaseResponse
     */
    @ApiOperation(value = "创建订单")
    @PostMapping(path = "/create")
    public BaseResponse create(
            @ApiParam(value = "订单数据", required = true) @RequestBody Order order) {
        if (orderDao.hasOrder(order.getBillNumber())) {
            return BaseResponse.fail(503, "订单重复创建。");
        }
        if (!orderDao.isSkuIdsExits(order.getLines())){
            return BaseResponse.fail(503,"商品不存在");
        }
        return orderDao.createOrder(order) ? BaseResponse.success() : BaseResponse.fail(503, "商品库存不足, 订单创建失败");
    }

    /**
     * 保存订单
     * 1. 仅"已提交"的订单可以保存
     * 2. 商品不存在则不可以保存订单
     * 3. 订单号不存在不可以保存订单
     * 4. 商品库存不足不可以保存订单
     * @param order 订单信息
     * @return BaseResponse
     */
    @ApiOperation(value = "保存订单")
    @PostMapping(path = "/update")
    public BaseResponse update(
            @ApiParam(value = "订单数据", required = true) @RequestBody Order order) {
        if (!orderDao.hasOrder(order.getBillNumber())) {
            return BaseResponse.fail(503, "订单数据不存在");
        }
        String state = orderDao.getStateByBillNumber(order.getBillNumber());
        if (state != null) {
            if (!state.equals(OrderStateEnum.getSubmitted())) {
                return BaseResponse.fail(503, "该订单不可保存");
            }
        }
        if (!orderDao.isSkuIdsExits(order.getLines())){
            return BaseResponse.fail(503,"商品不存在");
        }
        if (orderDao.updateOrder(order)) {
            return BaseResponse.success();
        } else {
            return BaseResponse.fail(503,"商品库存不足");
        }

    }

    /**
     * 审核订单
     * 1. 订单编号不存在不可以审核
     * 2. 已审核或已作废的订单不可以审核
     * 3. 库存不足审核失败
     * @param billNumber 订单号
     * @return BaseResponse
     */
    @ApiOperation(value = "审核订单")
    @PostMapping(path = "/check/{billNumber}")
    public BaseResponse check(
            @ApiParam(value = "订单单号", required = true) @PathVariable("billNumber") String billNumber) {
        if (!orderDao.hasOrder(billNumber)) {
            return BaseResponse.fail(503, "订单数据不存在");
        }
        // 检查订单是否重复审核
        String state = orderDao.getStateByBillNumber(billNumber);
        if (state != null) {
            if (state.equals(OrderStateEnum.getAudited())) {
                return BaseResponse.fail(503, "订单已审核");
            }
            if (state.equals(OrderStateEnum.getAborted())) {
                return BaseResponse.fail(503, "订单已作废");
            }

        }
        if (orderDao.checkOrder(billNumber)) {
            return BaseResponse.success();
        } else {
            return BaseResponse.fail(503, "商品库存不足");
        }

    }

    /**
     * 作废订单
     * 1. 订单状态为已作废不可以作废
     * 2. 订单状态为已审核，作废后会恢复商品库存
     * @param billNumber 订单号
     * @return BaseResponse
     */
    @ApiOperation(value = "作废订单")
    @PostMapping(path = "/abort/{billNumber}")
    public BaseResponse abort(
            @ApiParam(value = "订单单号", required = true) @PathVariable("billNumber") String billNumber) {
        if (!orderDao.hasOrder(billNumber)) {
            return BaseResponse.fail(503, "订单数据不存在");
        }
        String state = orderDao.getStateByBillNumber(billNumber);
        if (state != null && !state.equals(OrderStateEnum.getAborted())) {
            orderDao.abortOrder(billNumber, state);
            return BaseResponse.success();
        } else {
            return BaseResponse.fail(503, "订单不可作废");
        }
    }

    @ApiOperation(value = "获取订单及订单明细测试数据")
    @PostMapping(path = "/test")
    public Order getOrder() {
        String uuid = UUID.randomUUID().toString().substring(0, 20);
        String billNumber = UUID.randomUUID().toString().substring(0, 20);
        SimpleDateFormat format = new SimpleDateFormat();
        Date orderTime = null;
        try {
            orderTime = format.parse("2020-11-27");
        } catch (ParseException excepted) {
            // Do Nothing
        }
        String buyer = "购买人";
        BigDecimal amount = new BigDecimal(5000);
        DeliverTypeEnum deliverType = DeliverTypeEnum.SELFPICK;
        String remark = "订单说明";
        Order order = new Order(uuid, billNumber, orderTime, buyer, amount, deliverType, remark);

        List<OrderLine> orderLineList = new ArrayList<>();
        String orderLineUuid = UUID.randomUUID().toString().substring(0, 20);
        String orderUuid = uuid;
        int lineNo = 0;
        String skuId = "S1";
        String skuName = "S1_Name";
        BigDecimal qty = new BigDecimal(20);
        BigDecimal orderLineAmount = new BigDecimal(100000);
        OrderLine orderLine = new OrderLine(orderLineUuid, orderUuid, lineNo, skuId, skuName, qty, orderLineAmount);
        orderLineList.add(orderLine);
        order.setLines(orderLineList);
        return order;
    }
}
