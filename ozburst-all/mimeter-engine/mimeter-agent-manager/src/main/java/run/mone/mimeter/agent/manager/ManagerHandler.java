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

package run.mone.mimeter.agent.manager;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.impl.NutDao;
import run.mone.mimeter.agent.manager.bo.MibenchTask;
import run.mone.mimeter.dashboard.bo.agent.*;
import run.mone.mimeter.engine.agent.bo.data.AgentInfoDTO;
import run.mone.mimeter.engine.agent.bo.data.HttpResult;
import run.mone.mimeter.engine.agent.bo.task.CancelType;
import run.mone.mimeter.engine.agent.bo.task.ChangeQpsReq;
import run.mone.mimeter.engine.agent.bo.task.Task;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2022/5/19
 */
@Controller
@Slf4j
public class ManagerHandler {

    @Resource
    private ManagerService managerService;

    @Resource(name = "$daoName:mibench_st_db", description = "mysql")
    private NutDao dao;

    @RequestMapping(path = "/version")
    public String version() {
        return "2022-10-1 1.0.0";
    }

    @RequestMapping(path = "/system")
    public String system() {
        return System.getProperty("os.name");
    }


    @RequestMapping(path = "/test")
    public int test() {
        MibenchTask dagTask = dao.fetch(MibenchTask.class, 61381);
        log.info("test log info:{}",dagTask);
        return dagTask.getIncreasePercent();
    }

    /**
     * 获取agent 列表
     * @return
     */
    @RequestMapping(path = "/agent/list")
    public List<AgentInfoDTO> agentList() {
        return managerService.agents().stream().map(it -> {
            AgentInfoDTO info = new AgentInfoDTO();
            info.setIp(it.remoteAddress().toString());
            return info;
        }).collect(Collectors.toList());
    }

    @RequestMapping(path = "/agent/addr/list")
    public List<Map<String, List<String>>> agentAddrList() {
        Map<String, List<String>> result = new HashMap<>();
        List<String> ipList = new ArrayList<>();

        managerService.agents().forEach(it -> {
            try {
                String addrStr = it.remoteAddress().toString();
                String[] addrArr = addrStr.split("/");
                String ipAndPort = addrArr[addrArr.length - 1];
                String[] ipAndPortArr = ipAndPort.split(":");
                ipList.add(ipAndPortArr[0] + ":8080");
            } catch (Exception ex) {
                log.error("[ManagerHandler]agentAddrList parse agent's ip and port error; " + ex.getMessage());
            }
        });
        result.put("targets", ipList);
        return Collections.singletonList(result);
    }

    /**
     * 手动直接修改单台发压机hosts文件
     */
    @RequestMapping(path = "/manual/edit/hosts",timeout = 10000)
    public HttpResult manualEditHosts(HostForAgentReq req) throws Exception {
        return managerService.manualEditHosts(req);
    }

    /**
     * 修改发压机hosts文件
     */
    @RequestMapping(path = "/edit/hosts",timeout = 10000)
    public HttpResult editHosts(DomainApplyReq domainApplyReq) throws Exception {
        return managerService.editHosts(domainApplyReq);
    }

    /**
     * 同步发压机hosts域名绑定
     */
    @RequestMapping(path = "/sync/hosts",timeout = 10000)
    public HttpResult syncHosts(SyncHostsReq syncHostsReq) throws Exception {
        return managerService.syncHosts(syncHostsReq);
    }

    /**
     * 删除发压机hosts文件某项域名绑定
     */
    @RequestMapping(path = "/del/hosts",timeout = 10000)
    public HttpResult delHosts(DelHostForAgentsReq req) throws Exception {
        return managerService.delHosts(req);
    }

    /**
     * 删除发压机hosts文件某项域名绑定
     */
    @RequestMapping(path = "/load/hosts",timeout = 10000)
    public HttpResult loadHosts(LoadHostsFileReq req) throws Exception {
        return managerService.loadHostsFile(req);
    }

    /**
     * 基于场景创建执行任务
     */
    @RequestMapping(path = "/submit/task",timeout = 10000)
    public HttpResult submitTask(Task task) throws Exception {
        return managerService.submitTask(task);
    }

    /**
     * 手动停止某次压测任务
     *
     */
    @RequestMapping(path = "/cancel/task")
    public HttpResult cancelTask(Task task) {
        task.setCancelType(CancelType.Manual.code);
        return managerService.cancelTask(task);
    }

    /**
     * 调整指定场景某次压测任务的qps
     *
     */
    @RequestMapping(path = "/task/manualUpdateQps")
    public HttpResult manualUpdateQps(ChangeQpsReq req) {
        return managerService.manualUpdateQps(req);
    }

    @RequestMapping(path = "/view")
    public String view() {
        try {
            return new String(Files.readAllBytes(Paths.get("/Users/dongzhenxing/Documents/Mi/Projects/mibench-engine/mibench-agent-manager/src/main/resources/upload.html")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
