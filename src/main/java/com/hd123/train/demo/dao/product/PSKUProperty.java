package com.hd123.train.demo.dao.product;

import com.hd123.train.demo.controller.product.SKUProperty;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class PSKUProperty {

  public static final String TABLE_NAME = "SR_SKU_PROPERTY";

  public static final String UUID = "uuid";
  public static final String SKU_UUID = "skuUuid";
  public static final String NAME = "name";
  public static final String VALUE = "value";

  public static final String[] COLUMNS = new String[]{
          UUID, SKU_UUID, NAME, VALUE
  };

  public static SKUProperty mapRow(ResultSet rs, int rowNum) throws SQLException {
    SKUProperty target = new SKUProperty();
    target.setUuid(rs.getString(UUID));
    target.setSkuUuid(rs.getString(SKU_UUID));
    target.setName(rs.getString(NAME));
    target.setValue(rs.getString(VALUE));
    return target;
  }

  public static Map<String, Object> toFieldValues(SKUProperty entity) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, ObjectUtils.defaultIfNull(entity.getUuid(), java.util.UUID.randomUUID().toString()));
    fvm.put(SKU_UUID, entity.getSkuUuid());
    fvm.put(NAME, entity.getName());
    fvm.put(VALUE, entity.getValue());
    return fvm;
  }
}
