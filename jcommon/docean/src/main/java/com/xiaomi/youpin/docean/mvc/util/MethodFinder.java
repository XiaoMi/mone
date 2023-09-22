package com.xiaomi.youpin.docean.mvc.util;

import com.google.common.base.Joiner;
import com.xiaomi.youpin.docean.mvc.HttpRequestMethod;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2023/8/28 16:35
 */
public class MethodFinder {


    public static HttpRequestMethod find(String path, ConcurrentHashMap<String, HttpRequestMethod> requestMethodMap) {
        HttpRequestMethod hrm = requestMethodMap.get(path);
        if (null != hrm) {
            return hrm;
        }

        //Support fuzzy matching.
        String[] array = path.split("/");
        if (array.length > 1) {
            array[array.length - 1] = "*";
            String p = Joiner.on("/").join(array);
            HttpRequestMethod m = requestMethodMap.get(p);
            if (null != m) {
                return m;
            }
        }

        //rate limited or exceeded quota(/a/** match /a/b/c /a/b/d)
        final String p = path;
        Optional<Map.Entry<String, HttpRequestMethod>> optional = requestMethodMap.entrySet().stream().filter(it -> {
            String key = it.getKey();
            if (key.endsWith("/**")) {
                key = key.replace("/**", "");
                return p.startsWith(key);
            }
            return false;
        }).findAny();
        if (optional.isPresent()) {
            return optional.get().getValue();
        }
        return null;
    }

}
