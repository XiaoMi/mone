package com.xiaomi.youpin.tesla.rcurve.proxy.manager;

import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourceConfig;
import com.xiaomi.youpin.docean.plugin.dmesh.ds.Datasource;
import com.xiaomi.youpin.docean.plugin.redis.RedisDsConfig;
import com.xiaomi.youpin.docean.plugin.redis.RedisPlugin;
import com.xiaomi.youpin.docean.plugin.sql.SqlPlugin;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * 数据源管理器
 */
@Service
public class DsManager {

    @Resource
    private SqlPlugin sqlPlugin;

    @Resource
    private RedisPlugin redisPlugin;

    private ConcurrentHashMap<String, List<Datasource>> datasourceMap = new ConcurrentHashMap<>();

    public void add(String app, List<Datasource> datasourceList) {
        this.datasourceMap.put(app,datasourceList);
    }

    public void remove(String app) {
        List<Datasource> list = this.datasourceMap.get(app);
        if (null != list) {
            list.stream().forEach(it->{
                if (it.getDsType().equals("mysql")) {
                    DatasourceConfig config = new DatasourceConfig();
                    config.setName(app);
                    sqlPlugin.remove(config);
                }

                if (it.getDsType().equals("redis")) {
                    RedisDsConfig config = new RedisDsConfig();
                    config.setName(app);
                    redisPlugin.remove(config);
                }
            });
        }
        this.datasourceMap.remove(app);
    }

}
