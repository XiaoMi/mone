package com.xiaomi.youpin.docean.plugin.dmesh.ms;

import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;
import com.xiaomi.youpin.docean.plugin.sql.ColumnRecord;

import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 1/10/21
 * 数据库操作
 */
@MeshMsService(interfaceClass = MySql.class, name = "mysql")
public interface MySql {

    List<Map<String, ColumnRecord>> query(String sql, String... params);

    int update(String sql, String... params);

}
