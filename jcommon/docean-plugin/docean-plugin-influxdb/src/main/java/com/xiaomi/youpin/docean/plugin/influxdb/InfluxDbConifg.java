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

package com.xiaomi.youpin.docean.plugin.influxdb;

import lombok.Data;

/**
 * @author zhangjunyi
 * created on 2020/8/14 3:54 下午
 */
@Data
public class InfluxDbConifg {
    private String dbUrl;
    private String username;
    private String password;
    private String databaseName;
    private String retentionPolicy;
}
