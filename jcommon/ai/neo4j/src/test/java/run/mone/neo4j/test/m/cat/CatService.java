package run.mone.neo4j.test.m.cat;

import lombok.extern.slf4j.Slf4j;
import run.mone.neo4j.test.anno.Service;

import java.util.HashMap;

/**
 * @author goodjava@qq.com
 * @date 2024/8/19 18:21
 */
@Service
@Slf4j
public class CatService {

    private HashMap<String,String> data = new HashMap<>();


    //获取小猫的数量
    //获取小猫的数量
    public int getCatCount() {
        log.info("abc");
        System.out.println("123");
        return data.size();
    }


}
