/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.bootstrap;

import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.mapper.MilogEsClusterMapper;
import com.xiaomi.mone.log.manager.model.pojo.MilogEsClusterDO;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

import static com.xiaomi.mone.log.common.Constant.GSON;

@DOceanPlugin
@Slf4j
public class EsPlugin implements IPlugin {

    @Resource
    private MilogEsClusterMapper milogEsClusterMapper;

    private static final String ADDR_PREFIX = "http://";

    @Override
    public void init() {
        log.info("es init start");
        List<MilogEsClusterDO> esClusterList = milogEsClusterMapper.selectAll();
        if (esClusterList == null || esClusterList.isEmpty()) {
            log.warn("no es client");
            return;
        }
        for (MilogEsClusterDO cluster : esClusterList) {
            buildEsClient(cluster);
        }
    }

    public void buildEsClient(MilogEsClusterDO cluster) {
        try {
            EsService esService;
            checkAddrUpdate(cluster);
            switch (cluster.getConWay()) {
                case Constant.ES_CONWAY_PWD:
                    esService = new EsService(cluster.getAddr(), cluster.getUser(), cluster.getPwd());
                    Ioc.ins().putBean(Constant.ES_SERV_BEAN_PRE + cluster.getId(), esService);
                    break;
                case Constant.ES_CONWAY_TOKEN:
                    esService = new EsService(cluster.getAddr(), cluster.getToken(), cluster.getDtCatalog(), cluster.getDtDatabase());
                    Ioc.ins().putBean(Constant.ES_SERV_BEAN_PRE + cluster.getId(), esService);
                    break;
                default:
                    log.warn("ES集群录入异常:[{}]", cluster);
                    return;
            }
            Ioc.ins().putBean(Constant.ES_SERV_BEAN_PRE + cluster.getId(), esService);
            log.info("ES客户端[{}]生成成功[{}]", cluster.getName(), Constant.ES_SERV_BEAN_PRE + cluster.getId());
        } catch (Exception e) {
            log.error("init es cluster client error,cluster{}", GSON.toJson(cluster), e);
        }
    }

    private void checkAddrUpdate(MilogEsClusterDO cluster) {
        String addr = cluster.getAddr();
        if (addr.startsWith(ADDR_PREFIX)) {
            cluster.setAddr(addr.substring(ADDR_PREFIX.length() + 1));
        }
    }
}
