package run.mone.moner.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import run.mone.moner.server.bo.McpModelSetting;
import run.mone.moner.server.bo.McpModelSettingDTO;

@Slf4j
@Controller
@RequestMapping("/mcp/model/setting")
public class McpModelSettingController {

    /**
     * 获取所有MCP模型设置
     */
    @GetMapping("/all")
    @ResponseBody
    public String getAllSettings() {
        log.info("开始获取所有MCP模型设置");
        try {
            String settings = McpModelSetting.getAllMcpModelSetting();
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
    public McpModelSettingDTO getSettings() {
        log.info("开始获取MCP模型设置对象");
        try {
            McpModelSettingDTO settings = McpModelSetting.getMcpModelSetting();
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
    public String saveSettings(@RequestBody String content) {
        log.info("开始保存MCP模型设置");
        try {
            McpModelSetting.saveMcpModelSetting(content);
            log.info("保存MCP模型设置成功");
            return "success";
        } catch (Exception e) {
            log.error("保存MCP模型设置失败", e);
            return "error";
        }
    }
} 