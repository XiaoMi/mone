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

package com.xiaomi.youpin.tesla.agent.service;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 */
public class LabelService {


    public static final String HTTP_PORT = "http_port";

    public static final String DUBBO_PORT = "dubbo_port";

    public static final String GSON_DUBBO_PORT = "gson_dubbo_port";

    /**
     * 第三方需要开的端口
     */
    public static final String THIRD_PORT = "third_port";

    public static final String LOG_PATH = "log_path";

    public static final String PYTHON = "python";

    public static final String IMAGE_NAME = "image_name";

    public static final String NET_MODULE = "net_module";

    /**
     * 是否支持远程debug
     */
    public static final String DEBUG = "debug";

    public static final String DEBUG_ARGUMENTS = "-Xdebug";
    public static final String DEBUG_ARGUMENTS2 = "-Xrunjdwp:transport=dt_socket,suspend=n,server=y,address=";

    /**
     * 保留的内存限制(总内存-mem_limit = java堆内存)
     */
    public static final String MEM_LIMIT = "mem_limit";

    public static final String MAX_DIRECTMEMORY_SIZE = "max_directmemory_size";

    public static final String MMS = "mms";

    /**
     * 用来挂载磁盘
     */
    public static final String VOLUME = "volume";


    /**
     * serviceName
     * Labels
     */
    private ConcurrentHashMap<String, Map<String, String>> labels = new ConcurrentHashMap<>();


    private LabelService() {

    }

    private static class LazyHolder {
        private static final LabelService ins = new LabelService();
    }

    public static final LabelService ins() {
        return LazyHolder.ins;
    }


    public void updateLables(String name, Map<String, String> labels) {
        this.labels.put(name, labels);
    }


    public Map<String, String> getLabels(String name) {
        return labels.get(name);
    }


    public String getLabelValue(String labels, String key) {
        return getLabelValue(labels, key, "");
    }

    public String getLabelValue(String labels, String key, String defaultValue) {
        if (StringUtils.isEmpty(labels) || StringUtils.isEmpty(key)) {
            return "";
        }
        return Arrays.stream(labels.split(",")).map(it -> it.split("=")).filter(it -> it[0].equals(key)).map(it -> it[1]).findAny().orElse(defaultValue);
    }


}
