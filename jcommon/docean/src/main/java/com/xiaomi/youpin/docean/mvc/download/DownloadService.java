package com.xiaomi.youpin.docean.mvc.download;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

/**
 * @author goodjava@qq.com
 */
public class DownloadService extends BaseService {


    public void download(ChannelHandlerContext ctx, FullHttpRequest request, String name, String id) throws IOException {
        final String path = path(name);
        File file = new File(path);
        if (file.isHidden() || !file.exists() || !file.isFile() || !file.getPath().startsWith(DATAPATH)) {
            send(ctx, NOT_FOUND, NOT_FOUND.reasonPhrase());
            return;
        }
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        long fileLength = raf.length();
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        HttpUtil.setContentLength(response, fileLength);
        setContentTypeHeader(response, file);

        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set("CONNECTION", HttpHeaderNames.KEEP_ALIVE);
        }

        ctx.write(response);

        ChannelFuture sendFileFuture =
                ctx.write(new HttpChunkedInput(new ChunkedFile(raf, 0, fileLength, 1024 * 64)), ctx.newProgressivePromise());


        sendFileFuture.addListener(new DownloadListener(id, fileLength));
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        if (!HttpHeaders.isKeepAlive(request)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }


    private static String path(String name) {
        return DATAPATH + File.separator + name;
    }


    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap m = new MimetypesFileTypeMap();
        String contentType = m.getContentType(file.getPath());
        if (!contentType.equals("application/octet-stream")) {
            contentType += "; charset=utf-8";
        }
        response.headers().set(CONTENT_TYPE, contentType);
    }


}
