package com.hd123.train.demo.controller.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silent
 */
@Getter
@Setter
@ApiModel("SKU")
public class SKU {

  // uuid 不对外提供
  @JsonIgnore
  private String uuid;

  @ApiModelProperty(value = "SPUID")
  private String spuid;
  @ApiModelProperty(value = "ID")
  private String id;
  @ApiModelProperty(value = "名称")
  private String name;
  @ApiModelProperty(value = "主图")
  private String image;
  @ApiModelProperty(value = "说明")
  private String remark;
  @ApiModelProperty(value = "图片列表")
  private List<String> images = new ArrayList<String>();
  @ApiModelProperty(value = "市场价")
  private BigDecimal marketPrice = BigDecimal.ZERO;
  @ApiModelProperty(value = "价格")
  private BigDecimal price = BigDecimal.ZERO;
  @ApiModelProperty(value = "库存数量")
  private BigDecimal stockQty = BigDecimal.ZERO;
  @ApiModelProperty(value = "标签")
  private List<String> tags = new ArrayList<String>();
  @ApiModelProperty(value = "属性列表")
  private List<SKUProperty> properties = new ArrayList<SKUProperty>();
  @ApiModelProperty(value = "详情列表")
  private List<SKUDescription> descriptions = new ArrayList<SKUDescription>();
}
