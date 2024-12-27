package run.mone.ultraman.http;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 * @author goodjava@qq.com
 * @date 2023/4/19 17:43
 */
public class HttpResponseUtils {


    public static FullHttpResponse createResponse(HttpResponseStatus status,String data) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                Unpooled.wrappedBuffer(data.getBytes()));
        return response;
    }

}
