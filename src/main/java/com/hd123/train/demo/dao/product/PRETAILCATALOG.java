package com.hd123.train.demo.dao.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PRETAILCATALOG implements RowMapper<PRETAILCATALOG> {
    public static final String TABLE_NAME = "RETAILCATALOG";

    public static final String UUID = "UUID";
    public static final String PRODUCTUUID = "PRODUCTUUID";
    public static final String BEGINDATE = "BEGINDATE";
    public static final String ENDDATE = "ENDDATE";
    public static final String PRICE = "PRICE";

    private String uuid;
    private String productUuid;
    private Date beginDate;
    private Date endDate;
    private double price;

    @Override
    public PRETAILCATALOG mapRow(ResultSet resultSet, int i) throws SQLException {
        PRETAILCATALOG retailCatalog = new PRETAILCATALOG();
        retailCatalog.setUuid(resultSet.getString("UUID"));
        retailCatalog.setProductUuid(resultSet.getString("PRODUCTUUID"));
        retailCatalog.setBeginDate(resultSet.getDate("BEGINDATE"));
        retailCatalog.setEndDate(resultSet.getDate("ENDDATE"));
        retailCatalog.setPrice(resultSet.getDouble("PRICE"));
        return retailCatalog;
    }
}
