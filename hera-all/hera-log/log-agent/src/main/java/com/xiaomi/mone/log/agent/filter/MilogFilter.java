package com.xiaomi.mone.log.agent.filter;


import com.xiaomi.mone.log.api.model.meta.FilterConf;

public interface MilogFilter {
    void doFilter(Invoker next);

    boolean init(FilterConf conf);
}
