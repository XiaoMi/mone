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

package com.xiaomi.youpin.codegen.test;

import com.xiaomi.youpin.codegen.*;
import com.xiaomi.youpin.codegen.bo.ApiHeaderBo;
import com.xiaomi.youpin.codegen.bo.Dependency;
import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenTest {


    @Test
    public void testFilter() throws Exception {
        FilterGen filterGen = new FilterGen();
        filterGen.generateAndZip("/tmp/workspace", "filter1", "com.xiaomi.youpin", "com.xiaomi.youpin.filter1", "dp", "0.0.1", "1000", "[]", "Filter1", "desc", "address", "true");
    }

    @Test
    public void testHttpRequestGen() throws Exception {
        HttpRequestGen httpRequestGen = new HttpRequestGen();
        httpRequestGen.generateJavaReq("getUserInfo",2,13,"[{\"paramNotNull\":\"0\",\"paramType\":\"13\",\"paramName\":\"obj\",\"paramKey\":\"subObj\",\"paramValue\":\"\",\"paramLimit\":\"\",\"paramNote\":\"\",\"paramValueList\":[],\"default\":0,\"childList\":[{\"paramNotNull\":\"0\",\"paramType\":\"0\",\"paramName\":\"sub_dzx\",\"paramKey\":\"subName\",\"paramValue\":\"\",\"paramLimit\":\"\",\"paramNote\":\"\",\"paramValueList\":[],\"default\":0,\"childList\":[]},{\"paramNotNull\":\"0\",\"paramType\":\"3\",\"paramName\":\"666\",\"paramKey\":\"subID\",\"paramValue\":\"\",\"paramLimit\":\"\",\"paramNote\":\"\",\"paramValueList\":[],\"default\":0,\"childList\":[]}]}]");
        httpRequestGen.generateJavaReq("getUserInfo",2,13,"[{\"paramNotNull\":\"0\",\"paramType\":\"13\",\"paramName\":\"obj\",\"paramKey\":\"subObj\",\"paramValue\":\"\",\"paramLimit\":\"\",\"paramNote\":\"\",\"paramValueList\":[],\"default\":0,\"childList\":[{\"paramNotNull\":\"0\",\"paramType\":\"0\",\"paramName\":\"sub_dzx\",\"paramKey\":\"subName\",\"paramValue\":\"\",\"paramLimit\":\"\",\"paramNote\":\"\",\"paramValueList\":[],\"default\":0,\"childList\":[]},{\"paramNotNull\":\"0\",\"paramType\":\"3\",\"paramName\":\"666\",\"paramKey\":\"subID\",\"paramValue\":\"\",\"paramLimit\":\"\",\"paramNote\":\"\",\"paramValueList\":[],\"default\":0,\"childList\":[]}]},{\"paramNotNull\":\"0\",\"paramType\":\"0\",\"paramName\":\"dzx\",\"paramKey\":\"userName\",\"paramValue\":\"\",\"paramLimit\":\"\",\"paramNote\":\"\",\"paramValueList\":[],\"default\":0,\"childList\":[]}]");

        List<ApiHeaderBo> headers = new ArrayList<>();
        ApiHeaderBo header1 = new ApiHeaderBo();
        header1.setHeaderName("cookies");
        header1.setHeaderValue("asfhpfhpeqjf11323");
        ApiHeaderBo header2 = new ApiHeaderBo();
        header2.setHeaderName("Accept");
        header2.setHeaderValue("true");
        headers.add(header1);
        headers.add(header2);
        Result<String> result = httpRequestGen.generateCurlReq(0,"/Api/getUserInfo",1,"[{\"paramNotNull\":\"0\",\"paramType\":\"13\",\"paramName\":\"obj\",\"paramKey\":\"subObj\",\"paramValue\":\"\",\"paramLimit\":\"\",\"paramNote\":\"\",\"paramValueList\":[],\"default\":0,\"childList\":[{\"paramNotNull\":\"0\",\"paramType\":\"0\",\"paramName\":\"sub_dzx\",\"paramKey\":\"subName\",\"paramValue\":\"\",\"paramLimit\":\"\",\"paramNote\":\"\",\"paramValueList\":[],\"default\":0,\"childList\":[]},{\"paramNotNull\":\"0\",\"paramType\":\"3\",\"paramName\":\"666\",\"paramKey\":\"subID\",\"paramValue\":\"\",\"paramLimit\":\"\",\"paramNote\":\"\",\"paramValueList\":[],\"default\":0,\"childList\":[]}]}]",headers);
        System.out.print(result.getData());
    }

    @Test
    public void testPlugin() throws Exception {
        PluginGen filterGen = new PluginGen();
        filterGen.generateAndZip("/tmp/workspace", "plugin1", "com.xiaomi.youpin", "com.xiaomi.youpin.plugin1", "dp", "0.0.1", "url");
    }

    @Test
    public void testPro() throws Exception {
        SpringBootProGen springBootProGen = new SpringBootProGen();
        springBootProGen.generateAndZip("/tmp/workspace", "project-one", "com.xiaomi.youpin", "com.xiaomi.youpin.projectone", "dfz", "0.0.1", false, "aaaa", "bbbb", 2);
    }

    @Test
    public void testCMi() throws Exception {
        CMiProGen cMiProGen = new CMiProGen();
        cMiProGen.generateAndZip("/tmp/workspace", "project-mimimi", "com.xiaomi.youpin", "com.xiaomi.youpin.projectmimimi", "dfz", "1.0.0", false, "aaaa", "bbbb", 2);
    }


    @Test
    public void testDoceanPro() throws Exception {
        DoceanProGen doceanProGen = new DoceanProGen();
        doceanProGen.generateAndZip("/tmp/workspace", "projectzjy", "com.xiaomi.youpin", "com.xiaomi.youpin.projectzjy", "zhangjunyi_gen", "0.0.1");
    }


    @Test
    public void testRcurve() throws Exception {
        RcurveGen gen = new RcurveGen();
        gen.generateAndZip("/tmp/a/", "project-one", "com.xiaomi.youpin", "com.xiaomi.youpin.projectone", "dfz", "0.0.1", false, "aaaa", "bbbb", 2);
    }

    @Test
    public void testCMiCustomDep() throws Exception {
        HashMap<String, ArrayList<Dependency>> dep = new HashMap<>();
        dep.put("api", new ArrayList<Dependency>() {{
            add(new Dependency(new HashMap<String, String>() {{
                put("groupId", "org.slf4j");
                put("artifactId", "slf4j-log4j12");
                put("version", "1.7.25");
            }}));
        }});
        dep.put("server", new ArrayList<Dependency>() {{
            add(new Dependency(new HashMap<String, String>() {{
                put("groupId", "org.slf4j");
                put("artifactId", "slf4j-log4j12");
                put("version", "1.7.25");
            }}));
            add(new Dependency(new HashMap<String, String>() {{
                put("groupId", "org.apache.dubbo");
                put("artifactId", "dubbo-dependencies-zookeeper");
                put("version", "2.7.3");
            }}));
        }});
        CMiProGen cMiProGen = new CMiProGen();
        cMiProGen.generateAndZip("/home/work", "project-mimimi", "com.xiaomi.youpin", "com.xiaomi.youpin.projectmimimi", "dfz", "1.0.0", false, "aaaa", "bbbb", 2, dep);
    }

    @Test
    public void testDDD() throws Exception {
        HashMap<String, ArrayList<Dependency>> dep = new HashMap<>();
//        dep.put("api", new ArrayList<Dependency>() {{
//            add(new Dependency(new HashMap<String, String>() {{
//                put("groupId", "org.slf4j");
//                put("artifactId", "slf4j-log4j12");
//                put("version", "1.7.25");
//            }}));
//        }});
//        dep.put("server", new ArrayList<Dependency>() {{
//            add(new Dependency(new HashMap<String, String>() {{
//                put("groupId", "org.slf4j");
//                put("artifactId", "slf4j-log4j12");
//                put("version", "1.7.25");
//            }}));
//            add(new Dependency(new HashMap<String, String>() {{
//                put("groupId", "org.apache.dubbo");
//                put("artifactId", "dubbo-dependencies-zookeeper");
//                put("version", "2.7.3");
//            }}));
//        }});
        new DDDProGen().generateAndZip("/tmp/work", "abcd", "com.xiaomi.youpin", "com.xiaomi.youpin.abcdefg", "dfz", "1.0.0", dep);
        //new CNSalesCrmGen().generateAndZip("/home/work", "testt", "com.xiaomi.test", "com.xiaomi.test.testt", "dfz", "1.0.0", dep);
    }
    @Test
    public void testFaas() throws Exception {
        FaasGen gen = new FaasGen();
        gen.generateAndZip("/tmp/work", "project-faas", "com.xiaomi.youpin", "com.xiaomi.youpin.projectmimimi", "dfz", "1.0.0","modulex","functiony");
    }
}
