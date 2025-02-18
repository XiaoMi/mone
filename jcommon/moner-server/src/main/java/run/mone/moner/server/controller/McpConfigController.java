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
package run.mone.moner.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import run.mone.moner.server.mcp.McpOperationService;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;

@Slf4j
@Controller
@RequestMapping("/mcp/config")
public class McpConfigController {

    @Autowired
    private McpOperationService mcpOperationService;

    /**
     * 初始化MCP配置信息
     */
    @GetMapping("/init")
    @ResponseBody
    public Result<String> initMcpConfig(@RequestParam(name = "from") String from) {
        log.info("mcp init from {}", from);
        try {
            mcpOperationService.initMcpHub(from);
            return Result.success("");
        } catch (Exception e) {
            log.error("init mcp config error", e);
            return Result.fail(GeneralCodes.InternalError, "init mcp config error");
        }
    }

    /**
     * 获取MCP配置信息
     */
    @GetMapping("/fetch")
    @ResponseBody
    public Result<String> fetchMcpConfig(@RequestParam(name = "from") String from) {
        log.info("mcp fetch from {}", from);
        try {
            return Result.success(mcpOperationService.fetchMcpJson(from));
        } catch (Exception e) {
            log.error("fetch mcp config error", e);
            return Result.fail(GeneralCodes.InternalError, "fetch mcp config error");
        }
    }

    /**
     * 获取MCP服务器状态
     * @param mcpServerName 服务器名称（可选）
     */
    @GetMapping("/server/status")
    @ResponseBody
    public Result<String> getMcpServerStatus(
            @RequestParam(name = "from") String from,
            @RequestParam(name = "mcpServerName") String mcpServerName) {
        log.info("mcp server status from {}", from);
        try {
            return Result.success(mcpOperationService.fetchMcpServerStatus(from, mcpServerName));
        } catch (Exception e) {
            log.error("get mcp server status error", e);
            return Result.fail(GeneralCodes.InternalError, "get mcp server status error");
        }
    }

    /**
     * 打开MCP配置文件
     */
    @GetMapping("/server/open/file")
    @ResponseBody
    public Result<Void> openMcpFile(@RequestParam(name = "from") String from) {
        log.info("begin mcp_server_open_file");
        try {
            mcpOperationService.openMcpFileSettings(from);
            return Result.success(null);
        } catch (Exception e) {
            log.error("open mcp file error", e);
            return Result.fail(GeneralCodes.InternalError, "open mcp file error");
        }
    }

    /**
     * 重试MCP服务器连接
     * @param mcpServerName 服务器名称（可选）
     */
    @GetMapping("/server/retry/connection")
    @ResponseBody
    public Result<Void> retryMcpServerConnection(@RequestParam(name = "mcpServerName") String mcpServerName,
                                                 @RequestParam(name = "from") String from) {
        log.info("begin mcp_server_retry_connection");
        try {
            mcpOperationService.RetryMcpServerConnection(from, mcpServerName);
            return Result.success(null);
        } catch (Exception e) {
            log.error("retry mcp server connection error", e);
            return Result.fail(GeneralCodes.InternalError, "retry mcp server connection error");
        }
    }

    /**
     * 获取MCP服务器工具列表
     * @param mcpServerName 服务器名称（可选）
     */
    @GetMapping("/fetch/tools")
    @ResponseBody
    public Result<String> fetchMcpTools(@RequestParam(name = "mcpServerName") String mcpServerName,
                                        @RequestParam(name = "from") String from) {
        log.info("begin mcp_fetch_tools");
        try {
            return Result.success(mcpOperationService.fetchMcpServerTools(from, mcpServerName));
        } catch (Exception e) {
            log.error("fetch mcp tools error", e);
            return Result.fail(GeneralCodes.InternalError, "fetch mcp tools error");
        }
    }

    /**
     * 获取MCP服务器版本
     * @param mcpServerName 服务器名称
     */
    @GetMapping("/server/version")
    @ResponseBody
    public Result<String> getMcpServerVersion(@RequestParam(name = "mcpServerName") String mcpServerName,
                                              @RequestParam(name = "from") String from) {
        log.info("begin mcp_server_version");
        try {
            return Result.success(mcpOperationService.fetchMcpServerVersion(from, mcpServerName));
        } catch (Exception e) {
            log.error("get mcp server version error", e);
            return Result.fail(GeneralCodes.InternalError, "get mcp server version error");
        }
    }

    /**
     * 监听MCP配置文件变更
     */
    @GetMapping("/listen")
    @ResponseBody
    public Result listenToMcpConfig(@RequestParam(name = "from") String from) {
        log.info("begin listen mcp config from {}", from);
        try {
            mcpOperationService.listenToTabSave(from);
            return Result.success(null);
        } catch (Exception e) {
            log.error("listen mcp config error", e);
            return Result.fail(GeneralCodes.InternalError, "listen mcp config error");
        }
    }
}
