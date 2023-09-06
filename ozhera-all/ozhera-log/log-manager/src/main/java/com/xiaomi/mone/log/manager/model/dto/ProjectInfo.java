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
package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/15 11:50
 */
@Data
public class ProjectInfo implements Serializable {
    private Long id;

    private String name;

    private String mioneEnv;

    private String desc;

    private long ctime;

    private long utime;

    private String domain;
    /**
     * Project Importance 1. Level 1 Project 2.2 Level Project Level 3.3 Project
     */
    private Integer importLevel;

    private Long iamTreeId;
}
