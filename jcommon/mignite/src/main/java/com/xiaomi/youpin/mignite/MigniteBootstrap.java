package com.xiaomi.youpin.mignite;

import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;

import java.util.Collections;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class MigniteBootstrap {

    public static void main(String[] args) {
        log.info("ignite start");
        IgniteConfiguration cfg = new IgniteConfiguration();
//        cfg.setClientMode(true);
        cfg.setPeerClassLoadingEnabled(true);
        TcpDiscoveryMulticastIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder();
        ipFinder.setAddresses(Collections.singletonList("127.0.0.1:47500..47509"));
        cfg.setDiscoverySpi(new TcpDiscoverySpi().setIpFinder(ipFinder));
        Ignite ignite = Ignition.start(cfg);
        IgniteCache<String, String> cache = ignite.getOrCreateCache("myCache");
        cache.put("name", "zzy");
        log.info(">> Created the cache and add the values.");

        System.out.println(cache.get("name"));
    }
}
