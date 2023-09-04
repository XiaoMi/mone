package com.xiaomi.mone.test;

import com.xiaomi.mone.app.AppBootstrap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/6/15 10:03
 */
@SpringBootTest(classes = AppBootstrap.class)
public class HeraAppEnvControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void queryAppEnvByIdTest() throws Exception {
        //模拟发送一个请求访问分页查询品牌列表的接口
        mockMvc.perform(MockMvcRequestBuilders.get("/hera/app/env/id") //设置请求地址
                .param("id", "1")) //设置请求参数
                .andExpect(MockMvcResultMatchers.status().isOk()) //断言返回状态码为200
                .andDo(MockMvcResultHandlers.print()) //在控制台打印日志
                .andReturn(); //返回请求结果
    }
}
