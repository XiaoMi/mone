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

package com.xiaomi.youpin.tesla.ip.common;

import com.intellij.util.messages.Topic;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/6 17:59
 */
public interface UltramanNotifier {

    public static Topic<UltramanNotifier> ULTRAMAN_ACTION_TOPIC = Topic.create("UltramanTopic", UltramanNotifier.class);

    void onEvent(UltramanEvent event);

}
