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

package run.mone.api;

import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author goodjava@qq.com
 * @Date 2022/6/22 15:34
 */
public interface IClient<C extends RpcCommand> {

    void start(String str);

    C call(C rpcCommand);

    default Object callServer(Object request) {
        return null;
    }

    ConcurrentHashMap<String, UdsProcessor<C, C>> getProcessorMap();

    default void putProcessor(UdsProcessor processor) {

    }

    default void shutdown() {

    }

    default Address address() {
        return null;
    }

    default boolean isShutdown() {
        return false;
    }


}
