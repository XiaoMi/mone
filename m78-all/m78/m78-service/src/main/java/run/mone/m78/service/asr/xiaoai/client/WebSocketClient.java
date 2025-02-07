package run.mone.m78.service.asr.xiaoai.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.xiaomi.iauth.java.sdk.app.IAuthAppSDKTool;
import com.xiaomi.iauth.java.sdk.common.IAuthTokenInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.CombinedHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Slf4j
public class WebSocketClient {
    private static final String SERVICE_ID =  "miai-sod";

    private URI uri;
    private Bootstrap bootstrap ;

    public WebSocketClient(String uri) throws URISyntaxException {
        this.uri = new URI(uri);
    }

    public void start(){
       EventLoopGroup group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new DefaultThreadFactory("M78WebSocketClient", true));

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_BACKLOG, 1024 * 1024 * 10)
                    .option(ChannelOption.SO_TIMEOUT,6000)
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer(){
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            String protocol = uri.getScheme();
                            if ("wss".equals(protocol)) {
                               // pipeline.addLast(new SslHandler(SslContextBuilder.forClient().build().newHandler(channel.alloc(), uri.getHost(), uri.getPort())));
                            }
                            pipeline.addLast(new HttpClientCodec());
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            pipeline.addLast(new IdleStateHandler(3,3,5, TimeUnit.SECONDS));
//                            pipeline.addLast(new LoggingHandler(LogLevel.DEBUG));
                            pipeline.addLast("webSocketClientProtocolHandler",new WebSocketClientProtocolHandler(uri, WebSocketVersion.V13, null, true,getHttpHeaders(), 64 * 1024));
                            pipeline.addLast(new WebScoketClientFrameHandler());
                        }
                    });
            this.bootstrap = bootstrap;
        } catch (Exception e) {
            log.error("bootstrap 启动异常",e);
        }

    }

    public Channel doConnect() throws  InterruptedException {
        URI websocketURI = this.uri;
        String host = websocketURI.getHost();
        Integer port = websocketURI.getPort();
        String scheme = websocketURI.getScheme();
        //建立HTTP连接
        ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
        log.info("建立链接，channelFuture={}",channelFuture.isSuccess());
        return channelFuture.channel();
    }
    public static String payload() {
        JSONObject baseObject = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("namespace", "MiAiSpeechAsr");
        header.put("name", "SpeechRecognize");
        baseObject.put("header", header);
        JSONObject payload = new JSONObject();
//        payload.put("model_type", "input-e2e");
        payload.put("model_type", "input-ai-copilot-e2e");//input-ai-copilot-e2e
        payload.put("codec", "pcm");
        payload.put("bit", 16);
        payload.put("rate", 16000);
        payload.put("channel", 1);
//        payload.put("lang", "zh-CN");
        JSONArray vendor = new JSONArray();
        vendor.add("XiaoMi");
        payload.put("vendor", vendor);
        payload.put("enable_vad", true);
        payload.put("enable_punctuate", true);
        baseObject.put("payload", payload);
        baseObject.put("context", new JSONArray());
        return JSON.toJSONString(baseObject);
    }
    public static String finishPayload() {
        JSONObject baseObject = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("namespace", "General");
        header.put("name", "RecognizeStreamEnded");
        baseObject.put("header", header);
        baseObject.put("payload", new JSONObject());
        baseObject.put("context", new JSONArray());
        return JSON.toJSONString(baseObject);
    }
    public  HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new CombinedHttpHeaders(false);
        String appId = "1";
        String userAgent = "";
        //requestId字段用来追踪查询请求，可以根据具体情况，使用不同的方式设置，只需保证唯一即可
        httpHeaders.set("Authorization", Lists.newArrayList("IAUTH_AUTH appId:"+appId,"token:"+ getToken()));
        httpHeaders.set("RequestId", UUID.randomUUID().toString());
        httpHeaders.set("User-Agent", userAgent);
        return httpHeaders;
    }
    public String getToken() {
        String tokenStr = null;
        try {
            IAuthTokenInfo info = IAuthAppSDKTool.getInstance().getIAuthToken(SERVICE_ID, false);
            tokenStr = info.getToken();
            log.info("获取Token成功,info={}", JSON.toJSONString(info));
        } catch (Exception e) {
            log.error("获取Token失败", e);
        }
        return tokenStr;
    }
}
