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

package com.xiaomi.data.push.rpc.netty;

import com.xiaomi.data.push.rpc.common.CompressionUtil;
import com.xiaomi.data.push.rpc.common.RemotingHelper;
import com.xiaomi.data.push.rpc.common.RemotingUtil;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;


/**
 * @author goodjava@qq.com
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger log = LoggerFactory.getLogger(RemotingHelper.RemotingLogName);

    private static final int FRAME_MAX_LENGTH = 32 * 1024 * 1024;

    /** 解压后最大允许大小 */
    private static final int MAX_DECOMPRESS_SIZE = 50 * 1024 * 1024;

    public NettyDecoder() {
        super(FRAME_MAX_LENGTH, 0, 4, 0, 4);
    }

    @Override
    public Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf) super.decode(ctx, in);
            if (null == frame) {
                return null;
            }

            if (frame.readableBytes() > FRAME_MAX_LENGTH) {
                log.warn("Frame too large");
//                ctx.close();
                return null;
            }


            ByteBuffer byteBuffer = frame.nioBuffer();

            RemotingCommand cmd = RemotingCommand.decode(byteBuffer);

            // 如果启用了压缩，则解压缩 body 数据
            if (cmd.isCompressionEnabled() && cmd.getBody() != null) {
//                byte[] body = cmd.getBody();
//
//                if (!looksLikeCompressed(body)) {
//                    // process it directly as a normal body without decompression.
//                    return cmd;
//                }
//
//                byte[] decompressedBody = CompressionUtil.decompress(body);
//                cmd.setBody(decompressedBody);
//                log.debug("Body decompressed for opaque: {}, compressed size: {}, decompressed size: {}",
//                        cmd.getOpaque(), body.length, decompressedBody.length);
                byte[] compressedBody = cmd.getBody();
                try {
                    byte[] decompressedBody = CompressionUtil.decompress(compressedBody);
                    if (decompressedBody.length > MAX_DECOMPRESS_SIZE) {
                        log.warn("Decompressed body too large");
                        return null;
                    }
                    cmd.setBody(decompressedBody);
                    log.debug("Body decompressed for opaque: {}, compressed size: {}, decompressed size: {}",
                            cmd.getOpaque(), compressedBody.length, decompressedBody.length);
                } catch (java.io.EOFException eof) {
                    // 捕获不完整压缩包
                    log.warn("Incomplete compressed data for opaque: {}, compressed size: {} - discarded",
                            cmd.getOpaque(), compressedBody.length);
                    // 丢弃 body 防止异常继续影响 Channel
                    cmd.setBody(null);
                } catch (Exception e) {
                    log.error("Decompression failed for opaque: {}, compressed size: {}",
                            cmd.getOpaque(), compressedBody.length, e);
                    cmd.setBody(null);
                }
            }

            return cmd;
        } catch (TooLongFrameException e) {
            log.warn("too large frame from {}, drop", RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
            // don t close to avoid avalanche
            return null;
        } catch (Exception e) {
            log.error("decode exception, {}", RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            RemotingUtil.closeChannel(ctx.channel());
        } finally {
            if (null != frame) {
                frame.release();
            }
        }

        return null;
    }
}
