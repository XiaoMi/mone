/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package run.mone.moner.server.mcp;

import run.mone.hive.mcp.hub.McpHub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class McpHubHolder {

    private static final Map<String, McpHub> FROM_MCP_HUB = new ConcurrentHashMap<>();

    public static void put(String from, McpHub mcpHub){
        FROM_MCP_HUB.put(from, mcpHub);
    }

    public static McpHub get(String from){
        return FROM_MCP_HUB.get(from);
    }
}
