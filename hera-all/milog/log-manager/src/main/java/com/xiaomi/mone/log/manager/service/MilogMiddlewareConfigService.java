package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.api.model.bo.MiLogResource;
import com.xiaomi.mone.log.api.model.bo.ResourcePage;
import com.xiaomi.mone.log.api.model.vo.ResourceInfo;
import com.xiaomi.mone.log.api.model.vo.ResourceUserSimple;
import com.xiaomi.mone.log.common.Result;
import com.xiaomi.mone.log.manager.model.bo.MiddlewareAddParam;
import com.xiaomi.mone.log.manager.model.bo.MiddlewareQueryParam;
import com.xiaomi.mone.log.manager.model.bo.MiddlewareUpdateParam;
import com.xiaomi.mone.log.manager.model.page.PageInfo;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/22 11:41
 */
public interface MilogMiddlewareConfigService {
    PageInfo<MilogMiddlewareConfig> queryMiddlewareConfigPage(MiddlewareQueryParam middlewareQueryParam);

    Result addMiddlewareConfig(MiddlewareAddParam middlewareQueryParam);

    Result updateMiddlewareConfig(MiddlewareUpdateParam middlewareAddParam);

    Result deleteMiddlewareConfig(Long id);

    List<MilogMiddlewareConfig> queryMiddlewareConfigList();

    Result<MilogMiddlewareConfig> queryMiddlewareConfigById(Long id);

    PageInfo<ResourceInfo> queryResourceWithTab(ResourcePage resourcePage);

    Result<String> resourceOperate(MiLogResource miLogResource);

    String synchronousResourceLabel(Long id);

    ResourceUserSimple userResourceList(String regionCode, Integer logTypeCode);

    ResourceInfo resourceDetail(Integer resourceCode, Long id);

    MilogMiddlewareConfig queryMiddlewareConfigDefault(String regionCode);
}
