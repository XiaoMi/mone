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

package com.xiaomi.youpin.gateway.netty.filter;

import com.google.common.collect.Lists;
import com.xiaomi.common.perfcounter.PerfCounter;
import com.xiaomi.data.push.redis.Redis;
import com.xiaomi.youpin.gateway.cache.ApiRouteCache;
import com.xiaomi.youpin.gateway.common.FilterOrder;
import com.xiaomi.youpin.gateway.common.HttpRequestUtils;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.gateway.common.NetUtils;
import com.xiaomi.youpin.gateway.context.GatewayServerContext;
import com.xiaomi.youpin.gateway.filter.FilterContext;
import com.xiaomi.youpin.gateway.filter.Invoker;
import com.xiaomi.youpin.gateway.filter.RequestContext;
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.netty.GatewayInvoker;
import com.xiaomi.youpin.gateway.netty.transmit.connection.HttpHandler;
import com.xiaomi.youpin.gateway.protocol.http.HttpClient;
import com.xiaomi.youpin.gateway.service.ConfigService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.youpin.xiaomi.tesla.bo.ApiInfo;
import com.youpin.xiaomi.tesla.bo.GatewayFilterInfo;
import com.youpin.xiaomi.tesla.service.TeslaOpsService;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.xiaomi.youpin.gateway.filter.RequestFilter.DEFAULT_MACHINE_GROUP;

/**
 * @author goodjava@qq.com
 */
@Slf4j
@Component
public class RequestFilterChain {

    @Autowired
    private ApiRouteCache cache;

    @Autowired
    private ApplicationContext ac;

    @Autowired
    private FilterManager filterManager;

    @Autowired
    private ConfigService configService;

    @Autowired
    private Redis redis;

    @Value("${machine.group}")
    private String defaultMachineGroup;

    @Reference(check = false, group = "${dubbo.group}")
    private TeslaOpsService teslaOpsService;

    @Autowired
    private GatewayServerContext gatewayServerContext;


    private ReadWriteLock lock = new ReentrantReadWriteLock();

    private volatile Invoker lastInvoker;

    private CopyOnWriteArrayList<RequestFilter> filterList = new CopyOnWriteArrayList<>();


    private AtomicBoolean init = new AtomicBoolean(false);

    @PostConstruct
    public void init() {
        reload();
    }


    private boolean filterUserFilterWithGroup(RequestFilter it) {
        List<String> groups = it.getGroups();
        String machineGroup = getMachineGroup();
        boolean res = groups.stream().anyMatch(g -> g.equals(machineGroup) || g.equals(DEFAULT_MACHINE_GROUP));
        log.info("filterUserFilterWithGroup, filter_name:{}, groups: {}, machineGroup: {}, res: {}", it.getName(), groups, machineGroup, res);
        return res;
    }


    /**
     * 从新加载filter
     */
    public void reload() {
        log.info("reload filter");
        //获取系统定义的filter
        Map<String, RequestFilter> map = ac.getBeansOfType(RequestFilter.class);
        List<RequestFilter> list = new ArrayList<>(map.values());
        log.info("system filter size:{}", list.size());
        //获取用户定义的filter
        List<RequestFilter> userFilterList = filterManager.getUserFilterList().stream()
                .filter(it -> filterUserFilterWithGroup(it)).collect(Collectors.toList());
        log.info("user filter size:{}", userFilterList.size());

        list.addAll(userFilterList);
        list = sortFilterList(list);

        //停止所有filter
        stopFilter();

        filterList.clear();
        filterList.addAll(list);

        filterList.forEach(it -> log.info("filterName:{}", it.getName()));

        log.info("filter size:{}", list.size());

        init.set(false);

        long begin = System.currentTimeMillis();

        getLock();

        try {
            log.info("get write lock use time:{}", System.currentTimeMillis() - begin);
            Invoker last = new GatewayInvoker();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                log.info("init filter index:{}", i);
                RequestFilter filter = list.get(i);
                filter.init();
                Invoker next = last;
                last = (context, apiInfo, request) -> filter.doFilter(context, next, apiInfo, request);
            }
            log.info("init filter finish");
            this.lastInvoker = last;
        } finally {
            lock.writeLock().unlock();
            log.info("reload end");
        }

        init.set(true);
    }


    private void getLock() {
        int i = 0;
        while (true) {

            if (i == 5) {
                log.info("close future and http client begin");
                HttpHandler.closeFuture();
                HttpClient.closeClients();
                log.info("close future and http client finish");
            }

            boolean l = lock.writeLock().tryLock();
            if (!l) {
                log.warn("reload get lock error");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
            } else {
                break;
            }
            i++;
        }
    }

    private List<RequestFilter> sortFilterList(List<RequestFilter> list) {
        return list.stream().sorted((a, b) -> {
            Integer x = a.getClass().getAnnotation(FilterOrder.class).value();
            Integer y = b.getClass().getAnnotation(FilterOrder.class).value();
            return y.compareTo(x);
        }).collect(Collectors.toList());
    }

    private String getMachineGroup() {
        String machineGroup = gatewayServerContext.getAgentName().get();
        if (!StringUtils.isEmpty(machineGroup)) {
            return machineGroup;
        }
        try {
            Result<String> res = teslaOpsService.getMachineGroupByIp(NetUtils.getLocalHost());
            if (StringUtils.isEmpty(res.getData())) {
                return defaultMachineGroup;
            }
            return res.getData();
        } catch (Exception e) {
            log.error("RequestFilterChain.getMachineGroup, failed to getMachineGroup, msg:{}", e.getMessage());
            return defaultMachineGroup;
        }
    }


    private void stopFilter() {
        filterList.forEach(it -> {
            try {
                it.stop();
            } catch (Throwable ex) {
                log.warn("stop filter error:{} {}", it.getDef().getName(), ex.getMessage());
            }
        });
    }


    /**
     * 获取所有filter 列表的信息(包括公共的和私有的)
     *
     * @return
     */
    public List<GatewayFilterInfo> getFilterInfoList() {
        if (this.filterList.size() > 0) {
            return this.filterList.stream().map(it -> {
                GatewayFilterInfo info = new GatewayFilterInfo();
                String author = it.getDef().getAuthor();
                info.setName(Stream.of(it.toString(), author).collect(Collectors.joining(":")));
                return info;
            }).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    public FullHttpResponse doFilter(ApiInfo apiInfo, FullHttpRequest request, RequestContext context) {
        if (!init.get()) {
            return HttpResponseUtils.create(Result.fromException(new RuntimeException("filter init")));
        }
        long begin = System.currentTimeMillis();
        try {
            FilterContext ctx = new FilterContext(!configService.isCloseLogFilter());
            ctx.setHbegin(context.getBegin());
            ctx.setIp(context.getIp());
            ctx.setRequestContext(context);
            ctx.setTraceId(HttpRequestUtils.traceId(request));
            ctx.setBeginTime(begin);
            ctx.setCallId(context.getCallId());
            try {
                lock.readLock().lock();
                return this.lastInvoker.doInvoker(ctx, apiInfo, request);
            } finally {
                lock.readLock().unlock();
            }
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            PerfCounter.count("Tesla.Invoke", 1, System.currentTimeMillis() - begin);
        }
    }

}
