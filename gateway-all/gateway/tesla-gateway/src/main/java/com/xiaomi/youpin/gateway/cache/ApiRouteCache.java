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

package com.xiaomi.youpin.gateway.cache;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.gateway.RouteType;
import com.xiaomi.youpin.gateway.service.GatewayNamingService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.ApiInfoList;
import com.youpin.xiaomi.tesla.bo.ApiInfoReq;
import com.youpin.xiaomi.tesla.bo.Flag;
import com.youpin.xiaomi.tesla.service.TeslaOpsService;
import com.xiaomi.youpin.gateway.common.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


/**
 * @author goodjava@qq.com
 * api 路由 cache
 */
@Component
@Slf4j
public class ApiRouteCache {

    private static final ConcurrentHashMap<String, ApiInfo> cache = new ConcurrentHashMap<>();

    private static final AtomicBoolean haveCache = new AtomicBoolean(false);

    @Value("${cache.route.path}")
    private String cacheRoutePath;

    @Value(value = "${max.content.length}")
    @Getter
    private int maxContentLength;

    private static final String fileName = "api_route.cache";

    @DubboReference(check = false, interfaceClass = TeslaOpsService.class, group = "${manager.dubbo.group}", timeout = 4000)
    private TeslaOpsService teslaOpsService;


    @Autowired
    private GatewayNamingService gatewayNamingService;

    @Value("${api.info.list.size}")
    private int pageSize = 3000;

    /**
     * 租户概念(比如 中国区外网 中国区内网)
     */
    @Value("${tenant.id}")
    private String tenant;

    /**
     * 大多时候不用更新cache(除非在mananger哪里进行了路由更新),但第一次一定需要更新
     */
    private volatile long lastUpdateTime = Long.MIN_VALUE;

    @PostConstruct
    public void init() {
        new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(() -> {
            try {
                List<ApiInfo> list = Lists.newArrayList();
                int pageNum = 1;
                int state = 0;
                long lastTime = 0;
                while (true) {
                    ApiInfoReq req = new ApiInfoReq();
                    req.setPageNum(pageNum);
                    req.setPageSize(pageSize);
                    req.setTenant(tenant);
                    req.setLastUpdateTime(lastUpdateTime);
                    //支持租户概念的查询
                    Result<ApiInfoList> res = teslaOpsService.apiInfoList(req);
                    log.info("teslaOpsService.apiInfoList, get apiinfo, res code: {}", res.getCode());
                    if (res.getCode() == GeneralCodes.OK.getCode()) {
                        log.debug("teslaOpsService.apiInfoList, get apiinfo, res count: {}", res.getData().getTotal());
                        lastTime = res.getData().getLastUpdateTime();
                        //不需要更新
                        if (res.getData().getState() == 1) {
                            log.info("state:{}", res.getData().getState());
                            state = 1;
                            break;
                        }
                        res.getData().getList().stream().forEach(it -> {
                            log.debug("teslaOpsService.apiInfoList, get apiinfo: {}", it);
                            cache.put(it.getUrl(), it);
                            if (it.getRouteType().equals(RouteType.Http.type())) {
                                gatewayNamingService.subscribeWithPath(it.getPath(), it);
                            }
                        });
                        list.addAll(res.getData().getList());
                        if (res.getData().getList().size() == 0) {
                            break;
                        }
                        if (pageNum * pageSize >= res.getData().getTotal()) {
                            break;
                        }
                        if (res.getData().getList().size() > 0) {
                            pageNum++;
                        }
                    } else {
                        log.error("failed to get apiinfo, res: {}", res);
                        break;
                    }
                }
                this.lastUpdateTime = lastTime;
                if (state == 0) {
                    log.info("ApiRouteCache update size:{}", list.size());
                    Utils.writeFile(cacheRoutePath, fileName, new Gson().toJson(list));
                    haveCache.compareAndSet(false, true);
                    clearOldServiceNameMap(Result.success(list));
                }
            } catch (Exception ex) {
                log.error("ApiRouteCache.init, " + ex.getMessage(), ex);
                try {
                    if (haveCache.get() == false) {
                        String data = Utils.readFile(cacheRoutePath, fileName);
                        Type type = new TypeToken<List<ApiInfo>>() {
                        }.getType();
                        List<ApiInfo> apiInfos = new Gson().fromJson(data, type);
                        apiInfos.stream().forEach(it -> {
                            cache.put(it.getUrl(), it);
                        });
                        haveCache.compareAndSet(false, true);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

    /**
     * 清理那些现在已经不再需要同步的serviceName
     * 出现的原因是,下线一个服务的时候,这个网关并没有收到下线请求(属于意外的流程)
     *
     * @param res
     */
    private void clearOldServiceNameMap(Result<List<ApiInfo>> res) {
        try {
            //最新的serviceName set
            Set<String> newNameSet = getNewNameSet(res);
            //现在内存中需要同步的 serviceName set
            Set<String> serviceNameSet = gatewayNamingService.serviceNameSet();
            //解绑定
            Gson gson = new Gson();
            log.info("newNameSet: {},  serviceNameSet: {}", gson.toJson(newNameSet), gson.toJson(serviceNameSet));
            unsubscribe(newNameSet, serviceNameSet);
        } catch (Throwable ex) {
            log.info("clearOldServiceNameMap ex:{}", ex.getMessage());
        }
    }

    public static final Long Clean = -1L;

    private void unsubscribe(Set<String> newNameSet, Set<String> serviceNameSet) {
        serviceNameSet.stream().forEach(it -> {
            if (!newNameSet.contains(it)) {
                //-1L 代表直接清理
                gatewayNamingService.unsubscribe(it, Clean);
            }
        });
    }

    private Set<String> getNewNameSet(Result<List<ApiInfo>> res) {
        return res.getData().stream().map(it -> {
            Set<String> s = new HashSet<>();
            if (it.getRouteType().equals(RouteType.Http.type())) {
                String serviceName = GatewayNamingService.findServiceName(it.getPath());
                if (!StringUtils.isEmpty(serviceName)) {
                    s.add(serviceName);
                    if (it.isAllow(Flag.ALLOW_PREVIEW)) {
                        s.add(GatewayNamingService.getPreServiceName(serviceName));
                    }
                }
            }
            return s;
        }).flatMap(it -> it.stream()).collect(Collectors.toSet());
    }

    /**
     * 插入路由
     */
    public void insert(ApiInfo apiDO) {
        cache.put(apiDO.getUrl(), apiDO);
    }

    /**
     * 删除路由
     */
    public void delete(ApiInfo apiDO) {
        cache.remove(apiDO.getUrl());
    }


    /**
     * 修改路由
     */
    public void modify(ApiInfo apiDO) {
        Optional<String> optional = cache.entrySet().stream().filter(it -> it.getValue().getId().equals(apiDO.getId())).map(it -> it.getKey()).findFirst();
        if (optional.isPresent()) {
            cache.remove(optional.get());
        }
        cache.put(apiDO.getUrl(), apiDO);
    }


    /**
     * 获取路由
     *
     * @param url
     * @return
     */
    public ApiInfo get(String url) {
        return cache.get(url);
    }


    public void forEach(Function function) {
        cache.forEach((k, v) -> {
            if (v.isAllow(Flag.ALLOW_SCRIPT)) {
                Long id = v.getId();
                function.apply(id);
            }
        });
    }


}
