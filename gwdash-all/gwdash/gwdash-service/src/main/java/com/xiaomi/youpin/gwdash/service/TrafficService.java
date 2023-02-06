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

package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.tesla.traffic.recording.api.bo.Result;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.GetRecordingConfigListReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfig;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfigList;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfigReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.replay.ReplayData;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.replay.ReplayRequest;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.replay.ReplayResponse;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.GetTrafficReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.traffic.TrafficList;
import com.xiaomi.youpin.tesla.traffic.recording.api.service.RecordingDubboService;
import com.xiaomi.youpin.tesla.traffic.recording.api.service.ReplayService;
import com.xiaomi.youpin.tesla.traffic.recording.api.service.TrafficDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

/**
 * @description: 流量录制回放
 * @author 
 *
 */
@Slf4j
@Service
public class TrafficService {

    @Reference(group = "${ref.traffic.service.group}", interfaceClass = RecordingDubboService.class, check = false)
    private RecordingDubboService recordingDubboService;

    @Reference(group = "${ref.traffic.service.group}", interfaceClass = TrafficDubboService.class, check = false)
    private TrafficDubboService trafficDubboService;


    @Reference(group = "${ref.traffic.service.group}", interfaceClass = ReplayService.class, check = false)
    private ReplayService replayService;


    public Result<ReplayData> replay(ReplayRequest request) {
       return replayService.replay(request);
    }

    /**
     * 删除
     * @param request
     * @return
     */
    public Result<Boolean> delTraffic(GetTrafficReq request) {
        return trafficDubboService.delTraffic(request);
    }

    /**
     * 可以修改body 和 headers
     * @param request
     * @return
     */
    public Result<Boolean> updateTraffic(GetTrafficReq request) {
        return trafficDubboService.updateTraffic(request);
    }


    public Result<ReplayResponse> getLastCallResult(GetTrafficReq req) {
        return trafficDubboService.getLastCallResult(req);
    }



    //录制配置
    public Result<RecordingConfigList> getRecordingConfigList(GetRecordingConfigListReq req) {
        return recordingDubboService.getRecordingConfigList(req);
    }

    public Result<Boolean> newRecordingConfig(RecordingConfig recordingConfig) {
        return recordingDubboService.newRecordingConfig(recordingConfig);
    }

    public Result<Boolean> updateRecordingConfig(RecordingConfig recordingConfig) {
        return recordingDubboService.updateRecordingConfig(recordingConfig);
    }

    public Result<Boolean> deleteRecordingConfig(RecordingConfigReq req) {
        return recordingDubboService.deleteRecordingConfig(req);
    }

    public Result<RecordingConfig> startRecording(RecordingConfigReq req) {
        return recordingDubboService.startRecording(req);
    }

    public Result<RecordingConfig> stopRecording(RecordingConfigReq req) {
        return recordingDubboService.stopRecording(req);
    }


    //流量
    public Result<TrafficList> getTrafficList(GetTrafficReq req) {
        return trafficDubboService.getTrafficList(req);
    }

}