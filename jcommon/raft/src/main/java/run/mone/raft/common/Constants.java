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

public interface Constants {

    /**
     * The System property name of  Standalone mode
     */
    String STANDALONE_MODE_PROPERTY_NAME = "mone.raft.standalone";

    /**
     * The System property name of  Function mode
     */
    String FUNCTION_MODE_PROPERTY_NAME = "nacos.functionMode";

    String NACOS_SERVER_IP = "nacos.server.ip";

    String USE_ONLY_SITE_INTERFACES = "nacos.inetutils.use-only-site-local-interfaces";
    String PREFERRED_NETWORKS = "nacos.inetutils.preferred-networks";
    String IGNORED_INTERFACES = "nacos.inetutils.ignored-interfaces";
    String IP_ADDRESS = "nacos.inetutils.ip-address";
    String PREFER_HOSTNAME_OVER_IP = "nacos.inetutils.prefer-hostname-over-ip";
    String SYSTEM_PREFER_HOSTNAME_OVER_IP = "nacos.preferHostnameOverIp";
    String COMMA_DIVISION = ",";

}
