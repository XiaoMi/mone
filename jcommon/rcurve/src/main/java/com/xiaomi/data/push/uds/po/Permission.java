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

package com.xiaomi.data.push.uds.po;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/23 15:14
 */
public class Permission {

    /**
     * 是否是request
     */
    public static final int IS_REQUEST= 1 << 0;

    /**
     * 是否是oneway
     */
    public static final int IS_ONWAY= 1 << 1;

}
