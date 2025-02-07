package run.mone.m78.server.controller;

import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class TestController {

    /**
     * 进程心跳调用
     *
     * @return
     */
    @RequestMapping(value = "/isOk")
    public Result<String> isOk() {
        return Result.success("ok");
    }


}
