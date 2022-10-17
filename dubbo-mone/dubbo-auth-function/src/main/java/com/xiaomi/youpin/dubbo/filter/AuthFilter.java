package com.xiaomi.youpin.dubbo.filter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.dubbo.common.NacosConfigUtils;
import org.apache.commons.lang3.StringUtils;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.utils.ConcurrentHashSet;
import org.apache.dubbo.rpc.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.apache.dubbo.common.Constants.*;
import static org.apache.dubbo.rpc.RpcException.FORBIDDEN_EXCEPTION;

@Activate(group = Constants.PROVIDER, order = -10000)
public class AuthFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    private static final String PREFIX_NACOS_DATA_ID = "dubbo-auth-app-name";
    private static final String DEFAULT_RPC_AUTH_NACOS_GROUP_ID = "DEFAULT_GROUP";
    private static final String RPC_AUTH = "rpc_auth";
    private static final String TESLA_AUTH_DEFAULT_KEY = "auth-key";
    private static final String TESLA_AUTH_FUNC_KEY = "auth-func-key";

    private static final NacosConfigUtils nacosConfigUtils = NacosConfigUtils.ins();

    /**
     * 这里需要注意并发问题
     */
    private static ConcurrentHashSet<String> authConsumerSet = new ConcurrentHashSet<>();
    private static ConcurrentHashMap<String, Collection<String>> methodAuthMap = new ConcurrentHashMap<>();

    private static final AtomicBoolean isInit = new AtomicBoolean(false);

    private void init(String providerAppName) {
        if (isInit.compareAndSet(false, true)) {
            logger.info("AuthFilter init");
            // 第一次需要是阻塞的,保证刷进去
            refreshAuth(providerAppName);
            // 10秒后执行,每隔10秒执行一次
            new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
                refreshAuth(providerAppName);
            }, 10, 10, TimeUnit.SECONDS);
        }
    }

    private void refreshAuth(String providerAppName) {
        try {
            String dataId = getNacosDataId(providerAppName);
            String authConfig = nacosConfigUtils.getConfig(dataId, DEFAULT_RPC_AUTH_NACOS_GROUP_ID, 3000);
            if (StringUtils.isEmpty(authConfig)
                    || (!authConfig.contains(TESLA_AUTH_DEFAULT_KEY) && !authConfig.contains(TESLA_AUTH_FUNC_KEY))) {
                logger.warn("AuthFilter.init, fail to get nacos value");
            } else {
                Collection<String> tmpSet = getSetFromNacos(authConfig).stream()
                        .filter(it -> StringUtils.isNotEmpty(it)).collect(Collectors.toSet());
                // 删除掉已经不在的
                Set<String> rem = new HashSet<>();
                authConsumerSet.forEach(v -> {
                    if (!tmpSet.contains(v)) {
                        rem.add(v);
                    }
                });
                authConsumerSet.removeAll(rem);

                // 添加新的
                tmpSet.forEach(v -> {
                    if (!authConsumerSet.contains(v)) {
                        authConsumerSet.add(v);
                    }
                });
                Set<String> frem = new HashSet<>();
                Map<String, Collection<String>> tmpMap = getFuncMapFromNacos(authConfig);
                // 删除掉已经不在的
                methodAuthMap.forEach((k, v) -> {
                    if (!tmpMap.containsKey(k)) {
                        frem.add(k);
                    }
                });
                frem.forEach(k -> {
                    methodAuthMap.remove(k);
                });
                // 添加新的
                methodAuthMap.putAll(tmpMap);
            }
        } catch (Exception ex) {
            logger.error("AuthFilter.init, get nacos value error", ex);
        }
    }

    private Collection<String> getSetFromNacos(String authConfig) {
        HashMap<String, Object> authMap = new Gson().fromJson(authConfig, new TypeToken<HashMap<String, Object>>() {
        }.getType());
        Collection<String> tmpSet = (Collection<String>) authMap.get(TESLA_AUTH_DEFAULT_KEY);
        if (tmpSet == null) {
            tmpSet = new HashSet<>();
        }
        return tmpSet;
    }

    private Map<String, Collection<String>> getFuncMapFromNacos(String authConfig) {
        HashMap<String, Object> authMap = new Gson().fromJson(authConfig, new TypeToken<HashMap<String, Object>>() {
        }.getType());
        Map<String, Collection<String>> tmpMap = (Map<String, Collection<String>>) authMap.get(TESLA_AUTH_FUNC_KEY);
        if (tmpMap == null) {
            tmpMap = new HashMap<>();
        }
        return tmpMap;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        if (invoker.getUrl().getParameter(RPC_AUTH, FALSE).equals(TRUE)) {
            // 需要鉴权
            String consumerAppName = invocation.getAttachment(APPLICATION_KEY, "");
            if (StringUtils.isEmpty(consumerAppName)) {
                consumerAppName = invocation.getAttachment("remote." + APPLICATION_KEY, "");
            }
            String providerAppName = invoker.getUrl().getParameter(APPLICATION_KEY, "");
            String providerAppMethodName = invoker.getUrl().getServiceInterface() + "|" + invocation.getMethodName();
            init(providerAppName);
            if (isAuth(authConsumerSet, consumerAppName)
                    && isAuth(methodAuthMap.get(providerAppMethodName), consumerAppName)) {
                // 鉴权结果：有权限
                Result res = invoker.invoke(invocation);
                return res;
            } else {
                // 鉴权结果：无权限
                logger.info("AuthFilter.invoke, invocation attachments: " + new Gson().toJson(invocation.getAttachments()));
                return new RpcResult(new RpcException(FORBIDDEN_EXCEPTION,
                        "no auth to invoke dubbo provider:" + providerAppName + ", consumer: " + consumerAppName));
            }
        }

        // 不需要鉴权
        Result res = invoker.invoke(invocation);
        return res;
    }

    private boolean isAuth(Collection<String> authConsumerSet, String consumerAppName) {
        // 没有配置,或者nacos出现问题,也放行请求
        if (authConsumerSet == null || authConsumerSet.size() == 0) {
            return true;
        }
        return authConsumerSet.contains(consumerAppName);
    }

    private String getNacosDataId(String appName) {
        if (StringUtils.isEmpty(appName)) {
            return "";
        }
        return PREFIX_NACOS_DATA_ID + "-" + appName;
    }

}
