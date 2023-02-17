package com.xiaomi.mone.log.manager.service.validation;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.model.bo.MiLogResource;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.*;

/**
 * @author wtt
 * @version 1.0
 * @description 资源管理参数校验
 * @date 2022/5/11 16:51
 */
@Slf4j
@Component
public class ResourceValidation {

    public String resourceOperateValid(MiLogResource miLogResource) {
        List<String> errorInfos = Lists.newArrayList();
        if (null == miLogResource.getOperateCode()) {
            errorInfos.add("操作编码 不能为空");
        }
        boolean operateBoolean = Objects.equals(OperateEnum.UPDATE_OPERATE.getCode(), miLogResource.getOperateCode()) ||
                Objects.equals(OperateEnum.DELETE_OPERATE.getCode(), miLogResource.getOperateCode());
        if (operateBoolean && null == miLogResource.getId()) {
            errorInfos.add("id 不能为空");
        }

        if (null == miLogResource.getResourceCode()) {
            errorInfos.add("资源编码 不能为空");
        }

        if (null != miLogResource.getResourceCode() &&
                null != miLogResource.getOperateCode() &&
                OperateEnum.DELETE_OPERATE.getCode().equals(miLogResource.getOperateCode())) {
            return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
        }

        if (MiddlewareEnum.ELASTICSEARCH.getCode().equals(miLogResource.getResourceCode())) {
            if (StringUtils.isBlank(miLogResource.getConWay())) {
                errorInfos.add("连接方式 不能为空");
            }
            if (StringUtils.isBlank(miLogResource.getClusterName())) {
                errorInfos.add("集群名称 不能为空");
            }
            if (Objects.equals(ES_CONWAY_PWD, miLogResource.getConWay()) &&
                    (StringUtils.isBlank(miLogResource.getAk()) || StringUtils.isBlank(miLogResource.getSk()))) {
//                errorInfos.add("当连接方式为pwd方式时，用户名、密码 不能为空");
            }
            if (Objects.equals(ES_CONWAY_TOKEN, miLogResource.getConWay()) &&
                    (StringUtils.isBlank(miLogResource.getEsToken()) || StringUtils.isBlank(miLogResource.getCatalog()) ||
                            StringUtils.isBlank(miLogResource.getDatabase()))) {
                errorInfos.add("当连接方式为token方式时，token、Catalog集群、库名 不能为空");
            }
        }
        if (StringUtils.isBlank(miLogResource.getAlias())) {
            errorInfos.add("别名 不能为空");
        }
        if (StringUtils.isBlank(miLogResource.getRegionEn())) {
            errorInfos.add("区域码 不能为空");
        }
        boolean esIndexExist = MiddlewareEnum.ELASTICSEARCH.getCode().equals(miLogResource.getResourceCode()) &&
                CollectionUtils.isEmpty(miLogResource.getMultipleEsIndex());
        if (esIndexExist) {
            errorInfos.add("ES索引信息 不能为空");
        }
        if(MiddlewareEnum.ROCKETMQ.getCode().equals(miLogResource.getResourceCode()) &&
                StringUtils.isEmpty(miLogResource.getClusterName())){
            errorInfos.add("MQ地址信息 不能为空");
        }
        return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
    }
}
