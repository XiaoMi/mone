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

package com.xiaomi.mone.grpc.server.interceptor.client;

import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.ForwardingClientCallListener;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import lombok.extern.slf4j.Slf4j;
import run.mone.mesh.bo.SideCarAddress;
import run.mone.mesh.common.Cons;

/**
 * @Author goodjava@qq.com
 * @Date 2022/6/27 10:55
 */
@Slf4j
public class SideCarClientInterceptor implements ClientInterceptor {


    private SideCarAddress sideCarAddress;

    public SideCarClientInterceptor(SideCarAddress sideCarAddress) {
        this.sideCarAddress = sideCarAddress;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(next.newCall(method, callOptions)) {
            @Override
            public void sendMessage(ReqT message) {
                log.info("grpc client request:{}", message);
                super.sendMessage(message);
            }

            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                //此处为你登录后获得的token的值
                headers.put(Cons.SIDE_CAR_TOKEN, "dprqfwb123!");
                headers.put(Cons.SIDE_CAR_APP, sideCarAddress.getApp());
                super.start(new ForwardingClientCallListener.SimpleForwardingClientCallListener<RespT>(responseListener) {
                    @Override
                    public void onHeaders(Metadata headers) {
                        log.info("header received from server:" + headers);
                        super.onHeaders(headers);
                    }

                    @Override
                    public void onMessage(RespT message) {
                        log.info("grpc client response:{}", message);
                        super.onMessage(message);
                    }
                }, headers);
            }
        };
    }
}
