package com.brotherhood.o2o.model;


public class ResponseResult<T> {

    private int c;
    private String msg;
    private T data;

    public int getCode() {
        return c;
    }

    public void setCode(int c) {
        this.c = c;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
