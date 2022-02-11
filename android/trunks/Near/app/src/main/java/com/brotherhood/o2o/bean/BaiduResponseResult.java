package com.brotherhood.o2o.bean;

/**
 * 百度POI请求接口返回数据格式
 * @param <T>
 */
public class BaiduResponseResult<T> {

    private int status;
    private T result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
