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
package com.xiaomi.youpin.tesla.agent.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.youpin.docker.YpDockerClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * @author dingpei
 * @date 2021/9/7
 */
@Slf4j
public class DockerMachinePowerOffOrOn {

    public static final String CONTAINER_LIST_PATH = Config.ins().get("container_list_path", "");

    public static final String CONTAINER_LIST_FILE_NAME = "containerIds";

    public static void powerOff() {
        log.info("docker machine power off start...");
        List<String> containerIds = YpDockerClient.ins().powerOff();

        if (containerIds == null || containerIds.size() == 0) {
            log.info("powerOff.containerIds is empty");
            return;
        }

        FileUtils.writeFile(CONTAINER_LIST_PATH, CONTAINER_LIST_FILE_NAME, new Gson().toJson(containerIds));

        log.info("docker machine power off end, containerIds: {}", containerIds);

    }


    public static void powerOn() {
        log.info("docker machine power on start...");

        String str = FileUtils.readFile(CONTAINER_LIST_PATH, CONTAINER_LIST_FILE_NAME);
        List<String> containerIds = new Gson().fromJson(str, new TypeToken<List<String>>() {}.getType());

        if (containerIds == null || containerIds.size() == 0) {
            log.info("powerOn.containerIds is empty");
            return;
        }

        YpDockerClient.ins().powerOn(containerIds);

        log.info("docker machine power on end, containerIds: {}", containerIds);

    }


}
