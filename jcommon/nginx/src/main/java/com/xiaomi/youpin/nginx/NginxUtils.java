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

package com.xiaomi.youpin.nginx;


import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Zheng Xu zheng.xucn@outlook.com
 */


public class NginxUtils {

    private static final String UPSTREAM = "upstream";
    private static final char SPACE = ' ';
    private static final char LEFT = '{';
    private static final char RIGHT = '}';
    private static final char SEMI_COLON = ';';
    private static final String DELIMITER_SPACES = "[ ]+";
    private static final char POUND_SIGN = '#';
    private static final char NEW_LINE = '\n';


    private static String formatServerList(String config, int start, int end, List<String> servers) {
        String left = config.substring(0, start + 1);
        String right = config.substring(end);
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (String server : servers) {
            builder.append("\t" + server);
            builder.append(";\n");
        }
        return left + builder.toString() + right;
    }

    /**
     * 删除服务器
     *
     * @param config
     * @param name
     * @param serverToRemove
     * @return
     * @throws IllegalArgumentException 如果server缺少domain
     */
    public static String removeServer(String config, String name, String serverToRemove) {
        if (StringUtils.isEmpty(config) || StringUtils.isEmpty(name) || StringUtils.isEmpty(serverToRemove)) {
            return config;
        }

        List<String> serversToRemove = new LinkedList<>();
        serversToRemove.add(serverToRemove);
        return removeServer(config, name, serversToRemove);
    }

