package com.hd123.train.demo.dao.product;

import com.hd123.train.demo.controller.product.SKUDescription;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PSKUDescription {

  public static final String TABLE_NAME = "SR_SKU_DESCRIPTION";

  public static final String UUID = "uuid";
  public static final String SKU_UUID = "skuUuid";
  public static final String FORMAT = "format";
  public static final String CONTENT = "content";

  public static final String[] COLUMNS = new String[]{
          UUID, SKU_UUID, FORMAT, CONTENT
  };

  public static SKUDescription mapRow(ResultSet rs, int rowNum) throws SQLException {
    SKUDescription target = new SKUDescription();
    target.setUuid(rs.getString(UUID));
    target.setSkuUuid(rs.getString(SKU_UUID));
    target.setFormat(rs.getString(FORMAT));
    target.setContent(rs.getString(CONTENT));
    return target;
  }

  public static Map<String, Object> toFieldValues(SKUDescription entity) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, ObjectUtils.defaultIfNull(entity.getUuid(), java.util.UUID.randomUUID().toString()));
    fvm.put(SKU_UUID, entity.getSkuUuid());
    fvm.put(FORMAT, entity.getFormat());
    fvm.put(CONTENT, entity.getContent());
    return fvm;
  }
}
