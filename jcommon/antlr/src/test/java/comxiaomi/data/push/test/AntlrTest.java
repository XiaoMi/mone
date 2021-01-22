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

package comxiaomi.data.push.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.data.push.antlr.expr.Expr;
import com.xiaomi.data.push.antlr.json.Json;
import lombok.Data;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntlrTest {


    @Data
    private static class Bean {
        private int id;
        private String name;


        public Bean(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }


    @Test
    public void testAntlr() {
        Map<String, Object> m = new HashMap<>();
        m.put("name", "zzy");

        Bean bean = new Bean(1, "lucy");
        m.put("lucy", bean);


        String res = Expr.result(m, "result{name}");
        System.out.println(res);

        System.out.println(Expr.result(m, "result{lucy}.name"));
    }

    @Test
    public void testAntlrResult() {
        Map<String, Object> m = new HashMap<>();
        m.put("name", "zzy");

        Bean bean = new Bean(1, "lucy");
        m.put("lucy", bean);


        String res = Expr.result(m, "result{name}");
        System.out.println(res);

        System.out.println(Expr.result(m, "result{lucy}.name"));
    }


    @Test
    public void testAntlr2() {
        String str = "{\"name\":\"zzy\",\"ids\":[1,2,3,4]}";

//        JsonObject jo = new Gson().fromJson(str,JsonObject.class);
//        String name = jo.get("name").getAsString();
//        System.out.println(name);
//
//        int v = jo.getAsJsonArray("ids").get(2).getAsInt();
//        System.out.println(v);


        Object res = Expr.params(str, "params.json().get(name:string).getAsString()");
        System.out.println(res);
        res = Expr.params(str, "params.json().get(ids:string).get(2:int).getAsInt()");
        System.out.println(res);

    }


    @Test
    public void testAntlr3() {
        String str = "111";
        Object data = Expr.result(str, "result");
        System.out.println(data);
    }


    @Test
    public void testAntlr4() {
        byte[] str = "{\"name\":\"zzy\"}".getBytes();
        Map data = (Map) Expr.params(str, "params.toMap()");
        System.out.println(data);
        System.out.println(data.get("name"));
    }


    @Test
    public void testList() {
        byte[] str = "{'a':[{\"gid\":100311}],'b':[\"11\",\"11\",\"11\"]}".getBytes();
        Object params1 = Expr.params(str, "params.toMap(){b}");
        System.out.println(params1);
    }


    /**
     * list中取值
     */
    @Test
    public void testList2() {
        List<Integer> list = Lists.newArrayList(1, 2, 3, 4);
        Object params1 = Expr.params(list, "params[1]");
        System.out.println(params1);
    }

    /**
     * 支持把字符串变为list
     */
    @Test
    public void testList3() {
        byte[] str = "['1','3','4']".getBytes();
        Object params1 = Expr.params(str, "params.toList()[2]");
        System.out.println(params1);


        Object res = Expr.result(str, "result.toList()[0]");
        System.out.println(res);
    }


    @Test
    public void testJson() {
        HashMap<String, String> m = Maps.newHashMap();
        m.put("$zzy", "dddd");
        String str = Json.json("{\"name\":$zzy}", m);
        System.out.println(str);
    }


    @Test
    public void testJson2() {
        HashMap<String, String> m = Maps.newHashMap();
        m.put("$list", "[1,3,4]");
        String str = Json.json("$list", m);
        System.out.println(str);
    }


    @Test
    public void testJson3() {
        HashMap<String, String> m = Maps.newHashMap();
        m.put("$lista", "[{\"gid\":100311.0}]");
        String str = Json.json("$lista", m);
        System.out.println(str);
    }

}
