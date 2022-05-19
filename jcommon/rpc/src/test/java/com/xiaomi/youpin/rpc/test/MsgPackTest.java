package com.xiaomi.youpin.rpc.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaomi.data.push.bo.MPPing;
import org.junit.Test;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.io.IOException;
import java.util.Arrays;

public class MsgPackTest {


    @Test
    public void test1() throws IOException {
        System.out.println("tests");

        MPPing ping = new MPPing();
        ping.setData("ping");
        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
        byte[] data = objectMapper.writeValueAsBytes(ping);
        System.out.println(Arrays.toString(data));


        MPPing v = objectMapper.readValue(data, MPPing.class);
        System.out.println(v.getData());


    }
}
