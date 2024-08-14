package run.mone.z.desensitization.domain.gateway;

import run.mone.z.desensitization.domain.model.DemoReqEntiry;
import run.mone.z.desensitization.domain.model.DemoResEntiry;

public interface DemoGateway {

    DemoResEntiry demoTest(DemoReqEntiry reqEntiry);

}
