package com.xiaomi.youpin.docean.plugin.dmesh.ms.orm;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.plugin.dmesh.ms.MySql;
import com.xiaomi.youpin.docean.plugin.mybatis.bo.QueryParam;
import com.xiaomi.youpin.docean.plugin.mybatis.bo.QueryResult;
import com.xiaomi.youpin.docean.plugin.mybatis.bo.UpdateParam;
import com.xiaomi.youpin.docean.plugin.mybatis.interceptor.InterceptorFunction;
import com.xiaomi.youpin.docean.plugin.sql.ColumnRecord;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.ResultMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/27 16:00
 */
@Slf4j
@Data
public class MyBatisInterceptor extends OrmInterceptor implements InterceptorFunction {

    @Setter
    private Map<String, MySql> mySql;

    @Override
    public QueryResult query(QueryParam queryParam) {
        log.info("mesh mybatis query:{}", queryParam.getSql());
        List<Map<String, ColumnRecord>> res = getMysql(queryParam.getDsName()).query(queryParam.getSql(), queryParam.getParams());
        QueryResult result = new QueryResult();
        List<ResultMapping> mapping = queryParam.getMappings();
        ArrayList resultList = Lists.newArrayList();
        res.stream().forEach(row -> {
            Object obj = obj(queryParam.getType());
            mapping.stream().forEach(m -> {
                String column = m.getColumn();
                String property = m.getProperty();
                if (m.getJavaType().equals(byte[].class)) {
                    setProperty(obj, property, value(row.get(column).getBytes(), m.getJavaType()));
                } else {
                    setProperty(obj, property, value(row.get(column).getData(), m.getJavaType()));
                }
            });
            resultList.add(obj);
        });
        result.setList(resultList);
        return result;
    }

    @Override
    public int update(UpdateParam param) {
        log.info("mesh mybatis update sql:{}", param.getSql());
        return getMysql(param.getDsName()).update(param.getSql(), param.getParams());
    }

    private MySql getMysql(String name) {
        return this.mySql.get(name);
    }
}
