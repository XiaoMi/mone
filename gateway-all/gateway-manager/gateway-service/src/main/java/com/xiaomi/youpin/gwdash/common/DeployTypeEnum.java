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

package com.xiaomi.youpin.gwdash.common;

import java.util.Arrays;
import java.util.Optional;

public enum DeployTypeEnum {

    MACHINE(1, "物理机"),
    DOCKER(2, "docker"),
    DOCKERFILE(3, "dockerfile"),
    WEB(4, "web");

    private int id;
    private String status;

    public int getId () {return id;}

    public String getStatus() { return status; }

    public static boolean isDocker (int deployType) {
        return deployType == DOCKER.getId()
                || deployType == DOCKERFILE.getId();
    }

    public static String getDeployName (int deployType) {
        Optional<DeployTypeEnum> optional = Arrays.stream(DeployTypeEnum.values())
                .filter(it -> it.getId() == deployType)
                .findFirst();
        if (optional.isPresent()) {
            return optional.get().getStatus();
        }
        return "未知";
    }

    DeployTypeEnum(int id, String status) {
        this.id = id;
        this.status = status;
    }
}
