package com.xiaomi.hera.trace.etl.domain.tracequery;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/11/7 2:15 下午
 */
public class TraceQueryResult<T> {
    private T data;
    private int limit;
    private int offset;
    private int total;

    public TraceQueryResult(T data, int limit, int offset, int total){
        this.data = data;
        this.limit = limit;
        this.offset = offset;
        this.total = total;
    }

    public TraceQueryResult(T data, int total){
        this.data = data;
        this.total = total;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
