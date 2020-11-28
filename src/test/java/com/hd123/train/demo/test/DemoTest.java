package com.hd123.train.demo.test;

import com.hd123.rumba.commons.jdbc.executor.BatchUpdater;
import com.hd123.rumba.commons.jdbc.sql.DeleteBuilder;
import com.hd123.rumba.commons.jdbc.sql.DeleteStatement;
import com.hd123.rumba.commons.jdbc.sql.Expr;
import com.hd123.rumba.commons.jdbc.sql.InsertBuilder;
import com.hd123.rumba.commons.jdbc.sql.InsertStatement;
import com.hd123.rumba.commons.jdbc.sql.Predicates;
import com.hd123.rumba.commons.jdbc.sql.SelectBuilder;
import com.hd123.rumba.commons.jdbc.sql.SelectStatement;
import com.hd123.rumba.commons.jdbc.sql.UpdateBuilder;
import com.hd123.rumba.commons.jdbc.sql.UpdateStatement;
import com.hd123.train.demo.Application;
import com.hd123.train.demo.dao.order.DeliverTypeEnum;
import com.hd123.train.demo.dao.order.OrderStateEnum;
import com.hd123.train.demo.dao.product.PPRODUCT;
import com.hd123.train.demo.dao.product.PRETAILCATALOG;
import com.hd123.train.demo.dao.product.PSKU;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private BatchUpdater batchUpdater;

    private BatchUpdater getBatchUpdaterInstance() {
        if (batchUpdater == null) {
            batchUpdater = new BatchUpdater(jdbcTemplate);
        }
        return batchUpdater;
    }

    /**
     * 练习用
     */
    @Test
    public void test1() {
        InsertStatement insert = new InsertBuilder()
                .table(PSKU.TABLE_NAME)
                .addValue(PSKU.UUID, UUID.randomUUID().toString())
                .addValue(PSKU.SPUID, "0001")
                .addValue(PSKU.ID, "0001")
                .addValue(PSKU.NAME, "Apple/苹果 iPhone 11")
                .addValue(PSKU.IMAGE, "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1575191341219&di=deccfc3eef04af955e53d9cf0243fbad&imgtype=0&src=http%3A%2F%2Fdingyue.nosdn.127.net%2FoFrbOsoSF3PLjusd8gA3Y1O0tg6erCdGopyuKM1Ef4T9h1523946901622.jpg")
                .addValue(PSKU.MARKET_PRICE, new BigDecimal("1299"))
                .addValue(PSKU.PRICE, new BigDecimal("999"))
                .build();
        jdbcTemplate.update(insert);
        UpdateStatement update = new UpdateBuilder()
                .table(PSKU.TABLE_NAME)
                .addValue(PSKU.NAME, "1")
                .where(Predicates.equals(PSKU.ID, "0001"))
                .build();
        jdbcTemplate.update(update);
        DeleteStatement delete = new DeleteBuilder()
                .table(PSKU.TABLE_NAME)
                .where(Predicates.equals(PSKU.NAME, "1"))
                .build();
        jdbcTemplate.update(delete);
    }

    /**
     * 练习1 初始化数据
     * 1.  每个商品的 RETAILCATALOG 的 BEGINDATE ~ ENDDATE 不能重叠。
     * 2.  RETAILCATALOG 的PRICE 从100~999 不等。
     * 测试SQL： SELECT count(*) as '商品价格溢出数' FROM RETAILCATALOG where PRICE > 999 or PRICE < 100
     * 3.  5% 的商品无RETAILCATALOG
     * 测试SQL： SELECT count(*)/1000 as probability from PRODUCT P WHERE (SELECT COUNT(*) FROM RETAILCATALOG RC where RC.PRODUCTUUID = P.UUID ) = 0
     * 4.  5% 的商品，初始State=999
     * 测试SQL ： SELECT (SELECT COUNT(*) from PRODUCT where STATE = '999')/COUNT(*) as probability FROM PRODUCT ;
     */
    @Test
    public void addProductData() {
        BatchUpdater batch = getBatchUpdaterInstance();
        for (int i = 0; i < 1000; i++) {
            String uuid = UUID.randomUUID().toString().substring(0, 20);
            String code = UUID.randomUUID().toString().substring(0, 20);
            String name = UUID.randomUUID().toString().substring(0, 20);
            double orderPrice = Math.random() * 50;
            int state = 0;
            if (getPersentProbability(5)) {
                state = 999;
            }
            InsertStatement insert = new InsertBuilder()
                    .table(PPRODUCT.TABLE_NAME)
                    .addValue(PPRODUCT.UUID, uuid)
                    .addValue(PPRODUCT.CODE, code)
                    .addValue(PPRODUCT.NAME, name)
                    .addValue(PPRODUCT.ORDERPRICE, orderPrice)
                    .addValue(PPRODUCT.STATE, state)
                    .build();
            batch.add(insert);
            addRetailCatalogData(uuid);
        }
        batch.update();
    }

    /**
     * 1. 使用UpdateBuilder构建UpdateStatement，修改指定商品的Price
     * 2. 使用DeleteBuilder构建DeleteStatement删除指定的商品
     * 3. 使用UpdateBuilder构建UpdateStatement， 将无RETAILCATALOG的商品，状态修改为999
     */
    @Test
    public void Test2() {
        BatchUpdater batch = getBatchUpdaterInstance();

        // 使用UpdateBuilder构建UpdateStatement，修改指定商品的Price
        // 待修改的uuid (RETAILCATALOG表中第一条)
        String updateProductUuid = "f6b1af6d-39a5-41c1-a";
        UpdateStatement updateRetailCatalogPrie = new UpdateBuilder()
                .table(PRETAILCATALOG.TABLE_NAME)
                .addValue(PRETAILCATALOG.PRICE, "20.00")
                .where(Predicates.equals(PRETAILCATALOG.PRODUCTUUID, updateProductUuid))
                .build();
        jdbcTemplate.update(updateRetailCatalogPrie);

        // 使用DeleteBuilder构建DeleteStatement删除指定的商品
        // 待删除的uuid (RETAILCATALOG表中第二条)
        // 测试SQL select * from PRODUCT P,RETAILCATALOG RC where P.UUID = '4d5775c3-f110-491c-8' or RC.PRODUCTUUID = 'f739754f-80c0-4266-a'
        String deleteProductUuid = "f739754f-80c0-4266-a";
        DeleteStatement deleteRetailCatalog = new DeleteBuilder()
                .table(PRETAILCATALOG.TABLE_NAME)
                .where(Predicates.equals(PRETAILCATALOG.PRODUCTUUID, deleteProductUuid))
                .build();
        batch.add(deleteRetailCatalog);
        DeleteStatement deleteProduct = new DeleteBuilder()
                .table(PPRODUCT.TABLE_NAME)
                .where(Predicates.equals(PPRODUCT.UUID, deleteProductUuid))
                .build();
        batch.add(deleteProduct);

        // 使用UpdateBuilder构建UpdateStatement， 将无RETAILCATALOG的商品，状态修改为999
        // 测试SQL ： SELECT * from PRODUCT P WHERE (SELECT COUNT(*) FROM RETAILCATALOG RC WHERE RC.PRODUCTUUID = P.UUID) = 0
        UpdateStatement updateRetailCatalogState = new UpdateBuilder()
                .table(PPRODUCT.TABLE_NAME)
                .setValue(PPRODUCT.STATE, "999")
                .where(Predicates.notExists(
                        new SelectBuilder()
                                .from(PRETAILCATALOG.TABLE_NAME)
                                .where(Predicates.equals(PPRODUCT.TABLE_NAME + "." + PPRODUCT.UUID,
                                        Expr.valueOf(PRETAILCATALOG.TABLE_NAME + "." + PRETAILCATALOG.PRODUCTUUID)))
                                .build()))
                .build();
        batch.add(updateRetailCatalogState);
        batch.update();
    }

    @Test
    public void Test3() {
        // 使用SelectBuilder构建SelectStatement查询PRODUCT.PRICE > 100的商品
        // 测试SQL ： select count(DISTINCT PRODUCTUUID) FROM RETAILCATALOG where PRICE > 100
        // 查询全部RETAILCATALOG表中PRICE > 100 的商品信息
        SelectStatement selectProductPrice = new SelectBuilder()
                .select("*").from(PPRODUCT.TABLE_NAME)
                .where(Predicates.in(PPRODUCT.TABLE_NAME, PRETAILCATALOG.UUID, new SelectBuilder()
                        .select(PRETAILCATALOG.PRODUCTUUID).distinct()
                        .from(PRETAILCATALOG.TABLE_NAME)
                        .where(Predicates.greaterOrEquals(PRETAILCATALOG.PRICE, 100))
                        .build()))
                .build();
        List<Map<String, Object>> listProduct = jdbcTemplate.queryForList(selectProductPrice.getSql(), 100);
        System.out.println("RETAILCATALOG表中PRICE > 100 商品数量：" + listProduct.size());

        // 题：查询指定代码的商品，在指定日期的销售目录的PRICE(不包含截止日期)
        // 数据为RETAILCATALOG第三条数据的Code 测试SQL：Select * from RETAILCATALOG where PRODUCTUUID = 'ab5c0565-d2b5-4e74-b'
        String productCode = "8edcc710-16eb-450c-9";
        String productDate = "2020-10-03";
        SelectStatement selectPriceInDate = new SelectBuilder()
                .select(PRETAILCATALOG.PRICE)
                .from(PRETAILCATALOG.TABLE_NAME)
                .where(Predicates.equals(PRETAILCATALOG.PRODUCTUUID, new SelectBuilder()
                        .select(PRETAILCATALOG.UUID)
                        .from(PPRODUCT.TABLE_NAME)
                        .where(Predicates.equals(PPRODUCT.CODE, productCode))
                        .build()))
                .where(Predicates.greaterOrEquals("0", Expr.valueOf("DATEDIFF(BEGINDATE,\'" + productDate + "\')")))
                .where(Predicates.less("0", Expr.valueOf("DATEDIFF(ENDDATE,\'" + productDate + "\')")))
                .build();
        Object[] params = new Object[]{
                productCode
        };
        double price = 0;
        try {
            price = jdbcTemplate.queryForObject(selectPriceInDate.getSql(), params, Double.class);
            System.out.println("UUID:" + "ab5c0565-d2b5-4e74-b" + ";CODE:" + productCode + ";PRICE:" + price);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("该商品在此日期无销售记录");
        }

        // 查询PRODUCT中，存在20个及以上的REAILCATALOG的商品
        SelectStatement selectProductRetailCatalog = new SelectBuilder()
                .select(PPRODUCT.TABLE_NAME + "." + PPRODUCT.UUID)
                .from(PPRODUCT.TABLE_NAME)
                .where(Predicates.lessOrEquals("20", new SelectBuilder()
                        .select("count(*)")
                        .from(PRETAILCATALOG.TABLE_NAME)
                        .where(Predicates.equals(PRETAILCATALOG.TABLE_NAME + "." + PRETAILCATALOG.PRODUCTUUID,
                                Expr.valueOf(PPRODUCT.TABLE_NAME + "." + PRETAILCATALOG.UUID)))
                        .build()))
                .build();
        List<Map<String, Object>> retailCatalogList = jdbcTemplate.queryForList(selectProductRetailCatalog.getSql());
        // 测试SQL ： select count(*) from PRODUCT WHERE (select count(*) from RETAILCATALOG where RETAILCATALOG.PRODUCTUUID = PRODUCT.UUID) >= 20
        System.out.println(retailCatalogList.size());
    }


    public void addRetailCatalogData(String productUuid) {
        BatchUpdater batch = getBatchUpdaterInstance();
        if (getPersentProbability(5)) {
            return;
        }
        Date beginDate = null;
        Date endDate = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            beginDate = format.parse("2020-10-01");
        } catch (ParseException excepted) {
            // Do Nothing
        }
        for (int i = 0; i < getIntRangeRandom(0, 50); i++) {
            String uuid = UUID.randomUUID().toString().substring(0, 20);
            double price = getDoubleRangeRandom(100, 999);
            endDate = addDayOfDate(beginDate, getIntRangeRandom(1, 10));
            InsertStatement insert = new InsertBuilder()
                    .table(PRETAILCATALOG.TABLE_NAME)
                    .addValue(PRETAILCATALOG.UUID, uuid)
                    .addValue(PRETAILCATALOG.PRODUCTUUID, productUuid)
                    .addValue(PRETAILCATALOG.BEGINDATE, beginDate)
                    .addValue(PRETAILCATALOG.ENDDATE, endDate)
                    .addValue(PRETAILCATALOG.PRICE, price)
                    .build();
            batch.add(insert);
            beginDate = endDate;
        }
    }

    /**
     * 将原日期新增n天后，但返回新日期
     *
     * @param date   原日期
     * @param dayNum 新增天数
     * @return 新增天数后的Date类型数据
     */
    public Date addDayOfDate(Date date, int dayNum) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, dayNum);
        Date newDate = c.getTime();
        return newDate;
    }

    /**
     * 获取指定范围内浮点类型数据
     *
     * @param start 范围下限
     * @param end   范围上限
     * @return 返回范围内的整数类型数据
     */
    public int getIntRangeRandom(int start, int end) {
        int randomNum = (int) (Math.random() * (end + 1 - start) + start);
        return randomNum;
    }

    /**
     * 获取指定范围内浮点类型数据
     *
     * @param start 范围下限
     * @param end   范围上限
     * @return 返回范围内的浮点类型数据
     */
    public double getDoubleRangeRandom(int start, int end) {
        double randomNum = (Math.random() * (end + 1 - start) + start);
        return randomNum;
    }

    public Map<String, Date> stringToDate(String beginDate, String endDate) {
        Map<String, Date> dateMap = new HashMap<String, Date>(2, 1);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date begin = format.parse(beginDate);
            Date end = format.parse(endDate);
            dateMap.put("begin", begin);
            dateMap.put("end", end);
        } catch (ParseException exceped) {
            // Do Nothing
        }
        return dateMap;
    }

    /**
     * 获取指定百分比概率下某一次是否成功
     *
     * @param persent 百分比
     * @return boolean类型是否成功
     */
    public boolean getPersentProbability(int persent) {
        int randomNum = (int) (Math.random() * 100);
        if (persent < 0) {
            persent = 0;
        }
        if (persent > 100) {
            persent = 100;
        }
        return randomNum < persent;
    }
}
