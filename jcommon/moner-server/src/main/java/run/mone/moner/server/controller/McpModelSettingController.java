package run.mone.moner.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import run.mone.moner.server.bo.McpModelSettingDTO;
import run.mone.moner.server.mcp.McpModelSettingService;

@Slf4j
@Controller
@RequestMapping("/mcp/model/setting")
public class McpModelSettingController {

    @Autowired
    private McpModelSettingService mcpModelSetting;

    /**
     * 获取所有MCP模型设置
     */
    @GetMapping("/all")
    @ResponseBody
    public String getAllSettings(@RequestParam String from) {
        log.info("开始获取所有MCP模型设置, from: {}", from);
        try {
            String settings = mcpModelSetting.getAllMcpModelSetting(from);
            log.info("获取MCP模型设置成功");
            return settings;
        } catch (Exception e) {
            log.error("获取MCP模型设置失败", e);
            return null;
        }
    }

    /**
     * 获取MCP模型设置对象
     */
    @GetMapping
    @ResponseBody
    public McpModelSettingDTO getSettings(@RequestParam String from) {
        log.info("开始获取MCP模型设置对象, from: {}", from);
        try {
            McpModelSettingDTO settings = mcpModelSetting.getMcpModelSetting(from);
            log.info("获取MCP模型设置对象成功");
            return settings;
        } catch (Exception e) {
            log.error("获取MCP模型设置对象失败", e);
            return null;
        }
    }

    /**
     * 保存MCP模型设置
     */
    @PostMapping
    @ResponseBody
    public String saveSettings(@RequestParam String from, @RequestBody String content) {
        log.info("开始保存MCP模型设置, from: {}", from);
        try {
            mcpModelSetting.saveMcpModelSetting(from, content);
            log.info("保存MCP模型设置成功");
            return "success";
        } catch (Exception e) {
            log.error("保存MCP模型设置失败", e);
            return "error";
        }
    }
} 