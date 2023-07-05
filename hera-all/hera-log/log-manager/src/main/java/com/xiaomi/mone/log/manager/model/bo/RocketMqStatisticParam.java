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
package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

import java.util.List;

@Data
public class RocketMqStatisticParam {
    private List<String> topicList;
    private List<String> groupList;
    /**
     * 参数可选值：(*或broker组名)，若未null，则返回所有broker组之和
     */
    private String broker;
    private Long begin;
    private Long end;
    /**
     * 参数可选值:(avg、sum、min、max)   默认avg
     */
    private String aggregator;

    private String client;
    private String metirc;
}
