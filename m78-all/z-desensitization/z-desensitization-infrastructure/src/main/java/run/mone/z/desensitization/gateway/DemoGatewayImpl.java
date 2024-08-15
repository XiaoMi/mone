package run.mone.z.desensitization.gateway;

import com.xiaomi.sautumn.serverless.api.tool.Tool;
import org.springframework.stereotype.Service;
import run.mone.z.desensitization.domain.gateway.DemoGateway;
import run.mone.z.desensitization.domain.model.DemoReqEntiry;
import run.mone.z.desensitization.domain.model.DemoResEntiry;

import javax.annotation.Resource;

@Service
public class DemoGatewayImpl implements DemoGateway {

    @Resource
    private Tool tool;

    @Override
    public DemoResEntiry demoTest(DemoReqEntiry reqEntiry) {
        return new DemoResEntiry(tool.getMd5(reqEntiry.getTest()));
    }

}
