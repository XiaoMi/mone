package run.mone.neo4j.test.m;

import run.mone.neo4j.test.anno.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/8/19 18:21
 */
@RestController
public class CatService {

    private Map<String,String> data = new HashMap<>();


    //获取小猫的数量
    //获取小猫的数量
    public int getCatCount() {
        return data.size();
    }


}
