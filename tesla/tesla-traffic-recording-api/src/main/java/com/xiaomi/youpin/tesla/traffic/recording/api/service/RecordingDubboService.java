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

package com.xiaomi.youpin.tesla.traffic.recording.api.service;

import com.xiaomi.youpin.tesla.traffic.recording.api.bo.Result;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.GetRecordingConfigListReq;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfig;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfigList;
import com.xiaomi.youpin.tesla.traffic.recording.api.bo.recording.RecordingConfigReq;

/**
 * @author dingpei
 */
public interface RecordingDubboService {

    /**
     * 获取录制配置列表
     * @return
     */
    Result<RecordingConfigList> getRecordingConfigList(GetRecordingConfigListReq req);

    /**
     * 新建录制配置
     * @param recordingConfig
     * @return
     */
    Result<Boolean> newRecordingConfig(RecordingConfig recordingConfig);

    /**
     * 更新配置
     * @param recordingConfig
     * @return
     */
    Result<Boolean> updateRecordingConfig(RecordingConfig recordingConfig);

    /**
     * 删除配置
     * @param req
     * @return
     */
    Result<Boolean> deleteRecordingConfig(RecordingConfigReq req);

    /**
     * 开始录制
     * @return
     */
    Result<RecordingConfig> startRecording(RecordingConfigReq req);

    /**
     * 停止录制
     * @return
     */
    Result<RecordingConfig> stopRecording(RecordingConfigReq req);

}
