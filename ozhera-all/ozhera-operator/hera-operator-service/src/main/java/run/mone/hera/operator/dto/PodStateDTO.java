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
package run.mone.hera.operator.dto;

import io.fabric8.kubernetes.api.model.ContainerState;
import lombok.Data;

/**
 * @author shanwb
 * @date 2023-02-14
 */
@Data
public class PodStateDTO {

    private String podName;

    private String namespace;

    private String containerID;

    private String image;

    private String name;

    private Boolean ready;

    private Integer restartCount;

    private Boolean started;

    private ContainerState state;

    private ContainerState lastState;

}
