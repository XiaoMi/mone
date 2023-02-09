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

package run.mone.docean.plugin.sidecar.processor.server;

import com.xiaomi.data.push.uds.context.UdsServerContext;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;
import run.mone.docean.plugin.sidecar.bo.SideCarApp;
import run.mone.docean.plugin.sidecar.manager.SidecarManager;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2022/6/19
 * sidecar解除注册
 */
@Component
@Slf4j
public class UnRegProcessor implements UdsProcessor<UdsCommand, UdsCommand> {

    @Resource
    private SidecarManager sidecarManager;

    @Override
    public UdsCommand processRequest(UdsCommand request) {
        UdsServerContext.ins().remove(request.getApp());
        SideCarApp sidcarApp = request.getData(SideCarApp.class);
        log.info("unreg sidecar app:{}", sidcarApp);
        sidecarManager.removeApp(sidcarApp);
        UdsCommand command = UdsCommand.createResponse(request);
        command.setData("success");
        return command;
    }

    @Override
    public String cmd() {
        return "unRegSideCar";
    }
}
