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

package com.xiaomi.youpin.gateway.service;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.xiaomi.youpin.gateway.nacos.ConfigType;
import com.xiaomi.youpin.gateway.nacos.Nacos;
import com.xiaomi.youpin.gateway.nacos.NacosConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动态配置(会动态更新)
 *
 * @author dingpei
 */
@Service
@Slf4j
public class ConfigService implements Nacos {

    @Value("${nacos.config.addrs}")
    private String nacosConfigAddrs;

    private com.alibaba.nacos.api.config.ConfigService configService;

    private static Pattern PATTERN = Pattern.compile("\\s*|\\t|\\r|\\n");
    private static Cache<NacosConfig, String> CONFIG_CACHE = CacheBuilder.newBuilder()
            .maximumSize(500)
            //60秒过期
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build();

    @PostConstruct
    public void init() throws NacosException {
        log.info("init ConfigService:{}", nacosConfigAddrs);
        Properties properties = new Properties();
        properties.put("serverAddr", nacosConfigAddrs);
        configService = NacosFactory.createConfigService(properties);
    }

    @Override
    public String getConfig(NacosConfig nacosConfig) throws NacosException {
        String content = configService.getConfig(nacosConfig.getDataId(), nacosConfig.getGroupId(), nacosConfig.getTimeout());
        return content;
    }

    @Override
    public String getConfigValue(NacosConfig nacosConfig, ConfigType configType, String key) {
        try {
            String content = CONFIG_CACHE.getIfPresent(nacosConfig);
            if (null == content || content.trim().length() == 0) {
                content = this.getConfig(nacosConfig);
                CONFIG_CACHE.put(nacosConfig, content);
            }
            if (null == content || content.trim().length() == 0) {
                return null;
            }

            switch (configType){
                case PROP:
                case TEXT:
                    Map<String, String> prop = parse2Map(content);
                    return prop.get(key);
                default:
                    throw new RuntimeException("not support configType:"+configType);
            }
        } catch (NacosException e) {
            log.error("NacosException:{}", e);
            throw new RuntimeException(e);
        }
    }

    @NacosValue(value = "${needParseCode:false}", autoRefreshed = true)
    private boolean needParseCode = true;

    @NacosValue(value = "${usePrometheusFilter:true}", autoRefreshed = true)
    private boolean usePrometheusFilter = true;

    @NacosValue(value = "${useCatFilter:false}", autoRefreshed = true)
    private boolean useCatFilter = true;

    @NacosValue(value = "${returnDubboLog:false}", autoRefreshed = true)
    private boolean returnDubboLog = false;

    @NacosValue(value = "${closeLimitFilter:false}", autoRefreshed = true)
    private boolean closeLimitFilter = false;

    @NacosValue(value = "${closeLogFilter:false}", autoRefreshed = true)
    private boolean closeLogFilter = false;

    @NacosValue(value = "${openTrafficRecord:true}", autoRefreshed = true)
    private boolean openTrafficRecord = true;

    @NacosValue(value = "${openTrafficRecordGroup:}", autoRefreshed = true)
    private String openTrafficRecordGroup = "";

    @NacosValue(value = "${allowLogByUrlId:}", autoRefreshed = true)
    private String allowLogByUrlId = "";

    @NacosValue(value = "${pingSleepTime:500}", autoRefreshed = true)
    private int pingSleepTime = 500;

    @NacosValue(value = "${teslaTimeout:5000}", autoRefreshed = true)
    private int teslaTimeout = 5000;

    @NacosValue(value = "${teslaFileTimeout:60000}", autoRefreshed = true)
    private int teslaFileTimeout = 60000;


    @NacosValue(value = "${openJeager:true}", autoRefreshed = true)
    private boolean openJeager = true;

    /**
     * 系统插件路径
     */
    @NacosValue(value = "${systemFilterPath:/tmp/filter/}", autoRefreshed = true)
    private String systemFilterPath = "/tmp/filter/";

    /**
     * 是否允许用户级别 filter
     */
    @NacosValue(value = "${allowUserFilter:true}", autoRefreshed = true)
    private boolean allowUserFilter = true;

    /**
     * 允许访问的mtop组
     */
    @NacosValue(value = "${allowMtopGroup:}", autoRefreshed = true)
    private String allowMtopGroup = "";

    /**
     * 允许打开access.log
     */
    @NacosValue(value = "${allowAccessLog:false}", autoRefreshed = true)
    private boolean allowAccessLog = false;

    /**
     * 是否允许用户级别 filter
     */
    @NacosValue(value = "${allowDirectBuf:true}", autoRefreshed = true)
    private boolean allowDirectBuf = true;

    public String getAllowMtopGroup() {
        return allowMtopGroup;
    }

    public boolean isReturnDubboLog() {
        return returnDubboLog;
    }

    public boolean isNeedParseCode() {
        return needParseCode;
    }

    public boolean isCloseLimitFilter() {
        return closeLimitFilter;
    }

    public boolean isCloseLogFilter() {
        return closeLogFilter;
    }

    public String getSystemFilterPath() {
        return systemFilterPath;
    }

    public boolean isAllowUserFilter() {
        return allowUserFilter;
    }

    public boolean isAllowDirectBuf() {
        return allowDirectBuf;
    }

    public boolean isOpenTrafficRecord() {
        return openTrafficRecord;
    }

    public boolean isOpenTrafficRecordGroup(String group) {
        if (StringUtils.isNotEmpty(openTrafficRecordGroup)) {
            String[] groups = openTrafficRecordGroup.split(",");
            if (Arrays.asList(groups).contains(group)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean isUsePrometheusFilter() {
        return usePrometheusFilter;
    }

    public List<String> getAllowLogByUrlId() {
        if (StringUtils.isEmpty(allowLogByUrlId)) {
            return null;
        }
        String[] arr = allowLogByUrlId.split(",");
        return Arrays.asList(arr);
    }

    public int getPingSleepTime() {
        return pingSleepTime;
    }

    public int getTeslaTimeout() {
        return teslaTimeout;
    }

    public int getTeslaFileTimeout() {
        return teslaFileTimeout;
    }

    private Map<String, String> parse2Map(String content) {
        Map<String, String> prop = new HashMap<>();
        String[] arr = content.split("\n");
        for (String s : arr) {
            String[] kv = s.split("=");
            if (kv.length == 2) {
                prop.put(replaceBlank(kv[0]), replaceBlank(kv[1]));
            }
        }
        return prop;
    }

    private static String replaceBlank(String s) {
        if (null == s) {
            return null;
        }

        Matcher m = PATTERN.matcher(s);
        return m.replaceAll("");
    }

    public boolean isAllowAccessLog() {
        return allowAccessLog;
    }

    public boolean isOpenJeager() {
        return openJeager;
    }

    public boolean isUseCatFilter() {
        return useCatFilter;
    }
}
