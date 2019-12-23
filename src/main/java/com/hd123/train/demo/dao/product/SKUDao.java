package com.hd123.train.demo.dao.product;

import com.hd123.rumba.commons.biz.query.QueryResult;
import com.hd123.rumba.commons.jdbc.executor.JdbcPagingQueryExecutor;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.train.demo.controller.product.SKU;
import com.hd123.train.demo.controller.product.SKUDescription;
import com.hd123.train.demo.controller.product.SKUFilter;
import com.hd123.train.demo.controller.product.SKUProperty;
import com.hd123.train.demo.infrastructure.tx.DemoTX;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SKUDao {
  @Autowired
  private JdbcTemplate jdbcTemplate;

  public SKU get(String id) {
    SelectStatement select = new SelectBuilder()
            .select(PSKU.COLUMNS).from(PSKU.TABLE_NAME)
            .where(Predicates.equals(PSKU.ID, id))
            .build();

    List<SKU> list = jdbcTemplate.query(select, PSKU::mapRow);
    if (list.isEmpty())
      return null;

    SKU target = list.get(0);
    select = new SelectBuilder()
            .select(PSKUDescription.COLUMNS).from(PSKUDescription.TABLE_NAME)
            .where(Predicates.equals(PSKUDescription.SKU_UUID, target.getUuid()))
            .build();
    target.setDescriptions(jdbcTemplate.query(select, PSKUDescription::mapRow));

    select = new SelectBuilder()
            .select(PSKUProperty.COLUMNS).from(PSKUProperty.TABLE_NAME)
            .where(Predicates.equals(PSKUProperty.SKU_UUID, target.getUuid()))
            .build();
    target.setProperties(jdbcTemplate.query(select, PSKUProperty::mapRow));
    return target;
  }

  @DemoTX
  public void delete(String id) {
    DeleteStatement delete = new DeleteBuilder()
            .table(PSKU.TABLE_NAME)
            .where(Predicates.equals(PSKU.ID, id))
            .build();
    jdbcTemplate.update(delete);
  }

  @DemoTX
  public void save(SKU sku) {
    if (sku.getUuid() == null) {
      InsertStatement insert = new InsertBuilder()
              .table(PSKU.TABLE_NAME)
              .addValues(PSKU.toFieldValues(sku))
              .build();
      jdbcTemplate.update(insert);
    } else {
      UpdateStatement update = new UpdateBuilder()
              .table(PSKU.TABLE_NAME)
              .addValues(PSKU.toFieldValues(sku))
              .build();
      jdbcTemplate.update(update);
    }
    DeleteStatement delete = new DeleteBuilder()
            .table(PSKUDescription.TABLE_NAME)
            .where(Predicates.equals(PSKUDescription.SKU_UUID, sku.getUuid()))
            .build();
    jdbcTemplate.update(delete);
    for (SKUDescription description : sku.getDescriptions()) {
      description.setSkuUuid(sku.getUuid());
      InsertStatement insert = new InsertBuilder()
              .table(PSKUDescription.TABLE_NAME)
              .addValues(PSKUDescription.toFieldValues(description))
              .build();
      jdbcTemplate.update(insert);
    }
    delete = new DeleteBuilder()
            .table(PSKUProperty.TABLE_NAME)
            .where(Predicates.equals(PSKUProperty.SKU_UUID, sku.getUuid()))
            .build();
    jdbcTemplate.update(delete);
    for (SKUProperty property : sku.getProperties()) {
      property.setSkuUuid(sku.getUuid());
      InsertStatement insert = new InsertBuilder()
              .table(PSKUProperty.TABLE_NAME)
              .addValues(PSKUProperty.toFieldValues(property))
              .build();
      jdbcTemplate.update(insert);
    }
  }

  public QueryResult<SKU> query(SKUFilter filter) {
    SelectStatement select = new SelectBuilder()
            .select(PSKU.COLUMNS).from(PSKU.TABLE_NAME)
            .build();
    if (StringUtils.isBlank(filter.getSpuidEq()) == false) {
      select.where(Predicates.equals(PSKU.SPUID, filter.getSpuidEq()));
    }
    if (StringUtils.isBlank(filter.getIdNameLike()) == false) {
      select.where(Predicates.or(
              Predicates.equals(PSKU.ID, filter.getIdNameLike()),
              Predicates.equals(PSKU.NAME, filter.getIdNameLike())));
    }
    if (filter.getPriceLessOrEquals() != null) {
      select.where(Predicates.lessOrEquals(PSKU.SPUID, filter.getPriceLessOrEquals()));
    }
    if (filter.getPriceGreaterOrEquals() != null) {
      select.where(Predicates.greaterOrEquals(PSKU.SPUID, filter.getPriceGreaterOrEquals()));
    }

    JdbcPagingQueryExecutor executor = new JdbcPagingQueryExecutor(jdbcTemplate, PSKU::mapRow);
    return executor.query(select, filter.getPage(), filter.getPageSize());
  }
}
