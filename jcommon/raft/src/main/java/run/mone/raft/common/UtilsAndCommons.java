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

import java.util.concurrent.*;


/**
 * @author mone
 */
public class UtilsAndCommons {

    // ********************** Mone HTTP Context ************************ \\

    public static final String NACOS_HOME = SystemUtils.getNacosHome();

    public static final String MONE_SERVER_CONTEXT = "/mone";

    public static final String NACOS_SERVER_VERSION = "/v1";

    public static final String DEFAULT_NACOS_NAMING_CONTEXT = NACOS_SERVER_VERSION + "/ns";

    public static final String MONE_NAMING_CONTEXT = "/mone";


    // ********************** Nacos HTTP Context ************************ //

    public static final String MONE_RAFT_SERVER_HEADER = "Mone-Raft-Server";

    public static final String MONE_RAFT_VERSION = "0.0.1" ;


    public static final String DOMAINS_DATA_ID_PRE = "com.alibaba.nacos.naming.domains.meta.";


    public static final String SWITCH_DOMAIN_NAME = "00-00---000-NACOS_SWITCH_DOMAIN-000---00-00";

    public static final String UNKNOWN_SITE = "unknown";

    public static final String SERVER_VERSION = MONE_RAFT_SERVER_HEADER + ":" + MONE_RAFT_VERSION;

    public static final String MONE_RAFT_CLUSTER_IPS_ENV = "mone_raft_cluster_ips";

    public static final String IP_PORT_SPLITER = ":";

    public static final String NAMESPACE_SERVICE_CONNECTOR = "##";

    public static final ScheduledExecutorService SERVICE_SYNCHRONIZATION_EXECUTOR;

    public static final ScheduledExecutorService SERVICE_UPDATE_EXECUTOR;

    public static final ScheduledExecutorService INIT_CONFIG_EXECUTOR;

    public static final Executor RAFT_PUBLISH_EXECUTOR;

    static {

        SERVICE_SYNCHRONIZATION_EXECUTOR
            = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("nacos.naming.service.worker");
                t.setDaemon(true);
                return t;
            }
        });

        SERVICE_UPDATE_EXECUTOR
            = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("nacos.naming.service.update.processor");
                t.setDaemon(true);
                return t;
            }
        });

        INIT_CONFIG_EXECUTOR
            = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("nacos.naming.init.config.worker");
                t.setDaemon(true);
                return t;
            }
        });

        RAFT_PUBLISH_EXECUTOR
            = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("nacos.naming.raft.publisher");
                t.setDaemon(true);
                return t;
            }
        });

    }

    public static String getSwitchDomainKey() {
        return UtilsAndCommons.DOMAINS_DATA_ID_PRE + UtilsAndCommons.SWITCH_DOMAIN_NAME;
    }


    public static String assembleFullServiceName(String namespaceId, String serviceName) {
        return namespaceId + UtilsAndCommons.NAMESPACE_SERVICE_CONNECTOR + serviceName;
    }

    /**
     * Provide a number between 0(inclusive) and {@code upperLimit}(exclusive) for the given {@code string},
     * the number will be nearly uniform distribution.
     * <p>
     * <p>
     *
     * e.g. Assume there's an array which contains some IP of the servers provide the same service,
     * the caller name can be used to choose the server to achieve load balance.
     * <blockquote><pre>
     *     String[] serverIps = new String[10];
     *     int index = shakeUp("callerName", serverIps.length);
     *     String targetServerIp = serverIps[index];
     * </pre></blockquote>
     *
     * @param string     a string. the number 0 will be returned if it's null
     * @param upperLimit the upper limit of the returned number, must be a positive integer, which means > 0
     * @return a number between 0(inclusive) and upperLimit(exclusive)
     * @throws IllegalArgumentException if the upper limit equals or less than 0
     * @since 1.0.0
     * @author jifengnan
     */
    public static int shakeUp(String string, int upperLimit) {
        if (upperLimit < 1) {
            throw new IllegalArgumentException("upper limit must be greater than 0");
        }
        if (string == null) {
            return 0;
        }
        return (string.hashCode() & Integer.MAX_VALUE) % upperLimit;
    }

}
