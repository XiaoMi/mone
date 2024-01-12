package run.mone.mibench.test.controller;

import com.xiaomi.youpin.docean.anno.RequestMapping;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test {

    /**
     * //Athena:用token换id的方法注释
     * 用token换id
     *
     * @param request
     * @return
     */
    @RequestMapping(path = "/id")
    //Athena:定义请求映射路径的注释
    public Response<String> getId(Request request) {
        //Athena:记录请求信息的注释
        log.info("/id req:{}", request);
        //Athena:创建响应对象的注释
        Response<String> r = new Response<>();
        //Athena:设置响应消息的注释
        r.setMessage(request.getId());
        //Athena:设置响应数据的注释
        r.setData("1");
        //Athena:返回响应对象的注释
        return r;
    }
}
