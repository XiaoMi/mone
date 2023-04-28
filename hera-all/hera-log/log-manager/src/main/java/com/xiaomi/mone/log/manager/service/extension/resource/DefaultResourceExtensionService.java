package com.xiaomi.mone.log.manager.service.extension.resource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.model.bo.MiLogResource;
import com.xiaomi.mone.log.api.model.vo.ResourceUserSimple;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.model.pojo.MilogEsClusterDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogEsIndexDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.service.impl.RocketMqConfigService;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.YES;
import static com.xiaomi.mone.log.manager.service.extension.resource.ResourceExtensionService.DEFAULT_RESOURCE_EXTENSION_SERVICE_KEY;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/11 10:01
 */
@Service(name = DEFAULT_RESOURCE_EXTENSION_SERVICE_KEY)
@Slf4j
public class DefaultResourceExtensionService implements ResourceExtensionService {

    @Resource
    private RocketMqConfigService mqConfigService;

    @Override
    public List<MilogMiddlewareConfig> userShowAuthority(List<MilogMiddlewareConfig> configList) {
        return configList;
    }

    @Override
    public void filterEsQueryWrapper(QueryWrapper<?> queryWrapper) {

    }

    @Override
    public List<String> generateResourceLabels(String id) {
        return Lists.newArrayList();
    }

    @Override
    public void addResourcePreProcessing(List<String> resourceLabels, MiLogResource miLogResource) {

    }

    @Override
    public void addEsResourcePreProcessing(MilogEsClusterDO esClusterDO) {
        if (MoneUserContext.getCurrentUser().getIsAdmin()) {
            esClusterDO.setIsDefault(YES);
        }
    }

    @Override
    public void addResourceMiddleProcessing(MiLogResource miLogResource) {
        mqConfigService.createCommonTagTopic(miLogResource.getAk(), miLogResource.getSk(), miLogResource.getClusterName(),
                miLogResource.getServiceUrl(), StringUtils.EMPTY, miLogResource.getOrgId(),
                miLogResource.getBrokerName());
    }

    @Override
    public void addResourcePostProcessing(MilogMiddlewareConfig milogMiddlewareConfig) {
        /**
         * 目前默认共用一个配置
         */
        if (MoneUserContext.getCurrentUser().getIsAdmin()) {
            milogMiddlewareConfig.setIsDefault(YES);
        }
    }

    @Override
    public boolean userResourceListPre(Integer logTypeCode) {
        return false;
    }

    @Override
    public List<MilogMiddlewareConfig> currentUserConfigFilter(List<MilogMiddlewareConfig> middlewareConfigs) {
        if (MoneUserContext.getCurrentUser().getIsAdmin()) {
            return middlewareConfigs.stream().filter(milogMiddlewareConfig -> Objects.equals(YES, milogMiddlewareConfig.getIsDefault())).collect(Collectors.toList());
        }
        return middlewareConfigs.stream().filter(milogMiddlewareConfig -> !Objects.equals(YES, milogMiddlewareConfig.getIsDefault())).collect(Collectors.toList());
    }

    @Override
    public boolean resourceNotRequiredInit(Integer logTypeCode, List<MilogMiddlewareConfig> middlewareMqConfigs, List<MilogMiddlewareConfig> middlewareEsConfigs, List<MilogEsIndexDO> esIndexDOList) {
        return CollectionUtils.isNotEmpty(middlewareMqConfigs) &&
                CollectionUtils.isNotEmpty(middlewareEsConfigs) &&
                CollectionUtils.isNotEmpty(esIndexDOList);
    }

    @Override
    public boolean resourceShowStatusFlag(ResourceUserSimple configResource) {
        if (MoneUserContext.getCurrentUser().getIsAdmin()) {
            configResource.setShowFlag(Boolean.FALSE);
            return false;
        }
        configResource.setShowFlag(Boolean.TRUE);
        return Boolean.TRUE;
    }

    @Override
    public Integer getResourceCode() {
        return MiddlewareEnum.ROCKETMQ.getCode();
    }

}
