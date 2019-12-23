package com.hd123.train.demo.test;

import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.train.demo.Application;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author cRazy
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class DemoTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  public void test1() {
    InsertStatement insert = new InsertBuilder()
            .table("SR_SKU")
            .addValue("uuid", UUID.randomUUID().toString())
            .addValue("spuid", "0001")
            .addValue("id", "0001")
            .addValue("name", "Apple/苹果 iPhone 11")
            .addValue("image", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575191341219&di=deccfc3eef04af955e53d9cf0243fbad&imgtype=0&src=http%3A%2F%2Fdingyue.nosdn.127.net%2FoFrbOsoSF3PLjusd8gA3Y1O0tg6erCdGopyuKM1Ef4T9h1523946901622.jpg")
            .addValue("marketPrice", new BigDecimal("1299"))
            .addValue("price", new BigDecimal("999"))
            .build();
    jdbcTemplate.update(insert);
  }
}
