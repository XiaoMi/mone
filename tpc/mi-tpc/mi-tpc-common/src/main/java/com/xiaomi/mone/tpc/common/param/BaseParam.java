package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 10:23
 */
@Data
@ToString
public abstract class BaseParam implements ArgCheck, Serializable {

    private Long userId;
    private String account;
    private Integer userType;
    private Integer page;
    private Integer pageSize;
    private Boolean pager;

    public <T> PageDataVo<T> buildPageDataVo() {
      return buildPageDataVo(999);
    }

    public <T> PageDataVo<T> buildPageDataVo(int maxPageSize) {
        PageDataVo<T> pageDataVo = new PageDataVo<>();
        if (pager == null) {
            pager = false;
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 100;
        }
        if (page <= 0 || !pager) {
            pageDataVo.setPage(1);
        } else {
            pageDataVo.setPage(page);
        }
        if (pageSize <= 0) {
            pageDataVo.setPageSize(100);
        } else if (pageSize <= maxPageSize) {
            pageDataVo.setPageSize(pageSize);
        } else {
            pageDataVo.setPageSize(maxPageSize);
        }
        pageDataVo.setPager(pager);
        return pageDataVo;
    }

}
