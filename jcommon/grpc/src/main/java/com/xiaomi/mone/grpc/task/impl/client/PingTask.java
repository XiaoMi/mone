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

package com.xiaomi.mone.grpc.task.impl.client;

import com.xiaomi.mone.grpc.GrpcClient;
import com.xiaomi.mone.grpc.task.GrpcTask;
import lombok.extern.slf4j.Slf4j;
import run.mone.mesh.bo.SideCarRequest;
import run.mone.mesh.bo.SideCarResponse;

/**
 * @author goodjava@qq.com
 * @date 2022/7/3
 */
@Slf4j
public class PingTask implements GrpcTask {

    private String app;

    private GrpcClient client;

    public PingTask(String app, GrpcClient client) {
        this.app = app;
        this.client = client;
    }

    @Override
    public void execute() {
        SideCarRequest request = SideCarRequest.newBuilder()
                .setApp(app).setCmd("ping").build();
        SideCarResponse res = client.call(request);
        log.info("receive:{}", new String(res.getData().toByteArray()));
    }
}
