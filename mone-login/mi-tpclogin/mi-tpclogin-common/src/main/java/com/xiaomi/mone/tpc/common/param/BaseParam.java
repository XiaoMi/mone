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
    private int page;
    private int pageSize;
    private boolean pager;

    public <T> PageDataVo<T> buildPageDataVo() {
        PageDataVo<T> pageDataVo = new PageDataVo<>();
        if (page <= 0 || !pager) {
            pageDataVo.setPage(1);
        } else {
            pageDataVo.setPage(page);
        }
        if (pageSize <= 0) {
            pageDataVo.setPageSize(100);
        } else if (pageSize <= 300) {
            pageDataVo.setPageSize(pageSize);
        } else {
            pageDataVo.setPageSize(300);
        }
        pageDataVo.setPager(pager);
        return pageDataVo;
    }

}
