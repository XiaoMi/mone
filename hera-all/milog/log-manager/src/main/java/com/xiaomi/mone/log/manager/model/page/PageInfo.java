package com.xiaomi.mone.log.manager.model.page;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/22 11:48
 */
@Data
@NoArgsConstructor
public class PageInfo<T> implements Serializable {
    private Integer page;
    private Integer pageSize;
    private Integer total;
    private Integer totalPageCount;
    private List<T> list;

    public static PageInfo emptyPageInfo() {
        return new PageInfo(0, 0, 0, Collections.emptyList());
    }


    public PageInfo(int page, int pageSize, int total, List<T> list) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        initPage(page, pageSize, total, list, true);
    }

    /**
     * @param page     // 当前第多少页
     * @param pageSize // 每页大小
     * @param total    // 总数
     * @param list     // 数据载体
     * @param hasPaged // 是否已经分页
     */
    public PageInfo(int page, int pageSize, int total, List<T> list, boolean hasPaged) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        initPage(page, pageSize, total, list, hasPaged);
    }

    private void initPage(int pageIndex, int pageSize, int totalItemCount, List<T> data, boolean hasPaged) {
        // 计算总页数
        if (totalItemCount > 0 && pageSize > 0) {
            this.totalPageCount = totalItemCount / pageSize + 1;
        }
        // 截取当前页数据
        if (null == data) {
            this.list = new ArrayList<T>();
        } else if (data.isEmpty() || hasPaged) {
            this.list = data;
        } else {
            if (pageIndex == this.totalPageCount) { // 最后一页
                this.list = data.subList((pageIndex - 1) * pageSize, data.size());
            } else if (pageIndex < this.totalPageCount) { // 不是最后一页
                this.list = data.subList((pageIndex - 1) * pageSize, pageIndex * pageSize);
            } else { // 当前页超出总页数
                throw new IndexOutOfBoundsException("当前页数超出总页数范围");
            }
        }
    }
}
