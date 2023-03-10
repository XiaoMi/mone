package com.xiaomi.youpin.codecheck.code.impl;

import com.xiaomi.youpin.codecheck.CommonUtils;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author zhangping17
 */
@Slf4j
public class ConfigCheck {
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
            // property,yml文件之所以不用Properties和snakeyaml库来解析，是因为这些库读的数据不会包括注释掉的行
            if (!file.getName().contains(".")) {
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
            if (checkResults != null && !checkResults.isEmpty()){
                mapRes.put(file.getPath(), checkResults);
            }
        }
        return mapRes;
    }
}
