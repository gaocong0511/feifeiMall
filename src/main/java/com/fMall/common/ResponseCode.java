package com.fMall.common;

/**
 * Created by 高琮 on 2018/5/1.
 */
public enum ResponseCode {
    SUCCESS(1,"SUCCESS"),
    ERROR(0,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEAGAL_ARGUMENT(2,"ILLEAGAL_ARGUMENT");

    private final int code;
    private final String desc;

    ResponseCode(int code, String desc){
        this.code=code;
        this.desc=desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
