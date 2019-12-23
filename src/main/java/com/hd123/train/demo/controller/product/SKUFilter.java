package com.hd123.train.demo.controller.product;

import com.hd123.train.demo.infrastructure.biz.Filter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Silent
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SKUFilter extends Filter {
  private String spuidEq;
  private List<String> idIn;
  private String idNameLike;
  private BigDecimal priceLessOrEquals;
  private BigDecimal priceGreaterOrEquals;
}
