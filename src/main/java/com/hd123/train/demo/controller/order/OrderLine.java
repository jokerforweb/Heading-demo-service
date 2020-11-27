package com.hd123.train.demo.controller.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("OrderLine")
public class OrderLine {
    @ApiModelProperty("uuid")
    private String uuid;
    @ApiModelProperty("订单uuid")
    private String orderUuid;
    @ApiModelProperty("行号")
    private int lineNo;
    @ApiModelProperty("商品sku.id")
    private String skuId;
    @ApiModelProperty("商品sku.name")
    private String skuName;
    @ApiModelProperty("数量")
    private BigDecimal qty;
    @ApiModelProperty("金额")
    private BigDecimal amount;

    @Override
    public String toString() {
        return "OrderLine{" +
                "uuid='" + uuid + '\'' +
                ", orderUuid='" + orderUuid + '\'' +
                ", lineNo=" + lineNo +
                ", skuId='" + skuId + '\'' +
                ", skuName='" + skuName + '\'' +
                ", qty=" + qty +
                ", amount=" + amount +
                '}';
    }
}
