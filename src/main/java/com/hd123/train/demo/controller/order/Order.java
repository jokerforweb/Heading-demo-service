package com.hd123.train.demo.controller.order;

import com.hd123.train.demo.dao.order.DeliverTypeEnum;
import com.hd123.train.demo.dao.order.OrderStateEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("Order")
public class Order {
    private String uuid;
    @ApiModelProperty("单号")
    private String billNumber;
    @ApiModelProperty("下单日期")
    private Date orderTime;
    @ApiModelProperty("购买人")
    private String buyer;
    @ApiModelProperty("订单状态")
    private OrderStateEnum orderState;
    @ApiModelProperty("订单金额")
    private BigDecimal amount;
    @ApiModelProperty("发货方式")
    private DeliverTypeEnum deliverType;
    @ApiModelProperty("订单说明")
    private String remark;
    @ApiModelProperty("订单明细")
    private List<OrderLine> lines;

    public Order(String uuid, String billNumber, Date orderTime, String buyer, BigDecimal amount, DeliverTypeEnum deliverType, String remark) {
        this.uuid = uuid;
        this.billNumber = billNumber;
        this.orderTime = orderTime;
        this.buyer = buyer;
        this.amount = amount;
        this.deliverType = deliverType;
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Order{" +
                "uuid='" + uuid + '\'' +
                ", billNumber='" + billNumber + '\'' +
                ", orderTime=" + orderTime +
                ", buyer='" + buyer + '\'' +
                ", orderState=" + orderState +
                ", amount=" + amount +
                ", deliverType=" + deliverType +
                ", remark='" + remark + '\'' +
                ", lines=" + lines +
                '}';
    }
}
