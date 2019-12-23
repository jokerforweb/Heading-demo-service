package com.hd123.train.demo.controller.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author Silent
 */
@Data
public class SKUDescription {

  public static final String FORMAT_IMAGE = "image";
  public static final String FORMAT_VIDEO = "video";
  public static final String FORMAT_RICHTEXT = "richText";

  @JsonIgnore
  private String uuid;
  @JsonIgnore
  private String skuUuid;

  private String format;
  private String content;
}
