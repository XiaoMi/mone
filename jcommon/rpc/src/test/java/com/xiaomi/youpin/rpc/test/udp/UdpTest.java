package com.xiaomi.youpin.rpc.test.udp;

import com.xiaomi.data.push.udp.UdpClient;
import com.xiaomi.data.push.udp.UdpServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.stream.IntStream;


@Slf4j
public class UdpTest {


    @Test
    public void testServer() throws InterruptedException {
        UdpServer server = new UdpServer("127.0.0.1", 1234, msg -> msg + " --> " + System.currentTimeMillis());
        server.start();
    }


    @Test
    public void testClient() throws InterruptedException, IOException {
        UdpClient client = new UdpClient(1235, msg -> {
            log.info("--->{}", msg);
        });

        client.start();
        IntStream.range(0, 10).forEach(i -> {
            client.sendMessage("hi", new InetSocketAddress("127.0.0.1", 1234));
        });

        System.in.read();
    }
}
