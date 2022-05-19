package com.xiaomi.youpin.docean.test;

import com.xiaomi.youpin.docean.common.FileUtils;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2022/5/7
 */
public class FileUtilsTest {

    @Test
    public void testFileUtils() {
        System.out.println(FileUtils.home());
        System.out.println(FileUtils.tmp());
    }
}

