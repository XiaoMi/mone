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

package com.xiaomi.youpin.tesla.agent.exception;

/**
 * User: goodjava
 * Date: 2020/5/24
 * Time: 3:49 PM
 */
public class AgentException extends RuntimeException{

    public AgentException() {
    }

    public AgentException(String message) {
        super(message);
    }

    public AgentException(Throwable cause) {
        super(cause);
    }
}