    /**
     * 删除多个服务器
     *
     * @param config
     * @param name
     * @param serversToRemove
     * @return
     * @throws IllegalArgumentException 如果server缺少domain
     */
    public static String removeServer(String config, String name, List<String> serversToRemove) {
        if (StringUtils.isEmpty(config) || StringUtils.isEmpty(name) || serversToRemove == null) {
            return config;
        }
        int[] startEndIndex = getStartEndIndex(config, name);
        if (startEndIndex == null) {
            return config;
        }

        HashSet<String> domainSet = new HashSet<>();
        for (String server : serversToRemove) {
            if (!isValidServer(server)) {
                throw new IllegalArgumentException("invalid server: " + server);
            }
            String domainToRemove = getDomainFromServer(server);
            domainSet.add(domainToRemove);
        }

        String serverString = config.substring(startEndIndex[0] + 1, startEndIndex[1]);
        boolean[] exclude = new boolean[serverString.length()];
        int start = 0;
        for (int i = 0; i < serverString.length(); i++) {
            char c = serverString.charAt(i);
            if (isComment(serverString, i)) {
                i++;
                while (i < serverString.length() && !(serverString.charAt(i) == '\n'
                    || serverString.charAt(i) == '\r')) {
                    i++;
                }
                start = i + 1;
                if (i + 1 < serverString.length() && serverString.charAt(i + 1) == '\n') {
                    i++;
                    start++;
                }
            } else if (c == SEMI_COLON) {
                if (i - start > 0) {
                    String[] serverElements = getServerElements(serverString.substring(start, i));
                    if (serverElements == null || serverElements.length < 2) {
                        throw new IllegalArgumentException("invalid config server strings: " + serverString);
                    }
                    String domain = serverElements[1];
                    if (domainSet.contains(domain)) {
                        for (int j = start; j <= i; j++) {
                            exclude[j] = true;
                        }
                    }
                }
                start = i + 1;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < serverString.length(); i++) {
            if (!exclude[i]) {
                builder.append(serverString.charAt(i));
            }
        }

        return config.substring(0, startEndIndex[0] + 1) + builder.toString() + config.substring(startEndIndex[1]);

    }

    private static boolean isComment(String serverString, int index) {
        /*
        return serverString.charAt(index) == POUND_SIGN && index - 1 >= 0 && (serverString.charAt(index - 1) == '\n'
            || serverString.charAt(index - 1) == '\r' || serverString.charAt(index - 1) == ' ' ||
            serverString.charAt(index - 1) == '\t' || (index - 2 >= 0 && serverString.substring(index - 2, index).equals("\r\n")));

         */
        return serverString.charAt(index) == POUND_SIGN;
    }

    private static boolean isValidServer(String server) {
        if (StringUtils.isEmpty(server)) {
            return false;
        }

        server = server.trim();
        for (int i = 0; i < server.length(); i++) {
            if (server.charAt(i) == SEMI_COLON) {
                return false;
            }
        }
        String[] items = server.split(DELIMITER_SPACES);
        return items.length >= 2;
    }


    private static String addServers(String config, String name, List<String> serversToAdd, int[] startEndIndex
        , List<String> existingServers) {
        if (StringUtils.isEmpty(config) || StringUtils.isEmpty(name) || serversToAdd == null
            || startEndIndex == null || existingServers == null) {
            return config;
        }

        HashSet<String> domainSet = new HashSet<>();
        for (String server : existingServers) {
            if (!isValidServer(server)) {
                throw new IllegalArgumentException("invalid server: " + server);
            }
            String domain = getDomainFromServer(server.trim());
            domainSet.add(domain);
        }
        List<String> canAdd = new LinkedList<>();
        for (String toAdd : serversToAdd) {
            if (!isValidServer(toAdd)) {
                throw new IllegalArgumentException("server to add is invalid: " + toAdd);
            }
            String[] serverElements = getServerElements(toAdd);
            if (serverElements == null || serverElements.length < 2) {
                throw new IllegalArgumentException("missing domain: " + toAdd);
            }
            String domain = serverElements[1];
            if (!domainSet.contains(domain)) {
                domainSet.add(domain);
                canAdd.add(String.join(" ", serverElements));
            }
        }

        if (canAdd.size() == 0) {
            return config;
        }
        StringBuilder builder = new StringBuilder();
        for (String S : canAdd) {
            builder.append(S);
            builder.append(";");
            builder.append("\n");
        }

        return config.substring(0, startEndIndex[1]) + builder.toString() + config.substring(startEndIndex[1]);

    }

    /**
     * 添加多个新服务器
     *
     * @param config
     * @param name
     * @throws IllegalArgumentException 如果server缺少domain
     *                                  * @return　添加以后的配置
     */
    public static String addServer(String config, String name, List<String> serversToAdd) {
        if (StringUtils.isEmpty(config) || StringUtils.isEmpty(name) || serversToAdd == null) {
            return config;
        }

        int[] startEndIndex = getStartEndIndex(config, name);
        if (startEndIndex == null) {
            return config;
        }
        List<String> existingServers = getServers(config, name, startEndIndex);

        return addServers(config, name, serversToAdd, startEndIndex, existingServers);
    }


    /**
     * 添加服务器
     *
     * @param config
     * @param name
     * @param serverToAdd
     * @throws IllegalArgumentException 如果server缺少domain
     * @return　添加以后的配置
     */
    public static String addServer(String config, String name, String serverToAdd) {
        if (StringUtils.isEmpty(config) || StringUtils.isEmpty(name) || !isValidServer(serverToAdd)) {
            return config;
        }

        List<String> serversToAdd = new LinkedList<>();
        serversToAdd.add(serverToAdd);
        return addServer(config, name, serversToAdd);
    }

    /**
     * Ex. server www.google.com
     *
     * @param server 服务参数
     * @return 返回服务配置的domain
     * @throws IllegalArgumentException 如果server缺少domain
     */
    private static String getDomainFromServer(String server) {
        if (StringUtils.isEmpty(server)) {
            return null;
        }
        server = server.trim();
        String[] list = server.split(DELIMITER_SPACES);
        if (list.length < 2) {
            throw new IllegalArgumentException("server string should have a domain, invalid server: " + server);
        }
        return list[1];
    }

    private static String[] getServerElements(String server) {
        if (StringUtils.isEmpty(server)) {
            return null;
        }
        server = server.trim();
        return server.split(DELIMITER_SPACES);
    }

    /**
     * 返回upstream config的服务器列表
     *
     * @param config 　config文件
     * @param name   　　名称
     * @return　服务器列表
     */
    public static List<String> getServers(String config, String name) {
        List<String> list = new LinkedList<>();
        if (StringUtils.isEmpty(config) || StringUtils.isEmpty(name)) {
            return list;
        }

        int[] startEndIndex = getStartEndIndex(config, name);
        return getServers(config, name, startEndIndex);
    }


    private static String getServerString(String config, String name, int[] startEndIndex) {

        if (StringUtils.isEmpty(config) || StringUtils.isEmpty(name) || startEndIndex == null) {
            return null;
        }

        if (startEndIndex[1] - startEndIndex[0] + 1 - 2 <= 0) {
            return null;
        }

        return config.substring(startEndIndex[0] + 1, startEndIndex[1]);
    }


    private static List<String> getServers(String config, String name, int[] startEndIndex) {
        List<String> list = new LinkedList<>();
        if (StringUtils.isEmpty(config) || StringUtils.isEmpty(name) || startEndIndex == null) {
            return list;
        }

        if (startEndIndex[1] - startEndIndex[0] + 1 - 2 <= 0) {
            return list;
        }

        String servers = getServerString(config, name, startEndIndex);
        if (StringUtils.isEmpty(servers)) {
            return list;
        }
        parseServerList(servers, list);
        return list;
    }


    private static void parseServerList(String servers, List<String> list) {
        int start = 0;
        for (int i = 0; i < servers.length(); i++) {
            char c = servers.charAt(i);
            if (c == SEMI_COLON) {
                if (i - start > 0) {
                    String[] segments = servers.substring(start, i).trim().split(DELIMITER_SPACES);
                    list.add(String.join(" ", segments));
                    start = i + 1;
                }
            } else if (isComment(servers, i)) {
                i++;
                while (i < servers.length() && !(servers.charAt(i) == '\n'
                    || servers.charAt(i) == '\r')) {
                    i++;
                }
                start = i + 1;
                if (i + 1 < servers.length() && servers.charAt(i + 1) == '\n') {
                    i++;
                    start++;
                }
            }
        }
    }


    private static int[] getStartEndIndex(String config, String name) {
        if (StringUtils.isEmpty(config) || StringUtils.isEmpty(name)) {
            return null;
        }
        name = name.trim();

        int start = 0;
        while (start < config.length()) {
            //   int index = config.indexOf(UPSTREAM, start);
            int index = indexOf(config, UPSTREAM, start);
            if (index != -1) {
                start += UPSTREAM.length();
                int[] matchIndex = matchIndex(config, name, index + UPSTREAM.length());
                if (matchIndex != null) {
                    return matchIndex;
                }
            } else {
                start++;
            }
        }
        return null;
    }

    private static int indexOf(String config, String key, int start) {
        for (int i = start; i <= config.length() - key.length(); i++) {
            if (isComment(config, i)) {
                i++;
                while (i < config.length() && !(config.charAt(i) == '\n'
                    || config.charAt(i) == '\r')) {
                    i++;
                }
                if (i + 1 < config.length() && config.charAt(i + 1) == '\n') {
                    i++;
                }
            } else {
                if (config.substring(i, i + key.length()).equals(key)) {
                    return i;
                }
            }
        }
        return -1;
    }


    private static int[] matchIndex(String config, String name, int index) {
        int i = index;
        if (i >= config.length() || config.charAt(i) != SPACE) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        while (i < config.length()) {
        //    if (config.charAt(i) == LEFT && config.substring(index, i).trim().equals(name)) {
            if (config.charAt(i) == LEFT && builder.toString().trim().equals(name)) {
                int end = getEndIndex(config, i + 1);
                if (end == -1) {
                    return null;
                }
                int[] result = {i, end};
                return result;
            }
            if(isComment(config, i)) {
                i++;
                while (i < config.length() && !(config.charAt(i) == '\n'
                    || config.charAt(i) == '\r')) {
                    i++;
                }
                if (i + 1 < config.length() && config.charAt(i + 1) == '\n') {
                    i++;
                }
            }
            builder.append(config.charAt(i));
            i++;
        }
        return null;
    }

    private static int getEndIndex(String config, int index) {
        while (index < config.length()) {
            if (config.charAt(index) == RIGHT) {
                return index;
            }
            index++;
        }
        return -1;
    }

}
