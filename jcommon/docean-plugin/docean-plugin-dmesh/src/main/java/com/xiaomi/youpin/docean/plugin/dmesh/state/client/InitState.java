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

package com.xiaomi.youpin.docean.plugin.dmesh.state.client;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.xiaomi.data.push.uds.UdsClient;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourceConfig;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourcePlugin;
import com.xiaomi.youpin.docean.plugin.dmesh.ds.Datasource;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/13 12:03
 * 连接上server了进入init状态
 * 发送数据源给server(完成动态注册)
 */
@Component
@Slf4j
public class InitState extends BaseState {


    @Resource
    private ClientFsm fsm;

    @Value("$uds_app")
    private String app;


    @Override
    public void execute() {
        log.info("client init state");
        sendInitMsg(Ioc.ins());
        fsm.change(Ioc.ins().getBean(PingState.class));
    }


    private void sendInitMsg(Ioc ioc) {
        UdsClient client = ioc.getBean(UdsClient.class);
        UdsCommand req = UdsCommand.createRequest();
        req.setCmd("updateServerList");
        req.setServiceName("init");
        req.setData(new Gson().toJson(getDatasourceMap(ioc)));
        req.setApp(this.app);
        client.oneWay(req);
    }

    /**
     * 获取数据源
     *
     * @param ioc
     * @return
     */
    private Map<String, Datasource> getDatasourceMap(Ioc ioc) {
        Config config = ioc.getBean(Config.class);

        Map<String, Datasource> m = Maps.newHashMap();

        if (ioc.containsBean(DatasourcePlugin.DB_NAMES)) {
            List<String> dsList = ioc.getBean(DatasourcePlugin.DB_NAMES);
            dsList.stream().map(name -> {
                DatasourceConfig dc = ioc.getBean(name + "_config");
                Datasource ds = new Datasource();
                ds.setName(dc.getName());
                ds.setDsType("mysql");
                ds.setDataSourceUserName(dc.getDataSourceUserName());
                ds.setDataSourcePasswd(dc.getDataSourcePasswd());
                ds.setDataSourceUrl(dc.getDataSourceUrl());
                ds.setDefaultInitialPoolSize(dc.getDefaultInitialPoolSize());
                ds.setDefaultMaxPoolSize(dc.getDefaultMaxPoolSize());
                ds.setDefaultMinPoolSize(dc.getDefaultMinPoolSize());
                return ds;
            }).forEach(dc -> m.put(dc.getName(), dc));
        }


        config.forEach((k, v) -> {
            if (k.toString().startsWith("ds_redis_")) {
                if (!m.containsKey("redis")) {
                    Datasource ds = new Datasource();
                    ds.setDsType("redis");
                    m.put("redis", ds);
                }
                m.get("redis").set(k.toString().substring("ds_redis_".length()), v.toString());
            }
        });
        return m;
    }
}
