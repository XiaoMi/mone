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

package run.mone.mesh.processor.server;

import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;

import java.util.function.Function;

/**
 * @author goodjava@qq.com
 * @date 2022/7/3
 */
public class SimpleProcessor implements UdsProcessor<RpcCommand,RpcCommand> {

    private Function<RpcCommand,RpcCommand> function;

    public SimpleProcessor(Function<RpcCommand, RpcCommand> function) {
        this.function = function;
    }

    @Override
    public RpcCommand processRequest(RpcCommand rpcCommand) {
        String methodName = rpcCommand.getMethodName();
        switch (methodName) {
            case "ping":{
                RpcCommand res = new RpcCommand();
                res.setData("pong".getBytes());
                return res;
            }
            case "info":{
                RpcCommand res = new RpcCommand();
                res.setData("info".getBytes());
                return res;
            }
        }
        return function.apply(rpcCommand);
    }


    @Override
    public String cmd() {
        return "call";
    }
}
