/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package run.mone.mimeter.engine.service.test;

import com.xiaomi.data.push.antlr.expr.Expr;
import common.Util;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import run.mone.mimeter.engine.agent.bo.data.HttpData;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author goodjava@qq.com
 * @date 2022/6/2
 */
public class ExprTest {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class D {
        private int id;
        private String name;
    }

    @Data
    private class Result<D> {
        private int code;
        private String message;
        private D data;
    }


    @Test
    public void testResultExpr() {
        Map<String, Object> m = new HashMap<>();
        Result<String> r = new Result<>();
        r.setData("data:" + System.currentTimeMillis());
        m.put("id", r);
        String v = Expr.result(m, "result{id}.data");
        System.out.println(v);
    }


    @Test
    public void t() {
        List<Object> list = new ArrayList<>(2);
        list.add("dsds");
        System.out.println(list.get(0));
    }


    private static final Pattern EL_PATTERN = Pattern.compile("\\$\\{([^}]*)}");

    private void processDatasetUrl(HttpData httpData, TreeMap<String, List<String>> dataMap, int n) {
        if (dataMap == null) {
            return;
        }
        String k;
        String v = null;
        String url = httpData.getUrl();
        String tmpUrl = url;
        if (null != url && url.length() > 0) {
            Matcher m = EL_PATTERN.matcher(url);
            while (m.find()) {
                k = m.group(1);
                if (dataMap.get(k) != null && dataMap.get(k).size() != 0) {
                    int line = n % dataMap.get(k).size();
                    v = dataMap.get(k).get(line);
                }
                tmpUrl = Util.Parser.parse$(k, tmpUrl, v);
            }
        }
        httpData.setUrl(tmpUrl);
    }

    @Test
    public void testExpr() {

        String json = "{\"a\":\"aaa\",\"c\":2,\"b\":\"ccc\"}";

        TreeMap<String, Object> treeMap = Util.getGson().fromJson(json, TreeMap.class);
        System.out.println(treeMap.get(""));

    }
}
