package run.mone.z.desensitization.service;

import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import run.mone.sautumnn.springboot.starter.anno.DubboService;
import run.mone.z.desensitization.api.service.DubboHealthService;

@DubboService(interfaceClass = DubboHealthService.class, timeout = 1000, group = "${dubbo.group}", version = "1.0")
@Slf4j
public class DubboHealthServiceImpl implements DubboHealthService {

    @Override
    public Result<String> health() {
        return Result.success("ok");
    }
}
