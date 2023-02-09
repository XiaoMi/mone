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

package run.mone.docean.plugin.rpc.context;

/**
 * @author goodjava@qq.com
 * @date 2023/1/9
 */
public class RpcContextHolder {

    private static ThreadLocal<RpcContextHolder> context = new ThreadLocal<RpcContextHolder>() {
        @Override
        protected RpcContextHolder initialValue() {
            return new RpcContextHolder();
        }
    };

    private RpcContext rpcContext;


    public void set(RpcContext context) {
        this.rpcContext = context;
    }

    public RpcContext get() {
        return this.rpcContext;
    }


    public static RpcContextHolder getContext() {
        return context.get();
    }

    public void close() {
        context.remove();
    }

}
