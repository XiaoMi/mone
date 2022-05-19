package com.xiaomi.mone.spider.test;

import com.xiaomi.mone.spider.SpiterRunner;
import org.junit.Test;

import java.io.IOException;

/**
 * @author goodjava@qq.com
 * @date 2021/11/3
 * <p>
 * driver 下载路径:https://npm.taobao.org/mirrors/chromedriver
 */
public class SpiterRunnerTest {

    @Test
    public void testRun() throws IOException {
        new SpiterRunner().run();
    }

    @Test
    public void testRun2() {
        new SpiterRunner().run2();
    }

    /**
     * driver 目前放到了tmp目录
     */

    @Test
    public void testWordsFinder() {
        new SpiterRunner().findWords2("正能量文案", 100);
    }
}
