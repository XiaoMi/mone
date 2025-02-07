/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.m78.server.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.util.WebAppRootListener;
import run.mone.m78.server.ws.*;
import run.mone.m78.gateway.KnowledgeGatewayService;
import run.mone.m78.server.ws.*;
import run.mone.m78.server.ws.biz.IMFriendHandlerService;
import run.mone.m78.server.ws.biz.VisionHandlerService;
import run.mone.m78.service.asr.auth.RetailAuthService;
import run.mone.m78.service.asr.tencent.TencentAsrService;
import run.mone.m78.service.asr.xiaoai.XiaoAiService;
import run.mone.m78.service.service.bot.BotService;
import run.mone.m78.service.service.flow.FlowService;
import run.mone.m78.service.service.multiModal.AudioModalService;
import run.mone.m78.service.service.multiModal.MultiModalLimitService;
import run.mone.m78.service.service.multiModal.audio.AudioStreamService;
import run.mone.m78.service.service.user.UserLoginService;
import run.mone.m78.service.service.version.VersionService;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.List;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer, ServletContextInitializer {

    @NacosValue(value = "${aegis.sdk.public.key}", autoRefreshed = true)
    private String aegisSdk;

    @Value("${ws.connection.max.size:100}")
    private int maxConnectionSize;

    @Value("${ws.isopen}")
    private boolean isOpen;

    @Value("${ws.external.open}")
    private boolean isOpenToExternal;

    @NacosValue("${asr.ws.out.vendors}")
    private List<String> asrWsOutVendors;

    @Getter
    @NacosValue(value = "${asr.out.auth.switch}",autoRefreshed = true)
    private String authSwitch;

    @Autowired
    private BotService botService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private AudioModalService audioModalService;

    @Autowired
    private AudioStreamService audioStreamService;

    @Autowired
    private IMFriendHandlerService imFriendHandlerService;

    @Autowired
    private VisionHandlerService visionHandlerService;

    @Autowired
    private VersionService versionService;

    @Autowired
    private MultiModalLimitService multiModalLimitService;

    @Resource
    private UserLoginService userLoginService;

    @Resource
    private XiaoAiService xiaoAiService;
    @Resource
    private TencentAsrService tencentAsrService;

    @Resource
    private KnowledgeGatewayService knowledgeGatewayService;

    @Resource
    private RetailAuthService retailAuthService;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(maxConnectionSize, botService, flowService, versionService,multiModalLimitService), "/ws/bot/execute")
                .setAllowedOrigins("*")
                .addInterceptors(new WebSocketHandshake(aegisSdk, isOpen));

        registry.addHandler(new WebSocketHandler(maxConnectionSize, botService, flowService, versionService,multiModalLimitService), "/ws/sockjs/bot/execute")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new WebSocketHandshake(aegisSdk, isOpen))
                .withSockJS()
                .setClientLibraryUrl("");

        registry.addHandler(new WebSocketHandler(maxConnectionSize, botService, flowService, imFriendHandlerService, visionHandlerService, versionService, multiModalLimitService), "/ws/bot/abc", "/ws/bot/biz/abc")
                .setAllowedOrigins("*")
                .addInterceptors(new WebSocketHandshake(aegisSdk, isOpen, userLoginService));

        registry.addHandler(new FlowHandler(maxConnectionSize, flowService), "/ws/sockjs/flow/status/stream", "/ws/sockjs/flow/test")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new WebSocketHandshake(aegisSdk, isOpen))
                .withSockJS()
                .setClientLibraryUrl("");

        registry.addHandler(new FlowHandler(maxConnectionSize, flowService), "/ws/flow/stream/access")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new WebSocketHandshake(aegisSdk, isOpen));

        registry.addHandler(new AudioHandler(maxConnectionSize, tencentAsrService ,xiaoAiService), "/ws/multiModal/audio")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new WebSocketASRHandshake(aegisSdk, isOpen, isOpenToExternal, asrWsOutVendors, retailAuthService,this));

        registry.addHandler(new KnowledgeHandler(maxConnectionSize, knowledgeGatewayService), "/ws/knowledge/execute")
                .setAllowedOriginPatterns("*");

    }

    //允许websocket message 的大小设置
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(WebAppRootListener.class);
        // 减少text buffer size到5m，binary到2m，一个连接占用7m，防止OOM
        servletContext.setInitParameter("org.apache.tomcat.websocket.textBufferSize", "5120000");
        servletContext.setInitParameter("org.apache.tomcat.websocket.binaryBufferSize", "2560000");
    }

}
