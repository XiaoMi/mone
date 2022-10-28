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

package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;

import java.util.List;

/**
 * @author gaoyibo
 * @create 2019-04-29 18:13
 */
@Data
public class PluginDeleteParam {
    private Integer id;
    private String name;
    private String version;
    private String token;
    //用户名
    private String userName;


    private List<String> groupList;

    /**
     * 服务器列表
     */
    private List<String> addressList;


    private int accountId;


    private String projectId;
}
