package run.mone.ultraman;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.http.HttpResponseUtils;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Gson gson = new Gson();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        // 创建http响应
        sendRes(ctx, new JsonObject());
    }

    private void sendRes(ChannelHandlerContext ctx, JsonObject obj) {
        FullHttpResponse response = HttpResponseUtils.createResponse(HttpResponseStatus.OK, gson.toJson(obj));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @NotNull
    private static JsonObject getObj(String data, String message) {
        JsonObject obj = new JsonObject();
        obj.addProperty("code", 0);
        obj.addProperty("message", message);
        obj.addProperty("data", data);
        return obj;
    }

}