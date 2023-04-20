package com.xiaomi.mone.log.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.List;

/**
 * @author: wtt
 * @date: 2022/5/31 11:14
 * @description:
 */
@Slf4j
public class NetUtilsTest {

    @Test
    public void testQueryIpWithDomain(){
//        String domain = "blog.csdn.net";
        String domain = "open";
        List<String> ips = NetUtils.queryIpWithDomain(domain);
        log.info("result:{}",ips);
    }
}
