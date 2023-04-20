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

package com.xiaomi.mone.log.stream.sink;

import com.xiaomi.mone.es.EsProcessor;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author goodjava@qq.com
 * @Date 2021/8/20 10:05
 */
@Component
@Slf4j
@Data
public class TeslaSink {

    private EsProcessor esProcessor;

    @Value(value = "$exIndex", defaultValue = "youpin_insert_test-")
    private String esIndex = "";

    @Value(value = "$departmentEnvFlag", defaultValue = "china")
    private String departmentEnvFlag = "";

    public boolean execute(Map<String, Object> map) {
        if (null != map && map.containsKey("appName") && map.get("appName") != null && map.get("appName").equals("tesla")) {
            String message = map.get("message").toString();
            if (message.startsWith("$%^,")) {
                try {
                    String[] array = message.split(",");
                    log.info("tesla sink array:{}", Arrays.toString(array));
//                    String index = esIndex + new SimpleDateFormat("yyyy.MM.dd").format(new Date());
                    String index = esIndex + DateTimeFormatter.ofPattern("yyyy.MM.dd").format(LocalDateTime.now());
                    String url = array[1];
                    String applicationName = array[2];
                    String traceId = array[3];
                    String group = array[4];
                    String ip = array[5];
                    String time = array[6];
                    String department = array[7];
                    department = StringUtils.isEmpty(department) ? "china" : department;
                    esProcessor.bulkInsert(index, getEsData(url, applicationName, traceId, group, ip, time, department));
                    log.info("insert ex_index:{},department:{}", index, department);
                } catch (Throwable ex) {
                    log.error(String.format("es send message error,message:%s", message), ex);
                }
                return true;
            }
        }
        return false;
    }

    private Map<String, Object> getEsData(String url, String serviceName, String traceId, String group, String ip, String time, String department) {
        Map<String, Object> map = new HashMap<>(7);
        map.put("domain", department + "_tesla");
        map.put("group", group);
        map.put("host", ip);
        map.put("url", url);
        map.put("serviceName", serviceName);
        map.put("traceId", traceId);
        map.put("timestamp", time);
        return map;
    }

}
