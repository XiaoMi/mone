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

package run.mone.docean.plugin.rpc.interceptor;

import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;
import run.mone.docean.plugin.rpc.proxy.RpcReferenceBo;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 * @date 1/8/23
 */
@Slf4j
public class RpcCallMethodInterceptor extends AbstractRpcInterceptor {

    protected RpcReferenceBo reference;

    private String alias;

    public RpcCallMethodInterceptor(Ioc ioc, Config config, RpcReferenceBo reference, String alias) {
        super(ioc, config);
        this.reference = reference;
        this.alias = alias;
    }

    @Override
    public void intercept0(Context ctx, RemotingCommand command, Object obj, Method method, Object[] params) {
        ctx.getData().put("skip_code", "false");
        ctx.getData().put("serviceName", this.reference.getServiceName());
        command.setCode(2);
    }

}
