package run.mone.knowledge.server.provider;

import run.mone.knowledge.api.DemoProvider;
import run.mone.knowledge.api.dto.DemoReqDto;
import run.mone.knowledge.api.dto.DemoResDto;
import run.mone.knowledge.common.Constant;
import run.mone.knowledge.service.DemoService;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author bot
 */
@Slf4j
@SuppressWarnings(value = "checkstyle:MagicNumber")
@DubboService(timeout = Constant.DEF_DUBBO_TIMEOUT, group = "${dubbo.group}")
public class DemoProviderImpl implements DemoProvider {

    @Autowired
    private DemoService demoService;

    @Override
    public Result<DemoResDto> query(DemoReqDto reqDto) {
        //参数校验，用户信息校验等
        log.info("DemoProvider.query请求 reqDto={}", reqDto);
        Result<DemoResDto> result = demoService.query(reqDto);
        log.info("DemoProvider.query响应 result={}", result);
        return result;
    }
}
