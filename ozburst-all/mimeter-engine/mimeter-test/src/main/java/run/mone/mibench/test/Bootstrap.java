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

package run.mone.mibench.test;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.Mvc;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.DoceanHttpServer;

/**
 * @author goodjava@qq.com
 * @date 2022/6/2
 */
public class Bootstrap {


    /**
     * 起一个http服务+dubbo服务,用来测试dag任务
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        Ioc.ins().init("run.mone.mibench.test", "com.xiaomi.youpin.docean.plugin");
        Mvc.ins();
        DoceanHttpServer server = new DoceanHttpServer(HttpServerConfig.builder().port(7777).websocket(false).build());
        server.start();
    }


}
