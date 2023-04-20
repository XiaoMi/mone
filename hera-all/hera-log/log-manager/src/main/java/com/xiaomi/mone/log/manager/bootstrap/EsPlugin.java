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

@DOceanPlugin
@Slf4j
public class EsPlugin implements IPlugin {

    @Resource
    private MilogEsClusterMapper milogEsClusterMapper;

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
            log.error("init es cluster client error,address:{},userName:{},password:{},message:{}",
                    cluster.getAddr(), cluster.getUser(), cluster.getPwd(), e.getMessage(), e);
        }
    }


}
