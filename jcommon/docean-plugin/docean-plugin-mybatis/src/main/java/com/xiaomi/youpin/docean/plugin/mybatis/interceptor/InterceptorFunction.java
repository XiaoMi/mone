package com.xiaomi.youpin.docean.plugin.mybatis.interceptor;

import com.xiaomi.youpin.docean.plugin.mybatis.bo.QueryParam;
import com.xiaomi.youpin.docean.plugin.mybatis.bo.QueryResult;
import com.xiaomi.youpin.docean.plugin.mybatis.bo.UpdateParam;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/27 16:39
 */
public interface InterceptorFunction {

    QueryResult query(QueryParam param);

    int update(UpdateParam param);

}
