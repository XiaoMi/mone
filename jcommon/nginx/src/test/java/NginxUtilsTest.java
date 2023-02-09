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

import com.xiaomi.youpin.nginx.NginxUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Zheng Xu zheng.xucn@outlook.com
 */

public class NginxUtilsTest {

    private static final String CONFIG_UPSTREAM = "upstream backend { server abc ;server   www.google.com;  server   www.baidu.com; }";
    private static final String CONFIG_ABC = " abc backend {  server abc ;server www.google.com; server www.baidu.com; }";
    private static final String CONFIG_EMPTY_SERVER_LIST = " upstream backend {}";
    private static final String NAME_BACKEND = "backend";
    private static final String NAME_DYNAMIC = "dynamic";

    private static final String SERVER_GOOGLE = "server www.google.com";
    private static final String SERVER_BAIDU = "server www.baidu.com";
    private static final String SERVER_IP_1 = "server 127.0.0.1";
    private static final String SERVER_IP_2 = "server      127.0.0.1   asdfa  dsfafa ";
    private static final String SERVER_ABC = "server abc";

    private static final String TEST_FILE = "upstreams.conf";

    private static String commentFile;

    private static String readFile(String file) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    @BeforeClass
    public static void commonData() throws IOException {
        commentFile = readFile(TEST_FILE);
    }

    @Test
    public void removeServer_alreadyRemoved() {
        String config = NginxUtils.removeServer(CONFIG_UPSTREAM, NAME_BACKEND, SERVER_IP_1);
        List<String> servers = NginxUtils.getServers(config, NAME_BACKEND);
        assertEquals(3, servers.size());
    }

    @Test
    public void removeServer_middle_server() {
        String config = NginxUtils.removeServer(CONFIG_UPSTREAM, NAME_BACKEND, SERVER_GOOGLE);
        List<String> servers = NginxUtils.getServers(config, NAME_BACKEND);
        assertEquals(2, servers.size());
    }

    @Test
    public void removeServer_top_server() {
        String config = NginxUtils.removeServer(CONFIG_UPSTREAM, NAME_BACKEND, SERVER_ABC);
        List<String> servers = NginxUtils.getServers(config, NAME_BACKEND);
        assertEquals(2, servers.size());
    }

    @Test
    public void removeServer_bottom_server() {
        String config = NginxUtils.removeServer(CONFIG_UPSTREAM, NAME_BACKEND, SERVER_BAIDU);
        List<String> servers = NginxUtils.getServers(config, NAME_BACKEND);
        assertEquals(2, servers.size());
    }


    @Test
    public void getServers() {
        List<String> servers = NginxUtils.getServers(CONFIG_UPSTREAM, NAME_BACKEND);
        assertEquals(3, servers.size());
        HashSet<String> set = new HashSet<>();
        set.addAll(servers);
        assertTrue(set.contains(SERVER_ABC));
        assertTrue(set.contains(SERVER_GOOGLE));
        assertTrue(set.contains(SERVER_BAIDU));
    }


    @Test
    public void getServers_Comment() {

        List<String> servers = NginxUtils.getServers(commentFile, "staging_detail_backend");
        System.out.println(servers);
        assertEquals(1, servers.size());
    }

    @Test
    public void getServers_onlyComments() {
        long start = System.currentTimeMillis();
        List<String> servers = NginxUtils.getServers(commentFile, "abc");
        System.out.println(System.currentTimeMillis()- start);
        System.out.println(servers);
        assertEquals(0, servers.size());
    }

    @Test
    public void getServers_empty_config() {
        List<String> servers = NginxUtils.getServers(commentFile, "abc");
        assertEquals(0, servers.size());
    }

    @Test
    public void getServers_Zheng() {
        List<String> servers = NginxUtils.getServers(commentFile, "zheng");
        System.out.println(servers);
        assertEquals(2, servers.size());

        String config = NginxUtils.removeServer(commentFile, "zheng", "yeah great");
        servers = NginxUtils.getServers(config, "zheng");
        System.out.println(servers);
        assertEquals(1, servers.size());
    }

    @Test
    public void getServers_name_not_found() {
        List<String> servers = NginxUtils.getServers(CONFIG_UPSTREAM, NAME_DYNAMIC);
        assertEquals(0, servers.size());
    }

    @Test
    public void getServers_key_not_found() {
        List<String> servers = NginxUtils.getServers(CONFIG_ABC, NAME_DYNAMIC);
        assertEquals(0, servers.size());
    }

    @Test
    public void getServers_EMPTY_SERVER_LIST() {
        List<String> servers = NginxUtils.getServers(CONFIG_EMPTY_SERVER_LIST, NAME_BACKEND);
        assertEquals(0, servers.size());
    }

    @Test
    public void addServer_emptyConfig() {
        String config = NginxUtils.addServer(CONFIG_EMPTY_SERVER_LIST, NAME_BACKEND, SERVER_GOOGLE);
        List<String> servers = NginxUtils.getServers(config, NAME_BACKEND);
        assertEquals(1, servers.size());
        assertEquals(servers.get(0), SERVER_GOOGLE);

        config = NginxUtils.addServer(config, NAME_BACKEND, SERVER_BAIDU);
        servers = NginxUtils.getServers(config, NAME_BACKEND);
        assertEquals(2, servers.size());
        assertEquals(servers.get(1), SERVER_BAIDU);
    }

    @Test
    public void addServer() {
        String config = NginxUtils.addServer(CONFIG_UPSTREAM, NAME_BACKEND, SERVER_IP_2);
        List<String> servers = NginxUtils.getServers(config, NAME_BACKEND);
        assertEquals(4, servers.size());
    }

    @Test
    public void addServer_duplicate() {
        String config = NginxUtils.addServer(CONFIG_UPSTREAM, NAME_BACKEND, SERVER_GOOGLE);
        List<String> servers = NginxUtils.getServers(config, NAME_BACKEND);
        assertEquals(3, servers.size());
    }

    @Test
    public void addServer_addMultipleServers() {
        List<String> serversToAdd = new LinkedList<>();
        serversToAdd.add(SERVER_IP_1);
        serversToAdd.add(SERVER_IP_2);
        String config = NginxUtils.addServer(CONFIG_UPSTREAM, NAME_BACKEND, serversToAdd);
        List<String> servers = NginxUtils.getServers(config, NAME_BACKEND);
        assertEquals(5, servers.size());
    }

    @Test
    public void testFindList() throws IOException {
        String config = new String(Files.readAllBytes(Paths.get("/tmp/upstreams.conf")));


        Pattern p = Pattern.compile("upstream staging_login_backend.*\\{.*?\\}", Pattern.DOTALL);

        Matcher m = p.matcher(config);

        while (m.find()) {
            System.out.println("--->" + m.group());
        }
    }


}
