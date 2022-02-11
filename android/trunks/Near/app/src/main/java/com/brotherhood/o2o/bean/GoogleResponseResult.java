package com.brotherhood.o2o.bean;

/**
 * Created by jl.zhang on 2016/1/4.
 */
public class GoogleResponseResult<T> {
    private String status;
    private T results;

    public String getStatus() {
        return status;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
