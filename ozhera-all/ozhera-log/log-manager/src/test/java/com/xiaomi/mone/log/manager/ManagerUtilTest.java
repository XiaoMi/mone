package com.xiaomi.mone.log.manager;

import com.xiaomi.mone.log.manager.common.utils.ManagerUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/7/26 16:10
 */
@Slf4j
public class ManagerUtilTest {

    @Test
    public void test() {
        String path = "/home/work/log/zzy-test/server.log";
        String physicsDirectory = ManagerUtil.getPhysicsDirectory(path);
        log.info("result:{}", physicsDirectory);
    }
}
