package com.xiaomi.mone.log.manager.domain;

import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.mapper.MilogEsClusterMapper;
import com.xiaomi.mone.log.manager.model.pojo.LogEsClusterDO;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class EsCluster {
    @Resource
    private MilogEsClusterMapper esClusterMapper;

    /**
     * 获取ES客户端
     *
     * @param esClusterId
     * @return
     */
    public EsService getEsService(Long esClusterId) {
        if (esClusterId == null) {
            return null;
        }
        if (Ioc.ins().containsBean(Constant.ES_SERV_BEAN_PRE + esClusterId)) {
            return Ioc.ins().getBean(Constant.ES_SERV_BEAN_PRE + esClusterId);
        } else {
            return null;
        }
    }

    /**
     * 获取ES客户端
     *
     * @return
     */
    public EsService getEsService() {
        LogEsClusterDO curEsCluster = this.getCurEsCluster();
        if (curEsCluster == null) {
            return null;
        }
        if (Ioc.ins().containsBean(Constant.ES_SERV_BEAN_PRE + curEsCluster.getId())) {
            return Ioc.ins().getBean(Constant.ES_SERV_BEAN_PRE + curEsCluster.getId());
        } else {
            return null;
        }
    }

    /**
     * 获取当前用户对应的ES客户端
     *
     * @return
     */
    public LogEsClusterDO getCurEsCluster() {
        List<LogEsClusterDO> esClusterList = esClusterMapper.selectByTag(MoneUserContext.getCurrentUser().getZone());
        LogEsClusterDO cluster = esClusterList == null || esClusterList.isEmpty() ? null : esClusterList.get(0);
        log.info("[EsCluster.getCurEsCluster] user is {}, cluster is {}", MoneUserContext.getCurrentUser(), cluster.getName());
        return cluster;
    }

    public LogEsClusterDO getById(Long id) {
        return esClusterMapper.selectById(id);
    }

    public LogEsClusterDO getByRegion(String region) {
        if (StringUtils.isEmpty(region)) {
            return null;
        }
        LogEsClusterDO esClusterDO = esClusterMapper.selectByRegion(region);
        return esClusterDO;
    }

    // 获取所在区域支持的ES集群
    public LogEsClusterDO getByArea4China(String area) {
        if (StringUtils.isEmpty(area)) {
            return null;
        }
        List<LogEsClusterDO> clusterList = esClusterMapper.selectByArea(area);
        if (clusterList == null || clusterList.isEmpty()) {
            return null;
        }
        if (clusterList.size() > 1) {
            String zone = MoneUserContext.getCurrentUser().getZone();
            for (LogEsClusterDO clusterDO : clusterList) {
                if (Objects.equals(zone, clusterDO.getTag())) {
                    return clusterDO;
                }
            }
        }
        return clusterList.get(0);
    }
}
