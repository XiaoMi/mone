///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.service;
//
//import com.alibaba.nacos.api.config.annotation.NacosValue;
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.api.naming.pojo.Instance;
//import com.google.common.collect.Maps;
//import com.google.gson.Gson;
//import com.xiaomi.data.push.client.HttpClientV2;
//import com.xiaomi.data.push.nacos.NacosConfig;
//import com.xiaomi.data.push.nacos.NacosNaming;
//import com.xiaomi.data.push.redis.Redis;
//import com.xiaomi.youpin.gwdash.bo.NacosLoginInfo;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import org.apache.dubbo.common.utils.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.List;
//
///**
// * @author goodjava@qq.com
// */
//@Service
//public class NacosService {
//
//    @Autowired
//    private NacosConfig nacosConfig;
//
//    @Autowired
//    private NacosNaming nacosNaming;
//
//    @Autowired
//    private Redis redis;
//
//    @Value("${nacos.api.url}")
//    private String url;
//
//    @Value("${dubbo.registry.address}")
//    private String nacosAddress;
//
//    @NacosValue("${nacos.username}")
//    private String username;
//
//    @NacosValue("${nacos.password}")
//    private String password;
//
//    private final static String DEFAULT_GROUP = "Public";
//
//
//    public String getConfig(String dataId, String group, long timeout) throws NacosException {
//        return nacosConfig.getConfigStr(dataId, group, timeout);
//    }
//
//    /**
//     * doc nacos open api: https://nacos.io/zh-cn/docs/open-api.html
//     */
//    public String getConfig(String dataId, String group, String namespaceId, long timeout) throws NacosException {
//        String params = "?search=accurate&pageNo=1&pageSize=10"
//                + "&dataId=" + dataId
//                + "&group=" + group;
//
//        // 在没有指定为 Public 时，不需要传 tenant 参数
//        if (!DEFAULT_GROUP.equals(namespaceId)) {
//            params += "&tenant=" + namespaceId;
//        }
//
//        return HttpClientV2.get(url + params, Maps.newHashMap());
//    }
//
//    public boolean setConfig(String dataId, String group, String content) throws NacosException {
//        return nacosConfig.publishConfig(dataId, group, content);
//    }
//
//    public boolean publishConfig(String dataId, String group, String content) throws NacosException {
//        return nacosConfig.publishConfig(dataId, group, content);
//    }
//
//    public String publishConfig(String dataId, String group, String namespaceId, String content) throws NacosException, UnsupportedEncodingException {
//        String postBody = "dataId=" + dataId
//                + "&group=" + group
//                + "&content=" + URLEncoder.encode(content, "UTF-8");
//        if (!DEFAULT_GROUP.equals(namespaceId)) {
//            postBody += "&tenant=" + namespaceId;
//        }
//
//        return HttpClientV2.post(url, postBody, Maps.newHashMap(), 5000);
//    }
//
//    public List<Instance> getInstances(String serviceName) throws NacosException {
//        return nacosNaming.getAllInstances(serviceName);
//    }
//
//    public String getAllServiceList(String keyword,String namespaceId) {
//        String accessToken = redis.get(Consts.GIT_ACCESS_TOKEN_CACHE);
//        if(StringUtils.isBlank(accessToken)){
//            NacosLoginInfo nacosLoginInfo = new Gson().fromJson(nacosNaming.login(username, password), NacosLoginInfo.class);
//            if (null != nacosLoginInfo && StringUtils.isNotEmpty(nacosLoginInfo.getAccessToken())) {
//                accessToken = nacosLoginInfo.getAccessToken();
//                redis.set(Consts.GIT_ACCESS_TOKEN_CACHE,accessToken,15*60000);
//            }
//        }
//        return nacosNaming.serviceList2(namespaceId, 1, 100, keyword, accessToken);
//    }
//}
