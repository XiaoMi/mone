package com.xiaomi.youpin.docean.plugin.test;

import lombok.Data;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.annotation.Message;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/13 15:49
 */
public class MsgpackTest {

    @Message
    @lombok.Data
    public static class Data {
        private int id;
        private String name;
    }



    @Test
    public void testMsgpack() throws IOException {
        MessagePack mp = new MessagePack();
        Data d = new Data();
        d.setId(12);
        d.setName("zzy");
        byte[] b =mp.write(d);
        System.out.println(Arrays.toString(b));
        Data dd = mp.read(b,Data.class);
        System.out.println(dd);
        String str = "a12";
        System.out.println(mp.read(mp.write(str),String.class));
        int i =12;
        System.out.println(mp.read(mp.write(i),int.class));
    }

}
