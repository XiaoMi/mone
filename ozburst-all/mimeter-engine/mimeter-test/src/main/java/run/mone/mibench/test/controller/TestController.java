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

package run.mone.mibench.test.controller;

import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @date 2022/6/2
 */
@Controller
public class TestController {

    private static Logger log = LoggerFactory.getLogger(TestController.class);

    /**
     //Athena:获取一个token
     * 换取一个token
     *
     * @param request
     * @return
     */
    //Athena:映射请求路径为/token
    @RequestMapping(path = "/token")
    //Athena:处理请求并返回响应
    public Response<String> getToken(Request request) {
        //Athena:打印请求信息
        log.info("/token req:{}", request);
        //Athena:模拟延迟
        sleep();
        //Athena:创建响应对象
        Response<String> r = new Response<>();
        //Athena:设置响应消息
        r.setMessage(request.getId());
        //Athena:设置响应数据
        r.setData("abcdefaaff");
        //Athena:返回响应
        return r;
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 用token换id
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/id")
    public Response<String> getId(Request request) {
        log.info("/id req:{}", request);
        Response<String> r = new Response<>();
        r.setMessage(request.getId());
        r.setData("1");
        return r;
    }

    /**
     * 用id查年龄
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/age")
    public Response<String> getAge(Request request) {
        log.info("/age req:{}", request);
        Response<String> r = new Response<>();
        r.setMessage(request.getId());
        r.setData("22");
        return r;
    }


    @RequestMapping(path = "/sum", method = "get")
    public Response<String> sum(@RequestParam("a") int a, @RequestParam("b") int b) {
        log.info("/sum req:{} {}", a, b);
        Response<String> r = new Response<>();
        r.setData("" + (a + b));
        return r;
    }


}
