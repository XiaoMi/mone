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

package run.mone.mimeter.engine.agent.handler;

import run.mone.mimeter.engine.agent.bo.Version;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import run.mone.mimeter.engine.service.MetricsService;

import javax.annotation.Resource;

import java.security.Security;

/**
 * @author goodjava@qq.com
 * @author dongzhenxing
 * @date 2022/5/19
 */
@Controller
public class AgentHandler {

    @Resource
    private MetricsService metricsService;

    /**
     * 控制发压机jvm缓存dns时间,减短缓存时间
     *
     * 一般情况下我们不需要完全取消JVM的DNS缓存，只需要调小有效时间，经过一些测试发现一下结论：
     * 1）在高并发时，不做DNS缓存时的CPU耗用比做了3s缓存的CPU耗用要高3/4倍，实时DNS请求相当耗用CPU；
     * 2）3s和30s缓存有效时间对dns查询响应时间的影响差别不大，cpu内存占用都比较接近；
     * 3）建议使用3秒缓存，兼顾运维和性能；
     */
    public void init(){
        Security.setProperty("networkaddress.cache.ttl", "3");
        Security.setProperty("networkaddress.cache.negative.ttl", "0");
    }

    @RequestMapping(path = "/info")
    public String info() {
        return "agent:" + new Version() + " jvm:" + System.getProperty("java.version");
    }


    @RequestMapping(path = "/metrics", method = "get")
    public String metrics() {
        return this.metricsService.metrics();
    }


}
