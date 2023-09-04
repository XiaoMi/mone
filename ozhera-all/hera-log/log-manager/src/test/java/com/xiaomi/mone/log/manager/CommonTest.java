package com.xiaomi.mone.log.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/7/7 17:28
 */
@Slf4j
public class CommonTest {

    @Test
    public void testCollection() {
        List<String> list = Lists.newArrayList("张三", "里斯", "test", "john");
        List<String> otherAdmins = CollectionUtil.sub(list, 1, list.size());
        log.info("result:{}", otherAdmins);
    }
}
