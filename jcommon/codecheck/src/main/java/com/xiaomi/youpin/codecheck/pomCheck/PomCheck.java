package com.xiaomi.youpin.codecheck.pomCheck;

import com.xiaomi.youpin.codecheck.CommonUtils;
import com.xiaomi.youpin.codecheck.po.CheckResult;
import com.xiaomi.youpin.codecheck.pomCheck.bo.CheckDep;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PomCheck {

    public Map<String, List<CheckResult>> pomCheck(String path) {
        List<File> files = CommonUtils.searchFiles(new File(path), "pom.xml");
        Map<String, List<CheckResult>> mapRes = new HashMap<>();
        for (File file : files) {
            List<CheckResult> saxRes = saxCheck(file);
            if (saxRes.size() > 0) {
                mapRes.put(file.getPath(), saxRes);
            }
        }

        return mapRes;
    }

    private List<CheckResult> saxCheck(File file) {
        List<CheckResult> res = new ArrayList<>();
        try {
            // 创建SAXReader
            SAXReader reader = new SAXReader();
            // 读取指定文件
            Document doc = reader.read(file);
            // 获取根节点list
            Element root = doc.getRootElement();
            // 获取list下的所有子节点emp
            List<Element> elements = root.elements();

            // 遍历集合取出没个节点的内容信息.
            Element dependencies = elements.stream().filter(it -> it.getName().equals("dependencies")).findFirst().orElse(null);
            if (dependencies == null) {
                return res;
            }

            List<Element> depList = dependencies.elements();

            CheckDep checkDep = new CheckDep();
            boolean bool = true;
            String str = "";
            for (Element element : depList) {
                String groupId = element.elementText("groupId");
                String artifactId = element.elementText("artifactId");
                String version = element.elementText("version");
                CheckResult checkRes = checkDep.checkDep(groupId, artifactId, version);
                if (CheckResult.getIntLevel(checkRes.getLevel()) > CheckResult.INFO) {
                    res.add(checkRes);
                }
            }

            return res;
        } catch (Exception e) {
            return res;
        }
    }
}
