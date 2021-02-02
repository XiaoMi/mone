/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.tesla.agent.test;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

public class DockerFileTest {


    @Test
    public void testAdd() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("/Users/zhangzhiyong/IdeaProjects/tesla/tesla-agent/src/main/resources/template/docker_file.tml"));

        int index = IntStream.range(0, lines.size()).filter(i -> {
            String str = lines.get(i);
            if (str.startsWith("COPY")) {
                return true;
            }
            return false;
        }).findFirst().getAsInt();

        System.out.println(lines + "," + index);

        lines.add(5,"apt-get install -y python3");

        System.out.println(lines);

    }
}
