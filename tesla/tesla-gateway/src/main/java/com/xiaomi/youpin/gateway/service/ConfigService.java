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
import com.xiaomi.youpin.gateway.nacos.Nacos;
import com.xiaomi.youpin.gateway.nacos.NacosConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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


    @NacosValue(value = "${needParseCode:false}", autoRefreshed = true)
    private boolean needParseCode = true;

    @NacosValue(value = "${returnDubboLog:false}", autoRefreshed = true)
    private boolean returnDubboLog = false;

    @NacosValue(value = "${closeLimitFilter:false}", autoRefreshed = true)
    private boolean closeLimitFilter = false;

    @NacosValue(value = "${closeLogFilter:false}", autoRefreshed = true)
    private boolean closeLogFilter = false;

    @NacosValue(value = "${openTrafficRecord:true}", autoRefreshed = true)
    private boolean openTrafficRecord = true;

    @NacosValue(value = "${allowLogByUrlId:}", autoRefreshed = true)
    private String allowLogByUrlId = "";

    @NacosValue(value = "${pingSleepTime:500}", autoRefreshed = true)
    private int pingSleepTime = 500;

    @NacosValue(value = "${teslaTimeout:5000}", autoRefreshed = true)
    private int teslaTimeout = 5000;

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
     * 是否允许用户级别 filter
     */
    @NacosValue(value = "${allowDirectBuf:true}", autoRefreshed = true)
    private boolean allowDirectBuf = true;

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
}
