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

package com.xiaomi.youpin.docean.plugin.dmesh.ds;

import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * @author goodjava@qq.com
 */
@Data
public class Datasource {

    private String name;

    private String dsType;

    private String driverClass;

    private Integer defaultInitialPoolSize;

    private Integer defaultMaxPoolSize;

    private Integer defaultMinPoolSize;

    private String dataSourceUrl;

    private String dataSourceUserName;

    private String dataSourcePasswd;

    private String hosts;

    private String type;


    @SneakyThrows
    public void set(String key, String value) {
        Class<? extends Datasource> clazz = this.getClass();
        Field field = clazz.getDeclaredField(key);
        field.setAccessible(true);
        if (field.getType().equals(Integer.class)) {
            field.set(this, Integer.valueOf(value).intValue());
        } else {
            field.set(this, value);
        }
    }
}
