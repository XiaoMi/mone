package run.mone.local.docean.controller.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import run.mone.junit.DoceanConfiguration;
import run.mone.junit.DoceanExtension;
import run.mone.local.docean.fsm.FlowService;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2024/3/5 11:00
 */
@ExtendWith(DoceanExtension.class)
@DoceanConfiguration(basePackage = {"run.mone.local.docean", "com.xiaomi.youpin"})
public class CommonTest {


    @Resource
    private FlowService flowService;

    @Test
    public void displayFlowServiceSetContents() {
    }

}
