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

package com.xiaomi.youpin.gateway.filter;

public class SwitchFlag {

    /**
     * gson不进行HTML转码
     */
    public static final int SWITCH_GSON_DISABLE_HTML_ESCAPING = 1 << 0;

    /**
     * 不进行gson转换
     */
    public static final int SWITCH_DIRECT_TO_STRING = 1 << 1;
}
