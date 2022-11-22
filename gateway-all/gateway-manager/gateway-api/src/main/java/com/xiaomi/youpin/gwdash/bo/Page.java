package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Page<T> implements Serializable {
    private int page;
    private int pageSize;
    private int total;
    private int totalPageCount;
    private List<T> list;

    /**
     *
     * @param page // 当前第多少页
     * @param pageSize // 每页大小
     * @param total // 总数
     * @param list // 数据载体
     * @param hasPaged // 是否已经分页
     */
    public Page(int page, int pageSize, int total, List<T> list, boolean hasPaged) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        initPage(page, pageSize, total, list, hasPaged);
    }

    private void initPage(int pageIndex, int pageSize, int totalItemCount, List<T> data, boolean hasPaged) {
        // 计算总页数
        if (totalItemCount > 0 && pageSize > 0) {
            this.totalPageCount = totalItemCount/pageSize + 1;
        }
        // 截取当前页数据
        if (null == data) {
            this.list = new ArrayList<T>();
        } else if (data.isEmpty() || hasPaged) {
            this.list = data;
        } else {
            if (pageIndex == this.totalPageCount) { // 最后一页
                this.list = data.subList((pageIndex-1)*pageSize, data.size());
            } else if (pageIndex < this.totalPageCount) { // 不是最后一页
                this.list = data.subList((pageIndex-1)*pageSize, pageIndex*pageSize);
            } else { // 当前页超出总页数
                throw new IndexOutOfBoundsException("当前页数超出总页数范围");
            }
        }
    }
}
