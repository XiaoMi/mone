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

package com.xiaomi.data.push.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: goodjava
 * Date: 2020/5/24
 * Time: 12:32 PM
 */
@Data
@AllArgsConstructor
public class ClientInfo {

    private String name;
    private String ip;
    private int port;
    private String version;
}
