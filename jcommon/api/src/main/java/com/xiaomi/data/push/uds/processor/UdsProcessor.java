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

package com.xiaomi.data.push.uds.processor;


/**
 * @author goodjava@qq.com
 */
public interface UdsProcessor<Request, Response> {


    Response processRequest(Request request);

    // 新增：判断是否为流式处理器
    default boolean isStreamProcessor() {
        return false;
    }

    // 新增：流式处理方法
    default void processStream(Request request, StreamCallback callback) {
        throw new UnsupportedOperationException("Stream processing not supported");
    }


    default String cmd() {
        return "";
    }

    default SideType side() {
        return SideType.server;
    }

    default int poolSize() {
        return 0;
    }


}
