package com.xiaomi.youpin.codecheck;

import com.xiaomi.youpin.codecheck.docCheck.JavaDocReader;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocCheck implements Serializable {

    public Map<String, String> getDoc(String path) {
        Map<String, String> res = new HashMap<>();

        if (path == null || path.equals("")) {
            return res;
        }

        //xxx.java校验
        List<File> files = CommonUtils.searchFiles(new File(path), ".java");
        files.stream().forEach(it -> {
            String javaDocRes = JavaDocReader.getDoc(it.getPath());
            if (!javaDocRes.equals("")) {
                res.put(it.getPath(), javaDocRes);
            }
        });

        return res;
    }
}
