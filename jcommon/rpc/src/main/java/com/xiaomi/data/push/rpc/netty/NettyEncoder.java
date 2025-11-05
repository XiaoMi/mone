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
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;


/**
 * @author goodjava@qq.com
 */
public class NettyEncoder extends MessageToByteEncoder<RemotingCommand> {
    private static final Logger log = LoggerFactory.getLogger(RemotingHelper.RemotingLogName);

    @Override
    public void encode(ChannelHandlerContext ctx, RemotingCommand remotingCommand, ByteBuf out) {
        try {
            byte[] body = remotingCommand.getBody();
            
            // 如果启用了压缩，则压缩 body 数据
            if (body != null && remotingCommand.isCompressionEnabled()) {
                byte[] compressedBody = CompressionUtil.compress(body);
                remotingCommand.setBody(compressedBody);
                log.debug("Body compressed for opaque: {}, original size: {}, compressed size: {}", 
                         remotingCommand.getOpaque(), body.length, compressedBody.length);
            }
            
            ByteBuffer header = remotingCommand.encodeHeader();
            out.writeBytes(header);
            
            byte[] finalBody = remotingCommand.getBody();
            if (finalBody != null) {
                out.writeBytes(finalBody);
            }
        } catch (Exception e) {
            log.error("encode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            if (remotingCommand != null) {
                log.error(remotingCommand.toString());
            }
            RemotingUtil.closeChannel(ctx.channel());
        }
    }
}
