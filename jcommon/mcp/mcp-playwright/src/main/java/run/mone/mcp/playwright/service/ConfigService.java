package run.mone.mcp.playwright.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import run.mone.mcp.playwright.bo.SelectorConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Service
public class ConfigService {

    private static final String CONFIG_FILE = "selector_config.json";
    private final CopyOnWriteArrayList<SelectorConfig> configs = new CopyOnWriteArrayList<>();
    private final Gson gson = new Gson();


    @PostConstruct
    public void init() {
        loadConfigs();
    }

    // 加载配置
    private void loadConfigs() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) {
            return;
        }
        try (Reader reader = new FileReader(file)) {
            List<SelectorConfig> loadedConfigs = gson.fromJson(reader, 
                new TypeToken<List<SelectorConfig>>(){}.getType());
            if (loadedConfigs != null) {
                configs.addAll(loadedConfigs);
            }
        } catch (IOException e) {
            log.error("Failed to load configs", e);
        }
    }

    // 保存配置
    private void saveConfigs() {
        try (Writer writer = new FileWriter(CONFIG_FILE)) {
            gson.toJson(configs, writer);
        } catch (IOException e) {
            log.error("Failed to save configs", e);
        }
    }

    // 创建配置
    public SelectorConfig createConfig(SelectorConfig config) {
        config.setId(UUID.randomUUID().toString());
        configs.add(config);
        saveConfigs();
        return config;
    }

    // 更新配置
    public SelectorConfig updateConfig(String id, SelectorConfig config) {
        for (int i = 0; i < configs.size(); i++) {
            if (configs.get(i).getId().equals(id)) {
                config.setId(id);
                configs.set(i, config);
                saveConfigs();
                return config;
            }
        }
        return null;
    }

    // 删除配置
    public boolean deleteConfig(String id) {
        boolean removed = configs.removeIf(config -> config.getId().equals(id));
        if (removed) {
            saveConfigs();
        }
        return removed;
    }

    // 获取所有配置
    public List<SelectorConfig> getAllConfigs() {
        return new ArrayList<>(configs);
    }

    // 根据ID获取配置
    public SelectorConfig getConfig(String id) {
        return configs.stream()
                .filter(config -> config.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
} 