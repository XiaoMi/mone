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
package com.xiaomi.mone.log.manager.service;

/**
 * @author wtt
 * @version 1.0
 * @description 配置同步nacos的中间配置
 * @date 2021/7/19 16:10
 */
public interface MilogConfigNacosService {

    String STREAM_PREFIX = "stream";

    String SPACE_PREFIX = "space";

    void publishStreamConfig(Long spaceId, Integer type, Integer projectType, String motorRoomEn);

    void publishNameSpaceConfig(String motorRoomEn, Long spaceId, Long storeId, Long tailId, Integer type, String changeType);

    void removeStreamConfig(Long id);
}
