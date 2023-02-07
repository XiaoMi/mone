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

package run.mone.mesh.obs;

import com.google.protobuf.ByteString;
import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import run.mone.mesh.bo.SideCarPushMsg;
import run.mone.mesh.bo.SideCarRequest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * @Author goodjava@qq.com
 * @Date 2022/6/24 11:23
 */
@Slf4j
public class ClientObs implements BiConsumer<SideCarPushMsg, StreamObserver<SideCarRequest>> {


    private ConcurrentHashMap<String, UdsProcessor<RpcCommand, RpcCommand>> processorMap;


    public ClientObs(ConcurrentHashMap<String, UdsProcessor<RpcCommand, RpcCommand>> processorMap) {
        this.processorMap = processorMap;
    }

    @Override
    public void accept(SideCarPushMsg msg, StreamObserver<SideCarRequest> obs) {
        //client 调用后的返回结果(通过listen双向流调用过去的)
        if (msg.getType().equals("response")) {
            log.info("res:{}", new String(msg.getData().toByteArray()));
            return;
        }

        //可以理解成server端调用过来
        UdsProcessor<RpcCommand, RpcCommand> processor = processorMap.get(msg.getCmd());
        if (null != processor) {
            RpcCommand rpcCommand = new RpcCommand();
            rpcCommand.setApp(msg.getApp());
            rpcCommand.setCmd(msg.getCmd());
            rpcCommand.setData(msg.getData().toByteArray());
            rpcCommand.setAttachments(msg.getAttachmentsMap());
            RpcCommand res = processor.processRequest(rpcCommand);
            SideCarRequest scr = SideCarRequest.newBuilder().setData(ByteString.copyFrom(res.data()))
                    .setType("response")
                    .setReqId(msg.getReqId())
                    .setApp("app")
                    .setCmd("")
                    .build();
            //给服务端返回信息
            obs.onNext(scr);
        }
    }
}
