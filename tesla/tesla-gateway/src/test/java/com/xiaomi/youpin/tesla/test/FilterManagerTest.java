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
import com.xiaomi.youpin.gateway.filter.RequestFilter;
import com.xiaomi.youpin.gateway.netty.filter.FilterManager;
import io.netty.handler.codec.http.FullHttpResponse;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FilterManagerTest {


//    @Test
//    public void testLoadClass() throws InvocationTargetException, NoSuchMethodException, IOException, IllegalAccessException, NoSuchFieldException {
//        List<String> kv = Lists.newArrayList("tesla-filter-demo-1.0.0-SNAPSHOT.jar");
//
//        FilterManager manager = new FilterManager();
//
//        List<RequestFilter> filterList = manager.loadRequestFilter(kv);
//
//        filterList.stream().forEach(filter -> {
//            FullHttpResponse res = filter.doFilter(null, null, null, null);
//            System.out.println(res);
//        });
//
//        manager.releaseClassloader("tesla-filter-demo-1.0.0-SNAPSHOT");
//    }
//
//
//    @Test
//    public void testFindFile() throws IOException {
//        List<String> list = Files.find(Paths.get("/tmp/filter/"), 1, (path, attr) -> path.toString().endsWith("jar")).map(it -> it.toString()).collect(Collectors.toList());
//        System.out.println(list);
//    }
}
