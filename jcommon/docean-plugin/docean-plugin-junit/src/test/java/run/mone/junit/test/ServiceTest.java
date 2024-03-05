package run.mone.junit.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import run.mone.junit.DoceanConfiguration;
import run.mone.junit.DoceanExtension;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2024/3/3 17:37
 */
@ExtendWith(DoceanExtension.class)
@DoceanConfiguration(basePackage = {"run.mone.junit.test"})
public class ServiceTest {

    @Resource
    private TestService ts;


    @Test
    public void test1() {
        System.out.println(ts.hi());
    }

}
