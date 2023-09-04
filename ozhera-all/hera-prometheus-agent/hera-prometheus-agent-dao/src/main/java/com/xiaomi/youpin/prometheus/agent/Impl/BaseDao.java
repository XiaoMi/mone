package com.xiaomi.youpin.prometheus.agent.Impl;

import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseDao {

    @Autowired
    protected Dao dao;

    protected Pager buildPager(Integer pageNo, Integer pageSize) {
        Pager pager = new Pager();
        pager.setPageNumber(pageNo);
        pager.setPageSize(pageSize);
        return pager;
    }
}
