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

package com.xiaomi.data.push.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NacosConfig {

    private static final Logger logger = LoggerFactory.getLogger(NacosConfig.class);

    private String serverAddr;

    private String dataId;

    private String group = "DEFAULT_GROUP";

    @Setter
    private Properties properties;

    @Getter
    private ConfigService configService;

    @PostConstruct
    public void init() {
        String nacosAddr = System.getenv("nacos_addr");
        if (StringUtils.isNotEmpty(nacosAddr)) {
            serverAddr = nacosAddr;
        }

        if (serverAddr == null || serverAddr.length() == 0) {
            return;
        }


        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);

        if (null != this.properties) {
            this.properties.forEach((k, v) -> {
                properties.put(k, v);
            });
        }

        try {
            configService = NacosFactory.createConfigService(properties);
        } catch (Exception e) {
            logger.error("[NacosConfig.init] fail to init nacos config, serverAddr:{}, dataId: {}, group: {}, msg: {}", serverAddr, dataId, group, e.getCause());
        }
    }

    /**
     * 底层是http的,其实没什么可关闭的
     *
     * @return
     */
    public boolean close() {
        try {
            logger.info("nacos client close");
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * 获取配置
     *
     * @return
     */
    public String getConfig(String key) {
        try {
            Map<String, String> configMap = getConfigMap();
            return configMap.get(key);
        } catch (Exception e) {
            logger.error("[NacosConfig.getConfig] fail to get config, serverAddr:{}, dataId: {}, group: {}, msg: {}", serverAddr, dataId, group, e.getMessage());
            return "";
        }
    }

    public Map<String, String> getConfig() {
        try {
            return getConfigMap();
        } catch (Exception e) {
            logger.error("[NacosConfig.getConfig] fail to get config, serverAddr:{}, dataId: {}, group: {}, msg: {}", serverAddr, dataId, group, e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<String, String> getConfigMap() throws NacosException {
        return getConfigMap(this.dataId, this.group);
    }

    public Map<String, String> getConfigMap(String dataId, String group) throws NacosException {
        String content = configService.getConfig(dataId, group, 5000);
        Map<String, String> configMap = new HashMap<>();
        if (content != null && content.length() != 0) {
            String[] perConfig = content.split("\n|\r\n");
            for (String it : perConfig) {
                if (it == null || it.length() == 0 || it.startsWith("#")) {
                    continue;
                }
                //key-value中，value是可能含有'='，eg. a=b==
                int index = it.indexOf("=");
                if (index > -1) {
                    configMap.put(it.substring(0, index), it.substring(index + 1));
                }
            }
        }
        return configMap;
    }


    public String getConfigStr(String dataId, String group, long timeout) throws NacosException {
        String content = configService.getConfig(dataId, group, timeout);
        return content;
    }

    public String getConfigStr(long timeout) throws NacosException {
        String content = configService.getConfig(dataId, group, timeout);
        return content;
    }

    public boolean publishConfig(String dataId, String group, String content) throws NacosException {
        return configService.publishConfig(dataId, group, content);
    }

    public boolean publishConfig(String content) throws NacosException {
        return configService.publishConfig(dataId, group, content);
    }

    public boolean deleteConfig(String dataId, String group) throws NacosException {
        return configService.removeConfig(dataId, group);
    }

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
