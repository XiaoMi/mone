///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.controller;
//
//
//import com.dianping.cat.Cat;
//import com.google.common.base.Stopwatch;
//import com.google.gson.Gson;
//import com.xiaomi.aegis.utils.AegisFacade;
//import com.xiaomi.aegis.vo.UserInfoVO;
//import com.xiaomi.youpin.gwdash.bo.MachineBo;
//import com.xiaomi.youpin.gwdash.bo.MachineSshBo;
//import com.xiaomi.youpin.gwdash.bo.RemotingCommandBo;
//import com.xiaomi.youpin.gwdash.bo.ShellParams;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.service.AgentManagerServiceWrapper;
//import com.xiaomi.youpin.gwdash.service.MachineManagementService;
//import com.xiaomi.youpin.gwdash.service.UserService;
//import com.xiaomi.youpin.hermes.bo.RoleBo;
//import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
//import com.xiaomi.youpin.tesla.agent.cmd.AgentCmd;
//import com.xiaomi.youpin.tesla.agent.po.ShellReq;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.PostConstruct;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.*;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.stream.Collectors;
//
///**
// * @author tsingfu
// */
//@RestController
//@Slf4j
//public class DeploymentAgentController {
//
//    @Autowired
//    private AgentManagerServiceWrapper agentManager;
//
//    @Autowired
//    private MachineManagementService machineManagementService;
//
//    @Autowired
//    private UserService userService;
//
//
//    @Value("${hermes.project.name}")
//    private String projectName;
//
//
//    private AtomicReference<List<MachineSshBo>> machineSshList = new AtomicReference<>(new LinkedList<>());
//
//    @PostConstruct
//    public void init() {
//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
//            Stopwatch sw = Stopwatch.createStarted();
//            try {
//                loadMachineList();
//            } catch (Exception e) {
//                log.error(e.toString());
//                Cat.logError(e);
//            }
//            log.info("load machine list use time:{}", sw.elapsed(TimeUnit.MILLISECONDS));
//        }, 0, 20, TimeUnit.SECONDS);
//    }
//
//
//    @RequestMapping(value = "/api/dpagent/list", method = RequestMethod.GET)
//    public Result<Map<String, Object>> getDpAgentList() {
//        Map<String, Object> map = new HashMap<>();
//        map.put("list", agentManager.clientList());
//        return new Result(0, "", map);
//    }
//
//    @RequestMapping(value = "/api/dpagent/updateMachineList", method = RequestMethod.GET)
//    public Result<Boolean> updateMachineList(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        loadMachineList();
//        return Result.success(true);
//    }
//
//    @RequestMapping(value = "/api/dpagent/getAgentDetailList", method = RequestMethod.GET)
//    public Result<Map<String, Object>> getAgentDetailList(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        UserInfoVO user = AegisFacade.getUserInfo(request);
//        if (null == user) {
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        Map<String, Object> map = new HashMap<>();
//        List<MachineSshBo> machineList = machineSshList.get();
//        log.info("AgentDetailList machineList: {}", machineList);
//        List<MachineSshBo> resList = getUserMachine(user.getUser(), machineList);
//        log.info("AgentDetailList resList: {}", resList);
//        map.put("list", resList);
//
//        return new Result(0, "", map);
//    }
//
//    private void loadMachineList() {
//        List<String> agentList = agentManager.clientList();
//        log.info("AgentDetailList agentList: {}", agentList);
//        List<MachineBo> mList = machineManagementService.queryMachineList();
//        List<MachineSshBo> machineList = agentList.stream().map(it -> {
//            String[] ipAndPort = StringUtils.replace(it, "/", "").split(":");
//            MachineSshBo machineSshBo = new MachineSshBo();
//            machineSshBo.setAgentPort(ipAndPort[1]);
//            Optional<MachineBo> op = mList.stream().filter(mb -> mb.getIp().equals(ipAndPort[0])).findAny();
//            if (!op.isPresent()) {
//                log.info("error ip:{}", ipAndPort[0]);
//                return null;
//            }
//            MachineBo machineBo = op.get();
//            BeanUtils.copyProperties(machineBo, machineSshBo);
//            machineSshBo.setPrepareLabels(null);
//            machineSshBo.setLabels(null);
//            return machineSshBo;
//        }).filter(x -> x != null).collect(Collectors.toList());
//        machineSshList.set(machineList);
//    }
//
//
//    /**
//     * @param username
//     * @param liveMachineList agent 接口查询到机器列表
//     * @return 获取到aegent 列表不一定和数据库记录的一致，可能已经挂了
//     * 这里从活着的agent 筛选出用户关联的机器
//     * 本地环境 因为本机没有关联到项目上所以查不到，关联上了就能看到
//     */
//    private List<MachineSshBo> getUserMachine(String username, List<MachineSshBo> liveMachineList) {
//        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
//        queryRoleRequest.setProjectName(projectName);
//        queryRoleRequest.setUserName(username);
//        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
//        List<String> roleNames = roles.stream().map(it -> it.getName()).collect(Collectors.toList());
//        boolean isAdmin = roleNames.contains("admin");
//
//        List<MachineSshBo> res;
//        if (isAdmin) {
//            res = liveMachineList;
//        } else {
//            List<MachineBo> userMachines = machineManagementService.queryMachinesByUsername(username);
//            log.info("machineManagementService:{} ", userMachines);
//            Set<String> userMachineIps = userMachines.stream().map(MachineBo::getIp).collect(Collectors.toSet());
//            res = liveMachineList.stream().filter(it -> userMachineIps.contains(it.getIp())).collect(Collectors.toList());
//        }
//        return res;
//    }
//
//    @RequestMapping(value = "/api/dpagent/cmd", method = RequestMethod.POST)
//    public Result<String> sendCmd(
//            HttpServletRequest request,
//            @RequestParam(value = "address") String address,
//            @RequestParam(value = "body", defaultValue = "{}") String body,
//            @RequestParam(value = "cmd") int cmd) {
//        if ("".equals(address)) {
//            return new Result<>(1, "节点不能为空", "");
//        }
//        List<String> addressList = Arrays.asList(address.split(","));
//        List<String> ret = addressList.stream().map(it -> {
//            RemotingCommandBo remotingCommand = agentManager.send(it.substring(1), cmd, body, 5000);
//            byte[] res = remotingCommand.getBody();
//            return null != res ? new String(res) : "";
//        }).collect(Collectors.toList());
//        String retStr = StringUtils.joinWith("\n", ret);
//        return Result.success(retStr);
//    }
//
//
//    /**
//     * 调用shell 命令
//     * 用来执行交互式命令
//     *
//     * @param request
//     * @param shellParams
//     * @return
//     */
//    @RequestMapping(value = "/api/dpagent/shell", method = RequestMethod.POST)
//    public Result<String> shell(
//            HttpServletRequest request,
//            @RequestBody ShellParams shellParams
//    ) {
//        if ("".equals(shellParams.getAddress())) {
//            return new Result<>(1, "节点不能为空", "");
//        }
//        if (StringUtils.isEmpty(shellParams.getCmd())) {
//            shellParams.setCmd("pwd");
//        }
//        if (StringUtils.isEmpty(shellParams.getPath())) {
//            shellParams.setPath("/tmp/");
//        }
//
//        ShellReq shellReq = new ShellReq();
//        shellReq.setShellCmd(shellParams.getCmd());
//        if (!StringUtils.isEmpty(shellParams.getPath())) {
//            shellReq.setPath(shellParams.getPath());
//        }
//        String req = new Gson().toJson(shellReq);
//
//        RemotingCommandBo remotingCommand = agentManager.send(shellParams.getAddress().substring(1), AgentCmd.shellReq, req, 5000);
//        byte[] res = remotingCommand.getBody();
//        return Result.success(null != res ? new String(res) : "");
//    }
//
//    private List<MachineBo> getMachineList() {
//        List<MachineBo> machineBoList = new ArrayList<>();
//        return machineBoList;
//    }
//}
