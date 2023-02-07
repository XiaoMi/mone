package com.xiaomi.data.push.common;

import java.io.Serializable;
import java.util.List;

public class PageInfo<T> implements Serializable {

    private int pageSize;

    private int pageNum;

    private List<T> data;

    private int total;

    public PageInfo(){}
    public PageInfo(Integer pageSize, Integer pageNum){
        this.pageSize = (pageSize==null || pageSize<=0)?10:pageSize;
        this.pageNum = (pageNum==null || pageNum<=0)?1:pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
