package com.hd123.train.demo.infrastructure.biz;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author __Silent
 */
@Data
public abstract class Filter {

  @ApiModelProperty(value = " 返回的页号，从0开始计数。传入负数等价于传入0；传入大于可能的最大页数将被解释为返回最后一页。", required = false)
  private int page;
  @ApiModelProperty(value = "每页最多包含记录数。0表示将返回所有结果集，默认为0。传入负数等价于传入0。", required = false)
  private int pageSize;
  @ApiModelProperty(value = "排序字段", required = false)
  private String sortKey;
  @ApiModelProperty(value = "是否降序", required = false)
  private boolean desc;
  @ApiModelProperty(value = "级联查询信息", required = false)
  private String fetchParts;
}
