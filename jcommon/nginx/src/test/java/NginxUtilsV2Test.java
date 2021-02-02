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

import com.google.common.collect.Lists;
import com.xiaomi.youpin.nginx.NginxUtilsV2;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class NginxUtilsV2Test {


    @Test
    public void testGetServerList() throws IOException {
        String path = this.getClass().getResource("/").getPath()+"nginx.conf";
        System.out.println(path);
        String config = new String(Files.readAllBytes(Paths.get(path)));
        List<String> list = NginxUtilsV2.getServers(config, "gateway");
        System.out.println(list);
    }


    @Test
    public void testAddServer() throws IOException {
        String path = this.getClass().getResource("/").getPath()+"nginx.conf";
        String config = new String(Files.readAllBytes(Paths.get(path)));
        String r = NginxUtilsV2.addServer(config, "gateway", Lists.newArrayList("xxxx", "xxxx"));
        System.out.println(r);
    }
}
