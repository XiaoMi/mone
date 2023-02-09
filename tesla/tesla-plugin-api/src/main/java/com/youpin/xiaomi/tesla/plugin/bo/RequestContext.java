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

package com.youpin.xiaomi.tesla.plugin.bo;

import lombok.Data;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * @author goodjava@qq.com
 */
@Data
public class RequestContext {

    private User user;

    private Consumer<User> regConsumer;

    private Consumer<Message> sendConsumer;

    private Function<String, Map<String, User>> groupFunction;

    private Consumer<User> pingConsumer;

    /**
     * 用户注册
     *
     * @param user
     */
    public void reg(User user) {
        regConsumer.accept(user);
    }

    /**
     * 发送消息
     *
     * @param message
     */
    public void send(Message message) {
        sendConsumer.accept(message);
    }


    public void ping(User user) {
        pingConsumer.accept(user);
    }


    public Map<String, User> group(String groupName) {
        return groupFunction.apply(groupName);
    }


}
