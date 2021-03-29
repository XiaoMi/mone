package com.xiaomi.mone.autumn.ex

import org.nutz.dao.Dao
import org.nutz.dao.Sqls
import org.nutz.dao.sql.Sql

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/22 15:22
 */
class MysqlEx {


    def call(Map m, Dao dao) {
        String cmd = m.get("cmd")
        Map map = m.get("params")
        String sql = m.get("sql")
        if (cmd.equals("query")) {
            Sql nsql = Sqls.create(sql)
            map.each { it ->
                nsql.params().set(it.getKey(), it.getValue())
            }
            nsql.setCallback(Sqls.callback.maps());
            List<Map> res = dao.execute(nsql).getList(Map.class);
            return res
        }
        if (cmd.equals("update")) {
            Sql nsql = Sqls.create(sql);
            map.each { nsql.params().set(it.getKey(), it.getValue()) }
            nsql.setCallback(Sqls.callback.integer());
            dao.execute(sql);
            return nsql.getUpdateCount();
        }
        return "dont support this mysql cmd"
    }

}
