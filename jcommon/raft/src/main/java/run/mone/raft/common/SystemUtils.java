/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package run.mone.raft.common;

import com.google.common.collect.Lists;
import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author mone
 */
@Slf4j
public class SystemUtils {

    /**
     * Standalone mode or not(独立运行模式)
     * property mone.raft.standalone=true
     * -Dmone.raft.standalone=true
     */
    public static final boolean STANDALONE_MODE = Boolean.getBoolean(Constants.STANDALONE_MODE_PROPERTY_NAME);

    public static final String STANDALONE_MODE_ALONE = "standalone";
    public static final String STANDALONE_MODE_CLUSTER = "cluster";

    /**
     * server
     */
    public static final String FUNCTION_MODE = System.getProperty(Constants.FUNCTION_MODE_PROPERTY_NAME);

    public static final String FUNCTION_MODE_CONFIG = "config";
    public static final String FUNCTION_MODE_NAMING = "naming";


    private static OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();

    /**
     * nacos local ip
     */
    public static final String LOCAL_IP = InetUtils.getSelfIp();


    /**
     * The key of nacos home.
     */
    public static final String NACOS_HOME_KEY = "nacos.home";

    /**
     * The home of nacos.
     */
    public static final String NACOS_HOME = getNacosHome();

    /**
     * The file path of cluster conf.
     */
    public static final String CLUSTER_CONF_FILE_PATH = getClusterConfFilePath();

    public static List<String> getIPsBySystemEnv(String key) {
        String env = getSystemEnv(key);
        List<String> ips = new ArrayList<>();
        if (StringUtils.isNotEmpty(env)) {
            ips = Arrays.asList(env.split(","));
        }
        return ips;
    }

    public static String getSystemEnv(String key) {
        return System.getenv(key);
    }

    public static float getLoad() {
        return (float) operatingSystemMXBean.getSystemLoadAverage();
    }

    public static float getCPU() {
        return (float) operatingSystemMXBean.getSystemCpuLoad();
    }

    public static float getMem() {
        return (float) (1 - (double) operatingSystemMXBean.getFreePhysicalMemorySize() / (double) operatingSystemMXBean
                .getTotalPhysicalMemorySize());
    }

    public static String getNacosHome() {
        String nacosHome = System.getProperty(NACOS_HOME_KEY);
        if (StringUtils.isBlank(nacosHome)) {
            nacosHome = System.getProperty("user.home") + File.separator + "moneraft";
        }
        return nacosHome;
    }

    public static String getConfFilePath() {
        return NACOS_HOME + File.separator + "conf" + File.separator;
    }

    private static String getClusterConfFilePath() {
        return NACOS_HOME + File.separator + "conf" + File.separator + "mone_raft.conf";
    }

    /**
     * example /home/work/moneraft/conf/mone_raft.conf
     *
     * @return
     * @throws IOException
     */
    public static List<String> readClusterConf() {
        try {
            List<String> instanceList = new ArrayList<String>();
            try (Reader reader = new InputStreamReader(new FileInputStream(new File(CLUSTER_CONF_FILE_PATH)),
                    StandardCharsets.UTF_8)) {
                List<String> lines = IoUtils.readLines(reader);
                String comment = "#";
                for (String line : lines) {
                    String instance = line.trim();
                    if (instance.startsWith(comment)) {
                        // # it is ip
                        continue;
                    }
                    if (instance.contains(comment)) {
                        // 192.168.71.52:8848 # Instance A
                        instance = instance.substring(0, instance.indexOf(comment));
                        instance = instance.trim();
                    }
                    int multiIndex = instance.indexOf(Constants.COMMA_DIVISION);
                    if (multiIndex > 0) {
                        // support the format: ip1:port,ip2:port  # multi inline
                        instanceList.addAll(Arrays.asList(instance.split(Constants.COMMA_DIVISION)));
                    } else {
                        //support the format: 192.168.71.52:8848
                        instanceList.add(instance);
                    }
                }
                return instanceList;
            }
        } catch (Throwable ex) {
            log.debug(ex.getMessage());
        }
        return Lists.newArrayList();
    }

    public static void writeClusterConf(String content) throws IOException {
        IoUtils.writeStringToFile(new File(CLUSTER_CONF_FILE_PATH), content, "UTF-8");
    }

}
