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

package com.xiaomi.data.push.rpc.processor;

import com.xiaomi.data.push.bo.AdminReq;
import com.xiaomi.data.push.context.AgentContext;
import com.xiaomi.data.push.rpc.netty.NettyRequestProcessor;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author goodjava@qq.com
 */
public class AdminProcessor implements NettyRequestProcessor {

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        AdminReq req = request.getReq(AdminReq.class);
        switch (req.getCmd()) {
            case "df":{
                String address = req.getParams().get("address");
                String filePath = req.getParams().get("file");
                Channel ch = AgentContext.ins().map.get(address).getChannel();
                if (null != ch) {

                }
                break;
            }
        }


        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
