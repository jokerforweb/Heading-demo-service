package com.hd123.train.demo.controller.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Silent
 */
@Data
@ApiModel("商品属性")
public class SKUProperty {

  @JsonIgnore
  private String uuid;
  @JsonIgnore
  private String skuUuid;

  @ApiModelProperty(value = "属性名称")
  private String name;
  @ApiModelProperty(value = "属性值")
  private String value;
}
