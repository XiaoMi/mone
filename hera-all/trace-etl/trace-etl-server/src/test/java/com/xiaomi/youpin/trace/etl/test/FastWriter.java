package com.xiaomi.youpin.trace.etl.test;

import io.prometheus.client.Collector;
import org.apache.commons.lang3.tuple.Pair;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author goodjava@qq.com
 * @date 2023/8/29 00:27
 */
public class FastWriter {


    public byte[] getBytes(Enumeration<String> mfs) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        OutputStreamWriter w = new OutputStreamWriter(b);

        ArrayList<Pair<ByteArrayOutputStream, OutputStreamWriter>> list = new ArrayList<>();

        int v =2;

        IntStream.range(0, v).forEach(i -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(baos);
            list.add(Pair.of(baos, writer));
        });


        Collections.list(mfs).stream().parallel().forEach(it -> {
            Pair<ByteArrayOutputStream, OutputStreamWriter> pair = list.get((it.hashCode()& Integer.MAX_VALUE) % v);
            OutputStreamWriter writer = pair.getValue();
            IntStream.range(0, 100).forEach(i -> {
                try {
                    writer.write(it);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        });
        if (true) {
            return new byte[]{};
        }

//        list.stream().map(p->{
//            try {
//                p.getValue().flush();
//                return p.getKey().toByteArray();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }).flatMap(Arrays::stream);
//        w.flush();
//        return b.toByteArray();
        return null;

    }

}
