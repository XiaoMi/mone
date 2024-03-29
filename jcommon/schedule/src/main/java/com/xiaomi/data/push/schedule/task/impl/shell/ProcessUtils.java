package com.xiaomi.data.push.schedule.task.impl.shell;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class ProcessUtils {

    public static Pair<Integer, List<String>> process(String path, String... cmds) {
        try {
            boolean isWindows = System.getProperty("os.name")
                    .toLowerCase().startsWith("windows");

            ProcessBuilder builder = new ProcessBuilder();
            ArrayList<String> list = null;
            if (isWindows) {
                list = Lists.newArrayList("cmd.exe", "/c");
            } else {
                list = Lists.newArrayList("sh", "-c");
            }
            list.addAll(Arrays.stream(cmds).collect(Collectors.toList()));
            builder.command(list);
            builder.directory(new File(path));
            Process process = builder.start();

            List<String> data = Lists.newArrayList();
            StreamGobbler streamGobbler =
                    new StreamGobbler(process.getInputStream(), (it) -> {
                        data.add(it);
                    });
            streamGobbler.run();
            int exitCode = process.waitFor();
            return Pair.of(exitCode, data);
        } catch (Throwable ex) {
            return Pair.of(-1, Lists.newArrayList(ex.getMessage()));
        }
    }


    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }


}
