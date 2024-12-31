package run.mone.neo4j.test.m;

import run.mone.neo4j.test.anno.Resource;
import run.mone.neo4j.test.anno.Service;
import run.mone.neo4j.test.anno.Test;

/**
 * @author goodjava@qq.com
 * @date 2024/8/26 18:23
 */
@Service
public class PersonServiceTest {

    @Resource
    private PersonService personService;

    @Test
    public void test1() {
        int res = personService.getCatCount();
        System.out.println(res);
    }
}
