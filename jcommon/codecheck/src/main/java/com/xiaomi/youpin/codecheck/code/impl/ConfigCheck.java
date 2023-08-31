package com.xiaomi.youpin.codecheck.code.impl;

import com.xiaomi.youpin.codecheck.CommonUtils;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhangping17
 */
@Slf4j
public class ConfigCheck {

    private final static String PREFIX_PROPERTIES = ".properties";
    private final static String PREFIX_YML = ".yml";
    private final static String PREFIX_YAML = ".yaml";
    private final static String PREFIX_JAR = ".jar";
    private final static String PREFIX_CLASS = ".class";
    private final static String PREFIX_JAVA = ".java";
    private final static String PREFIX_ORI = ".original";

    private static final String DESC_1 = "file should avoid IP addresses";
    private static final String CHINA_DESC_1 = "文件内容应该避免包含ip地址";

    public Map<String, List<CheckResult>> configCheck(String path){
        Map<String, List<CheckResult>> mapRes = new HashMap<>();
        List<File> fileList = CommonUtils.searchNoFiles(new File(path), new ArrayList<String>() {{
            add(PREFIX_JAR);
            add(PREFIX_CLASS);
            add(PREFIX_JAVA);
            add(PREFIX_ORI);
        }});

        mapRes = check(fileList);
        return mapRes;
    }

    public Map<String, List<CheckResult>> check(List<File> files){
        Map<String, List<CheckResult>> mapRes = new HashMap<>();
        for (File file : files){
            List<CheckResult> checkResults = new ArrayList<>();
            Properties properties = new Properties();
            if (file.getName().endsWith(PREFIX_PROPERTIES)){
                properties = getProperties(file);
            } else if (file.getName().endsWith(PREFIX_YML) || file.getName().endsWith(PREFIX_YAML)){
                properties = getPropertiesOfYml(file);
            } else if (!file.getName().contains(".")) {
                continue;
            } else {
                try {
                    String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                    if(true == CommonUtils.hasIPv4(content.toString())){
                        checkResults.add(CheckResult.getErrorRes("file name: " + file.getPath(), DESC_1, CHINA_DESC_1));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (Map.Entry entry : properties.entrySet()){
                if(true == CommonUtils.hasIPv4(entry.getValue().toString())){
                    checkResults.add(CheckResult.getErrorRes("file name: " + file.getPath(), DESC_1, CHINA_DESC_1));
                    break;
                }
            }
            if (!CollectionUtils.isEmpty(checkResults)){
                mapRes.put(file.getPath(), checkResults);
            }
        }
        return mapRes;
    }

    public Properties getProperties(File file) {
        Properties properties = new Properties();
        try {
            InputStream inStream = new FileInputStream(file);
            properties.load(inStream);
        } catch (Exception e){
            log.error("propertiesCheck fail, path:{},", file.getPath(), e);
        }
        return properties;
    }

    public Properties getPropertiesOfYml(File file) {
        Properties properties = new Properties();
        try {
            InputStream inputStream = new FileInputStream(file);
            YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
            Resource resource = new InputStreamResource(inputStream);
            yamlPropertiesFactoryBean.setResources(resource);
            properties = yamlPropertiesFactoryBean.getObject();
        } catch (FileNotFoundException e) {
            log.error("ymlCheck fail, path:{},", file.getPath(), e);
        }
        return properties;
    }
}
