/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.hera.operator.service;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import run.mone.hera.operator.common.FileUtils;

import java.io.File;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/2/23 6:02 PM
 */
@Service
@Slf4j
public class NacosService {

    public void publishNacosConfig(String nacosAddr, String directory) {
        try {
            ConfigService configService = ConfigFactory.createConfigService(nacosAddr);
            String path = this.getClass().getResource(directory).getPath();
            File dir = new File(path);
            File[] files = dir.listFiles();
            for (File file : files) {
                String name = file.getName();
                String[] split = name.substring(0, name.indexOf(".properties")).split("_#_");
                configService.publishConfig(split[0], split[1], FileUtils.fileToString(file));
            }
        } catch (Throwable t) {
            log.error("publish nacos config error", t);
        }
    }

}
