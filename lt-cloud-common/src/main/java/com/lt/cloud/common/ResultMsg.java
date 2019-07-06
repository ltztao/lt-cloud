package com.lt.cloud.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultMsg<T> implements Serializable {

    private int code;
    private T data;
    private String msg;

    public final static ResultMsg SUCCESS = new ResultMsg(0,"success");

    public final static ResultMsg FAILURE = new ResultMsg(-1,"error");
    // 服务熔断
    public static ResultMsg HYSTRIX(String msg) {
        return new ResultMsg(10000, msg);
    }

    public final static ResultMsg Unauthorized = new ResultMsg(401,"unauthorized!");

    public final static ResultMsg InvalidToken = new ResultMsg(10002,"invalid token!");

    public static ResultMsg ERROR(String msg) {
        return new ResultMsg(-1, msg);
    }

    public static ResultMsg SUCCESS(Object obj){
        return new ResultMsg<Object>(0, obj, "success");
    }

    public ResultMsg() {
    }

    public ResultMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultMsg(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }
}
