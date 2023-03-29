package com.xiaomi.mone.tpc.login.vo;


import com.xiaomi.mone.tpc.login.util.UserUtil;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 10:23
 */
public abstract class BaseParam implements ArgCheck, Serializable {

    private Long userId;
    private String account;
    private Integer userType;
    private String fullAccount;
    private Integer page;
    private Integer pageSize;
    private Boolean pager;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    /**
     * 生成全局唯一账号
     * @return
     */
    public String getFullAccount() {
        return UserUtil.getFullAccount(account, userType);
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Boolean getPager() {
        return pager;
    }

    public void setPager(Boolean pager) {
        this.pager = pager;
    }

    public <T> PageDataVo<T> buildPageDataVo() {
      return buildPageDataVo(100);
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
