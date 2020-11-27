package com.hd123.train.demo.dao.order;

public enum DeliverTypeEnum {
    SELFPICK, COURIER;

    public String getSelfpick(){
        return "selfpick";
    }

    public String getCourier(){
        return "courier";
    }

    public String getValue(){
        if (this == SELFPICK){
            return getSelfpick();
        } else {
            return getSelfpick();
        }
    }
}
