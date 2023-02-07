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
import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;
import run.mone.docean.plugin.sidecar.bo.SideCarApp;
import run.mone.docean.plugin.sidecar.manager.RegService;
import run.mone.docean.plugin.sidecar.manager.SidecarManager;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2022/6/19
 * sidecar 注册过来
 */
@Component
@Slf4j
public class RegProcessor implements UdsProcessor<RpcCommand, RpcCommand> {

    @Resource
    private SidecarManager sidecarManager;

    @Resource
    private Ioc ioc;

    @Override
    public RpcCommand processRequest(RpcCommand request) {
        SideCarApp sidecarApp = request.getData(SideCarApp.class);
        log.info("reg sidecar app:{}", sidecarApp);

        RegService regService = getRegService();
        if (null != regService) {
            //系统填入一些信息
            regService.putFassInfo(sidecarApp);
        }

        sidecarManager.putApp(sidecarApp);
        UdsCommand command = UdsCommand.createResponse(request);

        if (request instanceof UdsCommand) {
            UdsCommand uds = (UdsCommand) request;
            UdsServerContext.ins().put(request.getApp(), uds.getChannel());
        }

        if (null != regService) {
            Object res = regService.reg(request);
            command.setData(res);
            return command;
        }
        command.setData("{\"data\":\"success\"}");
        return command;
    }

    private RegService getRegService() {
        if (null != ioc) {
            RegService regService = ioc.getBean(RegService.class.getName(), null);
            return regService;
        }
        return null;
    }

    @Override
    public String cmd() {
        return "regSideCar";
    }
}
