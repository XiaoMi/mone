package com.xiaomi.youpin.docean.plugin.test;

import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.redis.Redis;

import com.xiaomi.youpin.docean.plugin.test.bo.Test;
import org.nutz.dao.impl.NutDao;

import javax.annotation.Resource;

/**
 * @author goodjava@qq.com
 * @date 2020/6/27
 */
@Component
public class TestDao {

    @Resource
    private NutDao dao;

    @Resource
    private Redis redis;

    @TCAnno
    public Test get() {
        return dao.fetch(Test.class, 1);
    }

    public String key() {
        redis.set("name", "zzy");
        return redis.get("name");
    }
}
