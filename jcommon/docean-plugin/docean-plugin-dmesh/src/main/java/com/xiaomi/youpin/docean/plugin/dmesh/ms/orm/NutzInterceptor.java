/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.plugin.dmesh.ms.orm;

import com.xiaomi.youpin.docean.plugin.db.interceptor.NutzDaoMeshInvoker;
import com.xiaomi.youpin.docean.plugin.db.po.DbRequest;
import com.xiaomi.youpin.docean.plugin.dmesh.ms.MySql;
import com.xiaomi.youpin.docean.plugin.sql.ColumnRecord;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;

import java.sql.Blob;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/1 10:32
 */
@Slf4j
public class NutzInterceptor extends OrmInterceptor implements NutzDaoMeshInvoker {

    @Setter
    private Map<String, MySql> mySql;

    @Override
    public List query(DbRequest request) {
        MySql mySql = getMysql(request.getDsName());
        String sql = request.getSql();
        log.info("nutz mesh interceptor select sql:{}", sql);
        List<Map<String, ColumnRecord>> list = mySql.query(sql);
        Entity entity = request.getEntity();
        List<MappingField> fields = entity.getMappingFields();
        return list.stream().map(it -> {
            Object obj = obj(entity.getType());
            fields.stream().forEach(f -> {
                ColumnRecord v = it.get(f.getColumnName());
                if (f.getType().equals(Blob.class)) {
                    setProperty(obj, f.getName(), value(v.getBytes(), f.getType()));
                } else {
                    setProperty(obj, f.getName(), value(v.getData(), f.getType()));
                }
            });
            return obj;
        }).collect(Collectors.toList());

    }

    private MySql getMysql(String name) {
        return this.mySql.get(name);
    }

    @Override
    public int update(DbRequest request) {
        log.info("nutz mesh interceptor update sql:{}", request.getSql());
        return getMysql(request.getDsName()).update(request.getSql(), new String[]{});
    }


}
