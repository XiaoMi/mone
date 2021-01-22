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

package com.xiaomi.youpin.docean.plugin.dmesh.ms;

import com.xiaomi.youpin.docean.plugin.dmesh.anno.MeshMsService;

import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 1/10/21
 * 数据库操作
 */
@MeshMsService(interfaceClass = MySql.class, name = "mysql")
public interface MySql {

    List<Map<String, Object>> query(String sql, String... params);

    int update(String sql, String... params);

}
