package com.hd123.train.demo.dao.order;

public enum OrderStateEnum {
    SUBMITED, AUDITED, ABORTED;

    public static String getSubmitted(){
        return "submited";
    }

    public static String getAudited(){
        return "audited";
    }

    public static String getAborted(){
        return "aborted";
    }
}
