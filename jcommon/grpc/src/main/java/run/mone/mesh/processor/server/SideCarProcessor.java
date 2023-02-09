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
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2022/7/2
 */
@Slf4j
public class SideCarProcessor implements UdsProcessor<RpcCommand,RpcCommand> {
    @Override
    public RpcCommand processRequest(RpcCommand rpcCommand) {
        log.info("sidecar");
        RpcCommand command = new RpcCommand();
        command.setData("sidecar".getBytes());
        return command;
    }

    @Override
    public String cmd() {
        return "sidecar";
    }
}
