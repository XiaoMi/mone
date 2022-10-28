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
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import com.xiaomi.youpin.gwdash.service.TrafficService;
//import com.xiaomi.youpin.tesla.traffic.recording.api.bo.Result;
//import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.GetRecordingConfigListReq;
//import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfig;
//import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfigList;
//import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfigReq;
//import com.xiaomi.youpin.tesla.traffic.recording.api.bo.replay.ReplayData;
//import com.xiaomi.youpin.tesla.traffic.recording.api.bo.replay.ReplayRequest;
//import com.xiaomi.youpin.tesla.traffic.recording.api.bo.replay.ReplayResponse;
//import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.GetTrafficReq;
//import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.TrafficList;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Optional;
//
///**
// * @description: 流量录制回放
// * @author dingpei@xiaomi.com
// *
// */
//@RestController
//@Slf4j
//@RequestMapping("/api/traffic")
//public class TrafficController {
//
//    @Autowired
//    private TrafficService trafficService;
//
//    @Autowired
//    private LoginService loginService;
//
//
//    @RequestMapping(value = "/recording/config/list", method = {RequestMethod.GET, RequestMethod.POST})
//    public Result<RecordingConfigList> getRecordingConfigList(@RequestBody GetRecordingConfigListReq param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            return trafficService.getRecordingConfigList(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "recording/config/new", method = {RequestMethod.POST})
//    public Result<Boolean> newRecordingConfig(@RequestBody RecordingConfig param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            param.setCreator(account.getUsername());
//            param.setUpdater(account.getUsername());
//            return trafficService.newRecordingConfig(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//
//    @RequestMapping(value = "recording/replay", method = {RequestMethod.POST})
//    public Result<ReplayData> replay(@RequestBody ReplayRequest param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.replay] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        try {
//            ReplayRequest req = new ReplayRequest();
//            req.setId(param.getId());
//            return trafficService.replay(req);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//
//    /**
//     * 删除
//     * @param req
//     * @param request
//     * @param response
//     * @return
//     * @throws IOException
//     */
//    @RequestMapping(value = "recording/traffic/del", method = {RequestMethod.POST})
//    public Result<Boolean> del(@RequestBody GetTrafficReq req, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.traffice.del] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        try {
//            return trafficService.delTraffic(req);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    /**
//     * 更新
//     * @param req
//     * @param request
//     * @param response
//     * @return
//     * @throws IOException
//     */
//    @RequestMapping(value = "recording/traffic/update", method = {RequestMethod.POST})
//    public Result<Boolean> update(@RequestBody GetTrafficReq req, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.traffic.update] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        try {
//            return trafficService.updateTraffic(req);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//
//    @RequestMapping(value = "recording/traffic/last/result", method = {RequestMethod.POST})
//    public Result<ReplayResponse> lastResult(@RequestBody GetTrafficReq req, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.last.result] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        try {
//            return trafficService.getLastCallResult(req);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//
//
//    @RequestMapping(value = "recording/config/update", method = {RequestMethod.POST})
//    public Result<Boolean> updateRecordingConfig(@RequestBody RecordingConfig param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            param.setUpdater(account.getUsername());
//            return trafficService.updateRecordingConfig(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "recording/config/delete", method = {RequestMethod.POST})
//    public Result<Boolean> deleteRecordingConfig(@RequestBody RecordingConfigReq param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            param.setUser(account.getUsername());
//            return trafficService.deleteRecordingConfig(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "recording/config/start", method = {RequestMethod.POST})
//    public Result<RecordingConfig> startRecording(@RequestBody RecordingConfigReq param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            param.setUser(account.getUsername());
//            return trafficService.startRecording(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "recording/config/stop", method = {RequestMethod.POST})
//    public Result<RecordingConfig> stopRecording(@RequestBody RecordingConfigReq param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            param.setUser(account.getUsername());
//            return trafficService.stopRecording(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
//    public Result<TrafficList> getTrafficList(@RequestBody GetTrafficReq param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            return trafficService.getTrafficList(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//
//}