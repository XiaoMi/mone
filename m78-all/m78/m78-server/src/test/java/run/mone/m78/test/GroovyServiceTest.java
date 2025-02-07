package run.mone.m78.test;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import run.mone.m78.service.service.api.GroovyService;

/**
 * @author goodjava@qq.com
 * @date 2024/3/6 16:10
 */
public class GroovyServiceTest {


    @Test
    public void testExecute() {
        GroovyService groovyService = new GroovyService();
        Object res = groovyService.invoke("def sum(int a,int b){return a}", "sum", ImmutableMap.of(), 1,2);
        System.out.println(res);
    }

}
