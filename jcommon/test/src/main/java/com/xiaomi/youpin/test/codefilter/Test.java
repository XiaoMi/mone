package com.xiaomi.youpin.test.codefilter;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class Test {


    public static void main(String... args) throws IOException {

        List<Pair<Integer, byte[]>> list = Files.list(Paths.get(args[0])).sorted().map(it -> {
            try {
                byte[] data = Files.readAllBytes(it);
                JsonElement m = new Gson().fromJson(new String(data), JsonElement.class);
                if (m.isJsonObject()) {
                    JsonElement c = m.getAsJsonObject().get("code");
                    if (c == null) {
                        log.info("{} data:{}", it, 200);
                        return Pair.of(200, data);
                    }
                    try {
                        Integer code = c.getAsInt();
                        log.info("{} data:{}", it, code);
                        return Pair.of(code, data);
                    } catch (Exception ex) {
                        log.info("{} data:{}", it, 200);
                        return Pair.of(200, data);
                    }
                }
                log.info("{} data:{}", it, 200);
                return Pair.of(200, data);
            } catch (Exception e) {
                log.error("error:{} path:{}", e.getMessage(), it);
            }
            return null;
        }).filter(it -> it != null).collect(Collectors.toList());

        Stopwatch sw = Stopwatch.createStarted();

        int i = 1;
        // int i = 1000000;


        long sum = IntStream.range(0, i).mapToLong(index -> {
            long count = list.stream().map(it -> Pair.of(it.getKey(), code2(it.getValue()))).filter(it -> it.getKey().equals(it.getValue())).count();
            return count;

        }).sum();

        log.info("time:{} count:{}", sw.elapsed(TimeUnit.MILLISECONDS), sum);
    }


    private static Integer code(byte[] data) {
        //code
        JsonElement c = new Gson().fromJson(new String(data), JsonElement.class);
        if (c.isJsonObject()) {
            JsonElement e = c.getAsJsonObject().get("code");
            if (null == e) {
                return 200;
            }
            try {
                return e.getAsInt();
            } catch (Exception ex) {
                return 200;
            }
        }
        return 200;
    }


    private static Integer code2(byte[] data) {
        try {

            //  int code = Zhangzhiyong.code(data);
            int code = Zheng.code(data);
            return code;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


}
