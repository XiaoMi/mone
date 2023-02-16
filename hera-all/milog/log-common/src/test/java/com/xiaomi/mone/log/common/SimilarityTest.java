package com.xiaomi.mone.log.common;

import cn.hutool.core.date.DateUtil;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/2/16 12:48
 */
public class SimilarityTest {

    @Test
    public void test1() {
        String content1 = "log_info.log";
        String content2 = "log-debug.2023443";
        String content3 = "log-debug.log";
        String content4 = "log_warn.log";
        System.out.println(FuzzySearch.ratio(content2, content1));
        System.out.println(FuzzySearch.ratio(content2, content3));
        System.out.println(FuzzySearch.ratio(content2, content4));
    }

    @Test
    public void test2(){
        System.out.println(DateUtil.parse("2022/01/23 23:23:34").getTime());
    }
}
