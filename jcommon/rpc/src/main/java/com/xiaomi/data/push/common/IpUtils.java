package com.xiaomi.data.push.common;

import lombok.SneakyThrows;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2022/4/17
 */
public abstract class IpUtils {


    @SneakyThrows
    public static List<String> ips(String host) {
        return Arrays.stream(InetAddress.getAllByName(host)).map(it -> it.getHostAddress()).collect(Collectors.toList());
    }
}
