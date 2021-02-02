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

package com.xiaomi.youpin.gwdash.config;

import com.xiaomi.youpin.gwdash.service.PipelineService;
import com.xiaomi.youpin.gwdash.ws.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author tsingfu
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private PipelineService pipelineService;
    @Autowired
    private WebSSHWebSocketHandler webSSHWebSocketHandler;

    @Autowired
    private AgentLogWebsocketHandler agentLogWebsocketHandler;

     @Autowired
     private BroadcastWebSocketHandler broadcastWebSocketHandler;

    private  String socketjsLibUrl= "https://cdn.jsdelivr.net/npm/sockjs-client@1.3.0/dist/sockjs.min.js";

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(dataHubWebSocketHandler(), "/ws/cicd")
//                .addInterceptors(webSocketHandshakeInterceptor())
                .setAllowedOrigins("*")
                .withSockJS()
                .setClientLibraryUrl( "" );
        // ssh
        webSocketHandlerRegistry
                .addHandler(webSSHWebSocketHandler, "/ws/ssh")
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .setAllowedOrigins("*")
                .withSockJS()
                .setClientLibraryUrl(socketjsLibUrl);

        // agent system log
        webSocketHandlerRegistry
                .addHandler(agentLogWebsocketHandler, "/ws/agentSystemlog")
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .setAllowedOrigins("*")
                .withSockJS()
                .setClientLibraryUrl(socketjsLibUrl);

        // broadCast
        /**
         * bug:使用sockjs 前端会打过来请求但是会404
         * 失败后会重试 但是实际每个请求都打到了服务器上并且不能触发关闭
         * 导致 服务端的close wait 过多而504
         * 暂取消sockjs
         */
        webSocketHandlerRegistry
                .addHandler(broadcastWebSocketHandler, "/ws/broadcast")
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .setAllowedOrigins("*");
//                .withSockJS()
//                .setClientLibraryUrl(socketjsLibUrl);

    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public CiCdWebSocketHandler dataHubWebSocketHandler() {
        return new CiCdWebSocketHandler(pipelineService);
    }


    @Bean
    public WebSocketHandshakeInterceptor webSocketHandshakeInterceptor() {
        return new WebSocketHandshakeInterceptor();
    }
}
