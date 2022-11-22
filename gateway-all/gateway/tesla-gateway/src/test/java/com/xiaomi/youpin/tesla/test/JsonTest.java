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

package com.xiaomi.youpin.tesla.test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.schedule.task.graph.GraphTaskContext;
import com.xiaomi.data.push.schedule.task.graph.TaskVertexData;
import com.xiaomi.youpin.gateway.common.JsonUtils;
import com.xiaomi.youpin.infra.rpc.Result;
import com.youpin.xiaomi.tesla.bo.NodeInfo;
import lombok.Data;
import org.apache.dubbo.common.utils.StringUtils;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonTest {

    @Test
    public void testJson() {
        GraphTaskContext c = new GraphTaskContext();

        TaskVertexData data = new TaskVertexData();
        data.setIndex(0);

        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setBody("{\"name\":$name}");
        nodeInfo.setHeader("");
        Map<String, String> m = Maps.newHashMap();
        m.put("name", "");
        nodeInfo.setParamMap(m);
        data.setData(nodeInfo);

        c.getTaskList().add(data);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String str = gson.toJson(c);
        System.out.println(str);

        Type typeOfT = new TypeToken<GraphTaskContext<NodeInfo>>() {
        }.getType();
        GraphTaskContext c2 = new Gson().fromJson(str, typeOfT);
        System.out.println(c2.getTaskList().get(0));
    }


    @Test
    public void testJson2() {
        NullPointerException ex = new NullPointerException();
        System.out.println(ex.getMessage());
        Result<Object> res = Result.fromException(ex);
        System.out.println(new Gson().toJson(res));
    }


    @Data
    class Bean0 {
        private int bid;
        private String banme;
    }

    @Data
    class Bean {
        private int id;
        private String name;
        private String tel;
        private List<String> list;
        private Bean0 bean0;
    }


    @Test
    public void testJsonFilter() {
        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes arg0) {
                        if (arg0.getName().equals("name1")) {
                            return true;
                        }
                        return false;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> arg0) {
                        return false;
                    }
                }).create();

        Bean bean = new Bean();
        bean.setId(1);
        bean.setName("zzy");
        bean.setTel("123");
        bean.setList(Lists.newArrayList("a", "b", "c"));

        Bean0 bean0 = new Bean0();
        bean0.setBid(111);
        bean0.setBanme("bean0");
        bean.setBean0(bean0);

        String str = gson.toJson(bean);
        System.out.println(str);
    }


    @Test
    public void testJsonFilter2() {
        Bean bean = new Bean();
        bean.setId(1);
        bean.setName("zzy");
        bean.setTel("123");

        Gson gson = createGson("aa");

        System.out.println(gson.toJson(bean));
    }


    private Gson createGson(String filterField) {
        if (StringUtils.isNotEmpty(filterField)) {
            Set<String> keySet = Arrays.stream(filterField.split(",")).collect(Collectors.toSet());
            return new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return keySet.contains(f.getName());
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            }).create();
        } else {
            return new Gson();
        }
    }


    @Test
    public void TestJsonObject() {
        String str = "{\"id\":1,\"name\":\"zzy\",\"tel\":\"123\"}";
        Gson gson = new Gson();
        JsonObject jo = gson.fromJson(str, JsonObject.class);
//        jo.remove()
        System.out.println(jo);
    }

    @Test
    public void testJsonTree() {
        String json = "{\"id\":1,\"name\":\"zzy\",\"tel\":\"123\",\"list\":[\"a\",\"b\",\"c\"],\"bean0\":{\"bid\":111,\"banme\":\"bean0\"}}";
        String res = JsonUtils.parser(json, Sets.newHashSet("id", "list", "name"));
        System.out.println(res);
    }


}
