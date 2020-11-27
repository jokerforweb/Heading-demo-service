/**
 * 版权所有(C)，上海海鼎信息工程股份有限公司，2013，所有权利保留。
 * <p>
 * 项目名：	e21-h5-core
 * 文件名：	PromotionTopic.java
 * 模块说明：
 * 修改历史：
 * 2013-2-21 - chenwenfeng - 创建。
 */
package com.hd123.train.demo.dao.product;

import com.hd123.rumba.commons.collection.CollectionUtil;
import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;
import com.hd123.train.demo.controller.product.SKU;
import org.apache.commons.lang3.ObjectUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 促销主题
 *
 * @author chenwenfeng
 */
public abstract class PSKU {
  public static final String TABLE_NAME = "SR_SKU";

  public static final String UUID = "uuid";
  public static final String SPUID = "spuid";
  public static final String ID = "id";
  public static final String NAME = "name";
  public static final String IMAGE = "image";
  public static final String IMAGES = "images";
  public static final String REMARK = "remark";
  public static final String MARKET_PRICE = "marketPrice";
  public static final String PRICE = "price";
  public static final String STOCK_QTY = "stockQty";
  public static final String TAGS = "tags";

  public static final String[] COLUMNS = new String[]{
          UUID, SPUID, ID, NAME, IMAGE, IMAGES, REMARK, MARKET_PRICE, PRICE, STOCK_QTY, TAGS
  };

  public static SKU mapRow(ResultSet rs, int rowNum) throws SQLException {
    SKU target = new SKU();
    target.setUuid(rs.getString(UUID));
    target.setSpuid(rs.getString(SPUID));
    target.setId(rs.getString(ID));
    target.setName(rs.getString(NAME));
    target.setImage(rs.getString(IMAGE));
    target.setImages(CollectionUtil.toList(rs.getString(IMAGES)));
    target.setRemark(rs.getString(REMARK));
    target.setMarketPrice(rs.getBigDecimal(MARKET_PRICE));
    target.setPrice(rs.getBigDecimal(PRICE));
    target.setStockQty(rs.getBigDecimal(STOCK_QTY));
    target.setTags(CollectionUtil.toList(rs.getString(TAGS)));
    return target;
  }

  public static Map<String, Object> toFieldValues(SKU entity) {
    Map<String, Object> fvm = new HashMap<>();
    fvm.put(UUID, ObjectUtils.defaultIfNull(entity.getUuid(), java.util.UUID.randomUUID().toString()));
    fvm.put(SPUID, entity.getSpuid());
    fvm.put(ID, entity.getId());
    fvm.put(NAME, entity.getName());
    fvm.put(IMAGE, entity.getImage());
    fvm.put(IMAGES, CollectionUtil.toString(entity.getImages()));
    fvm.put(REMARK, entity.getRemark());
    fvm.put(MARKET_PRICE, entity.getMarketPrice());
    fvm.put(PRICE, entity.getPrice());
    fvm.put(STOCK_QTY, entity.getStockQty());
    fvm.put(TAGS, CollectionUtil.toString(entity.getTags()));
    return fvm;
  }
}
