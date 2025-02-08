package run.mone.mcp.playwright.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import run.mone.mcp.playwright.bo.SelectorConfig;
import run.mone.mcp.playwright.common.Result;
import run.mone.mcp.playwright.service.ConfigService;

@Slf4j
@Controller
@RequestMapping("/config")
public class ConfigController {

    @Resource
    private ConfigService configService;

    @PostMapping("/create")
    @ResponseBody
    public String handleConfig(@RequestBody SelectorConfig config) {
        log.info("开始创建配置: {}", config);
        configService.createConfig(config);
        log.info("配置创建完成");
        return "success";
    }

    @PostMapping("/update")
    @ResponseBody
    public String handleConfigUpdate(@RequestBody SelectorConfig config) {
        log.info("开始更新配置, id: {}, config: {}", config.getId(), config);
        configService.updateConfig(config.getId(), config);
        log.info("配置更新完成");
        return "success";
    }   

    @PostMapping("/delete")
    @ResponseBody
    public String handleConfigDelete(@RequestParam(name = "id") String id) {
        log.info("开始删除配置, id: {}", id);
        configService.deleteConfig(id);
        log.info("配置删除完成");
        return "success";
    }

    @GetMapping("/get")
    @ResponseBody
    public SelectorConfig handleConfigGet(@RequestParam(name = "id") String id) {
        log.info("开始获取配置, id: {}", id);
        SelectorConfig config = configService.getConfig(id);
        log.info("配置获取完成");
        return config;
    }

    @GetMapping("/list")
    @ResponseBody
    public List<SelectorConfig> handleConfigList() {
        log.info("开始获取配置列表");
        List<SelectorConfig> configs = configService.getAllConfigs();
        log.info("配置列表获取完成");
        return configs;
    }
}
