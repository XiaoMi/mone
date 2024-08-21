package run.mone.neo4j.test.m;

import run.mone.neo4j.test.anno.Resource;
import run.mone.neo4j.test.anno.RestController;
import run.mone.neo4j.test.anno.Service;

/**
 * @author goodjava@qq.com
 * @date 2024/8/16 10:16
 */
@Service
public class PersonService {


    @Resource
    private CatService catService;


    /**
     * 计算两数之和
     *
     * @param a 第一个整数
     * @param b 第二个整数
     * @return 两数之和
     */
	//计算两数和
    public static int sum(int a, int b) {
        return (a + b);
    }

    /**
     * 计算两个数的差值
     *
     * @param num1 第一个数
     * @param num2 第二个数
     * @return 两数之差
     */
	//计算两个数的差值
    public static int subtract(int num1, int num2) {
        return num1 - num2;
    }

}
