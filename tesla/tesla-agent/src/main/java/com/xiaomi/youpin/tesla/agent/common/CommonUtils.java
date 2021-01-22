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

package com.xiaomi.youpin.tesla.agent.common;

import com.google.gson.Gson;
import com.xiaomi.data.push.rpc.RpcClient;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
import com.xiaomi.youpin.tesla.agent.po.DockerReq;
import com.xiaomi.youpin.tesla.agent.po.NotifyMsg;
import com.xiaomi.youpin.tesla.agent.service.LabelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Slf4j
public class CommonUtils {

    public static void sleep(int num) {
        try {
            TimeUnit.SECONDS.sleep(num);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }


    public static int fileSize(String filePath) {
        try {
            return (int) (new File(filePath).length() / 1024);
        } catch (Throwable ex) {
            log.warn(ex.getMessage());
            return 0;
        }
    }

    public static void mkdir(String filePath) {
        try {
            FileUtils.forceMkdir(new File(filePath));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    public static boolean matchImage(String name, String imageName) {
        return Pattern.matches("^(cr\\.d\\.xiaomi\\.net/mixiao(-st)?/)?" + Pattern.quote(name) + "[:-][0-9]+$", imageName);
    }

    public static String getName(String containerName) {
        int lastIndex = containerName.lastIndexOf("-");
        if (lastIndex != -1) {
            return containerName.substring(0, lastIndex);
        }
        return containerName;
    }

    public static int downloadJarFile(String path, String downloadKey, String jarName) {
        String filePath = path + File.separator + jarName;
        File file = new File(filePath);
        log.info("download jar name:{} file:{}", jarName, file);
        YoupinKsYunService.ins().ksyunService.getFileByKey(downloadKey, file);
        int size = CommonUtils.fileSize(filePath);
        log.info("download jar name:{} finish {}", jarName, size);
        return size;
    }


    public static void notifyServer(RpcClient client, NotifyMsg notifyMsg) {
        try {
            log.info("notify:{}", new Gson().toJson(notifyMsg));
            RemotingCommand msg = RemotingCommand.createMsgPackRequest(AgentCmd.notifyMsgReq, notifyMsg);
            msg.setTimeout(2000L);
            client.sendMessage(msg);
        } catch (Throwable ex) {
            log.warn("notify error:{}", ex.getMessage());
        }
    }

    /**
     * 是否支持远程debug
     *
     * @param req
     * @return
     */
    public static boolean supportDebug(DockerReq req) {
        return Config.ins().get("debug","false").equals("true")
                && StringUtils.isNotEmpty(LabelService.ins().getLabelValue(req.getLabels(), LabelService.DEBUG));
    }

}
