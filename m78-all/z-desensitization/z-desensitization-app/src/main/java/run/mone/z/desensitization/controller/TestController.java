package run.mone.z.desensitization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import run.mone.z.desensitization.domain.gateway.DemoGateway;
import run.mone.z.desensitization.domain.model.DemoReqEntiry;

/**
 * @author wm
 */
@RestController
public class TestController {

    @Autowired
    private DemoGateway demoGateway;

    @ResponseBody
    @RequestMapping(value = "/md5", method = RequestMethod.GET)
    public String getStr(){
        DemoReqEntiry req = new DemoReqEntiry();
        req.setTest("abc");
        return demoGateway.demoTest(req).getTest();
    }


}
