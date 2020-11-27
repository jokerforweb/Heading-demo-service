package com.hd123.train.demo.dao.product;

import com.hd123.rumba.commons.jdbc.entity.PStandardEntity;

public class PPRODUCT extends PStandardEntity {
    public static final String TABLE_NAME = "PRODUCT";

    public static final String UUID = "UUID";
    public static final String CODE = "CODE";
    public static final String NAME = "NAME";
    public static final String ORDERPRICE = "ORDERPRICE";
    public static final String SALEPRICE = "SALEPRICE";
    public static final String STATE = "STATE";

    public static final String[] COLUMNS = new String[]{
            UUID, CODE, NAME, ORDERPRICE, SALEPRICE, STATE
    };
}
