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

package com.xiaomi.youpin.tesla.plug.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.xiaomi.data.push.mongodb.MongoDb;
import com.xiaomi.data.push.nacos.NacosConfig;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.tesla.plug.common.SafeExecute;
import com.xiaomi.youpin.tesla.plug.ioc.IocInit;
import com.youpin.xiaomi.tesla.plugin.bo.DubboDatasource;
import com.youpin.xiaomi.tesla.plugin.bo.PluginDatasource;
import lombok.Getter;
import lombok.Setter;
import org.nutz.dao.impl.NutDao;

import javax.sql.DataSource;
import java.util.Optional;

/**
 * @author goodjava@qq.com
 */
public class TeslaConfig {

    /**
     * dubbo 的数据源
     */
    @Setter
    @Getter
    private DubboDatasource dubboDatasource;

    /**
     * 数据 的数据源
     */
    @Getter
    @Setter
    private DataSource dbDatasource;


    /**
     * 插件数据源
     */
    @Getter
    @Setter
    private PluginDatasource pluginDatasource;

    @Getter
    @Setter
    private Redis redis;

    /**
     * 动态配置 nacos_config
     */
    @Getter
    @Setter
    private NacosConfig nacosConfig;

    /**
     * mongodb 的数据源
     */
    @Getter
    @Setter
    private MongoDb mongoDb;


    @Getter
    @Setter
    private String pluginId;


    public boolean closeRedis() {
        return Optional.ofNullable(TeslaConfig.ins().getRedis()).isPresent() ? SafeExecute.run(() ->
                TeslaConfig.ins().getRedis().close()
        ) : true;

    }

    public boolean closeMogodb() {
        return Optional.ofNullable(TeslaConfig.ins().getMongoDb()).isPresent() ? SafeExecute.run(() -> {
            return TeslaConfig.ins().getMongoDb().close();
        }) : true;
    }

    public boolean closeNacos() {
        return Optional.ofNullable(TeslaConfig.ins().getNacosConfig()).isPresent() ? SafeExecute.run(() -> TeslaConfig.ins().getNacosConfig().close()
        ) : true;
    }

    public boolean closeMysql() {
        return Optional.ofNullable(TeslaConfig.ins().getDbDatasource()).isPresent() ? SafeExecute.run(() -> {
            ComboPooledDataSource ds = (ComboPooledDataSource) IocInit.ins().getIoc().get(NutDao.class, "dao").getDataSource();
            ds.close();
            return true;
        }) : true;
    }


    private static final class LazyHolder {
        private static final TeslaConfig ins = new TeslaConfig();
    }


    public static final TeslaConfig ins() {
        return LazyHolder.ins;
    }
}
