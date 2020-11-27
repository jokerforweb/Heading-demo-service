package com.hd123.train.demo.controller.order;

import com.hd123.train.demo.dao.order.DeliverTypeEnum;
import com.hd123.train.demo.dao.order.OrderDao;
import com.hd123.train.demo.dao.order.OrderStateEnum;
import com.hd123.train.demo.infrastructure.biz.BaseResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation(value = "创建订单")
    @PostMapping(path = "/create")
    public BaseResponse create(
            @ApiParam(value = "订单数据", required = true) @RequestBody Order order) {
        if (orderDao.hasOrder(order.getBillNumber())){
            return BaseResponse.fail(503, "订单重复创建");
        }
        return orderDao.createOrder(order) ? BaseResponse.success() : BaseResponse.fail(503, "商品库存不足");
    }

    @ApiOperation(value = "保存订单")
    @PostMapping(path = "/update")
    public BaseResponse update(
            @ApiParam(value = "订单数据", required = true) @RequestBody Order order) {
        if (!orderDao.hasOrder(order.getBillNumber())) {
            return BaseResponse.fail(503, "订单数据不存在");
        }
        orderDao.updateOrder(order);
        return BaseResponse.success();
    }

    @ApiOperation(value = "审核订单")
    @PostMapping(path = "/check")
    public BaseResponse check(
            @ApiParam(value = "订单单号", required = true) @RequestBody String billNumber) {
        if (!orderDao.hasOrder(billNumber)) {
            return BaseResponse.fail(503, "订单数据不存在");
        }
        if (orderDao.checkOrder(billNumber)){
            return BaseResponse.success();
        }else {
            return BaseResponse.fail(5, "商品库存不足");
        }

    }

    @ApiOperation(value = "作废订单")
    @PostMapping(path = "/abort")
    public BaseResponse abort(
            @ApiParam(value = "订单单号", required = true) @RequestBody String billNumber) {
        if (!orderDao.hasOrder(billNumber)) {
            return BaseResponse.fail(503, "订单数据不存在");
        }
        orderDao.abortOrder(billNumber);
        return BaseResponse.success();
    }

    @ApiOperation(value = "获取订单及订单明细测试数据")
    @PostMapping(path = "/test")
    public Order getOrder(){
        String uuid = UUID.randomUUID().toString().substring(0,20);
        String billNumber = UUID.randomUUID().toString().substring(0,20);
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
        Order order = new Order(uuid,billNumber,orderTime,buyer,amount,deliverType,remark);

        List<OrderLine> orderLineList = new ArrayList<>();
        String orderLineUuid = UUID.randomUUID().toString().substring(0,20);
        String orderUuid = uuid;
        int lineNo = 0;
        String skuId = "S1";
        String skuName = "S1_Name";
        BigDecimal qty = new BigDecimal(20);
        BigDecimal orderLineAmount = new BigDecimal(100000);
        OrderLine orderLine = new OrderLine(orderLineUuid,orderUuid,lineNo,skuId,skuName,qty,orderLineAmount);
        orderLineList.add(orderLine);
        order.setLines(orderLineList);
        return order;
    }
}
