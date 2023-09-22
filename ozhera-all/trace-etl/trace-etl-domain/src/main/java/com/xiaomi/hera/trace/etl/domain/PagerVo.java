package com.xiaomi.hera.trace.etl.domain;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/4/19 10:13 上午
 */
public class PagerVo {
    private int page;
    private Integer pageSize;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
