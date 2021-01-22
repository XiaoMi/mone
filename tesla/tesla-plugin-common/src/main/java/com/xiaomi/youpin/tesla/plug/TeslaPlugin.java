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

package com.xiaomi.youpin.tesla.plug;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.xiaomi.data.push.mongodb.MongoDb;
import com.xiaomi.data.push.nacos.NacosConfig;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.tesla.plug.common.PluginCommonVersion;
import com.xiaomi.youpin.tesla.plug.config.TeslaConfig;
import com.xiaomi.youpin.tesla.plug.datasource.DatasourceUtils;
import com.xiaomi.youpin.tesla.plug.datasource.MongoDbUtils;
import com.xiaomi.youpin.tesla.plug.datasource.NacosUtils;
import com.xiaomi.youpin.tesla.plug.datasource.RedisUtils;
import com.xiaomi.youpin.tesla.plug.ioc.IocInit;
import com.youpin.xiaomi.tesla.plugin.bo.*;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.impl.NutDao;
import org.pf4j.Plugin;
import org.pf4j.PluginContext;
import org.pf4j.PluginException;
import org.pf4j.PluginWrapper;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * 插件
 */
@Slf4j
public class TeslaPlugin extends Plugin {


    private String pluginId;


    private String contextPath;


    public TeslaPlugin(PluginWrapper wrapper) {
        super(wrapper);
        this.pluginId = wrapper.getPluginId();
    }


    public TeslaPlugin(PluginWrapper wrapper, String contextPath) {
        super(wrapper);
        this.pluginId = wrapper.getPluginId();
        this.contextPath = contextPath;
    }


    @Override
    public void start() throws PluginException {
        super.start();
        log.info("plugin start:{} version:{}", pluginId, new PluginCommonVersion());

        TeslaConfig.ins().setPluginId(this.pluginId);

        PluginContext context = this.getWrapper().getDescriptor().getPluginContext(contextPath);


        //获取到数据源
        String datasourceMapStr = context.getAttachment().get("dataSourceMap").toString();
        log.info("datasource:{}", datasourceMapStr);
        TeslaDatasourceMap tdm = new Gson().fromJson(datasourceMapStr, TeslaDatasourceMap.class);


        //容错
        if (null == tdm) {
            tdm = new TeslaDatasourceMap();
        }

        final Gson gson = new Gson();
        tdm.getMap().entrySet().forEach((e) -> {
            //如果数据源是mysql
            if (e.getKey().startsWith("mysql")) {
                DbDatasource dbDatasource = gson.fromJson(e.getValue(), DbDatasource.class);
                try {
                    DataSource ds = DatasourceUtils.createDataSource(dbDatasource.getDriverClass(),
                            dbDatasource.getDataSourceUrl(),
                            dbDatasource.getDataSourceUserName(),
                            dbDatasource.getDataSourcePasswd(),
                            dbDatasource.getDefaultInitialPoolSize(),
                            dbDatasource.getDefaultMaxPoolSize(),
                            dbDatasource.getDefaultMinPoolSize()
                    );
                    TeslaConfig.ins().setDbDatasource(ds);
                } catch (Throwable ex) {
                    log.error(ex.getMessage(), ex);
                }
            } else if (e.getKey().startsWith("dubbo")) {
                DubboDatasource dubboDatasource = gson.fromJson(e.getValue(), DubboDatasource.class);
                TeslaConfig.ins().setDubboDatasource(dubboDatasource.copy());
            } else if (e.getKey().startsWith("plugin")) {
                PluginDatasource pluginDatasource = gson.fromJson(e.getValue(), PluginDatasource.class);
                TeslaConfig.ins().setPluginDatasource(pluginDatasource.copy());
            } else if (e.getKey().startsWith("redis")) {
                RedisDatasource redisDatasource = gson.fromJson(e.getValue(), RedisDatasource.class);
                try {
                    Redis redis = RedisUtils.createRedis(redisDatasource.getRedisHosts(), redisDatasource.getRedisType());
                    TeslaConfig.ins().setRedis(redis);
                } catch (Throwable ex) {
                    log.error(ex.getMessage());
                }
            } else if (e.getKey().startsWith("nacos")) {
                NacosDatasource nacosDatasource = gson.fromJson(e.getValue(), NacosDatasource.class);
                try {
                    NacosConfig nacosConfig = NacosUtils.createNacos(nacosDatasource.getServerAddr(), nacosDatasource.getDataId(), nacosDatasource.getGroup());
                    TeslaConfig.ins().setNacosConfig(nacosConfig);
                } catch (Throwable ex) {
                    log.error(ex.getMessage(), ex);
                }
            } else if (e.getKey().startsWith("mongoDb")) {
                MongoDbDatasource mongoDbDatasource = gson.fromJson(e.getValue(), MongoDbDatasource.class);
                try {
                    MongoDb mongoDb = MongoDbUtils.createMongoDb(mongoDbDatasource.getMongoDbHosts(), mongoDbDatasource.getMongoDbDatabase());
                    TeslaConfig.ins().setMongoDb(mongoDb);
                } catch (Throwable ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        });


        IocInit.ins();

        //mysql
        if (TeslaConfig.ins().getDbDatasource() != null) {
            NutDao dao = IocInit.ins().getIoc().get(NutDao.class, "dao");
            dao.setDataSource(TeslaConfig.ins().getDbDatasource());
        }

        //nacos
        if (TeslaConfig.ins().getNacosConfig() != null) {
            IocInit.ins().getIoc().addBean("config", TeslaConfig.ins().getNacosConfig());
        }

        //redis
        if (TeslaConfig.ins().getRedis() != null) {
            IocInit.ins().getIoc().addBean("redis", TeslaConfig.ins().getRedis());
        }

        //mongo
        if (TeslaConfig.ins().getMongoDb() != null) {
            IocInit.ins().getIoc().addBean("mongo", TeslaConfig.ins().getMongoDb());
        }

        log.info("plugin:{} start version:{}", this.pluginId, new TeslaPluginVersion());
    }


    @Override
    public void stop() throws PluginException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        super.stop();
        log.info("plugin:{} stop begin", this.pluginId);

        //mysql
        boolean closeMysqlRes = TeslaConfig.ins().closeMysql();
        log.info("close mysql:{}", closeMysqlRes);

        //mongodb
        boolean closeMongoRes = TeslaConfig.ins().closeMogodb();
        log.info("close mongod:{}", closeMongoRes);

        //nacos
        boolean closeNacosRes = TeslaConfig.ins().closeNacos();
        log.info("close nacos:{}", closeNacosRes);

        //redis
        boolean closeRedisRes = TeslaConfig.ins().closeRedis();
        log.info("close redis:{}", closeRedisRes);

        //这里会阻塞住,知道真到真正关闭(rpc的关闭等...)
        IocInit.ins().destory();
        log.info("plugin:{} stop end use time:{}", this.pluginId, stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }


    @Override
    public void delete() throws PluginException {
        super.delete();
        log.info("plugin:{} delete", this.pluginId);
    }


}
