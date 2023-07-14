package com.xiaomi.mone.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.client.config.utils.MD5;
import com.google.gson.Gson;
import com.xiaomi.mone.app.AppBootstrap;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.app.api.model.HeraAppBaseQuery;
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupDataRequest;
import com.xiaomi.mone.app.api.model.project.group.ProjectGroupTreeNode;
import com.xiaomi.mone.app.auth.AuthorizationService;
import com.xiaomi.mone.app.common.Result;
import com.xiaomi.mone.app.service.impl.HeraAppBaseInfoService;
import com.xiaomi.mone.app.service.project.group.HeraProjectGroupService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2022/11/2 2:59 下午
 */
@Slf4j
@SpringBootTest(classes = AppBootstrap.class)
public class HeraProjectGroupTest {

    @Autowired
    HeraProjectGroupService projectGroupService;

    @Autowired
    AuthorizationService authorizationService;

    @NacosValue(value = "hera.auth.user",autoRefreshed = true)
    private String userName;

    @NacosValue(value = "hera.auth.pwd",autoRefreshed = true)
    private String passWord;

    @NacosValue(value = "hera.auth.secret",autoRefreshed = true)
    private String secret;

    @Test
    public void testAuth() throws InterruptedException {
        Long current = System.currentTimeMillis();

        StringBuilder secretPwdBuffer = new StringBuilder();
        String md5Pwd = MD5.getInstance().getMD5String(passWord);
        secretPwdBuffer.append(userName).append(md5Pwd).append(current);
        String sign = MD5.getInstance().getMD5String(secretPwdBuffer.toString());

        System.out.println("userName:" + userName + ";sign:"+sign+";timestamp:" + current);

//        Result result = authorizationService.fetchToken(userName, sign, current);
//        System.out.println("result === " + new Gson().toJson(result));
//
//        Boolean re = authorizationService.checkAuthorization((String) result.getData());
//        System.out.println("check token result:"+re);
//
//        Thread.sleep(3000);
//        Boolean re1 = authorizationService.checkAuthorization((String) result.getData());
//        System.out.println("check token result1:"+re1);
    }

    @Test
    public void testAuthorization(){
        Result rt = authorizationService.checkAuthorization("ac138023ce8ac1258bbb3c050f58769c");
        System.out.println("rt =========== " + new Gson().toJson(rt));
    }

    @Test
    public void searchGroupApps(){
        Result<List<HeraAppBaseInfoModel>> apps = projectGroupService.searchGroupApps("gaoxihui", 0,null, null, null, null);
        System.out.println(new Gson().toJson(apps));
    }

    @Test
    public void getFullTree(){
        Result<ProjectGroupTreeNode> fullTree = projectGroupService.getFullTree(0);
        System.out.println(new Gson().toJson(fullTree.getData()));
    }

    @Test
    public void getTreeByUser(){
        Result<ProjectGroupTreeNode> fullTree = projectGroupService.getTreeByUser("maqianli",0,null,null);
        System.out.println(new Gson().toJson(fullTree.getData()));
    }

    @Test
    public void create(){
        HeraProjectGroupDataRequest request = new HeraProjectGroupDataRequest();

        String testStr1 = "{\n" +
                "    \"relationObjectId\": 4,\n" +
                "    \"type\": 0,\n" +
                "    \"name\": \"newSale\",\n" +
                "    \"cnName\": \"newSale\",\n" +
                "    \"users\": [\n" +
                "        \"xxxx\",\n" +
                "        \"yyyy\"\n" +
                "    ],\n" +
                "    \"parentGroupId\": 3\n" +
                "}";

        HeraProjectGroupDataRequest heraProjectGroupDataRequest1 = JSON.parseObject(testStr1, HeraProjectGroupDataRequest.class);
        Result<Integer> saleResult = projectGroupService.create(heraProjectGroupDataRequest1);

        String testStr2 = "{\n" +
                "    \"relationObjectId\": 5,\n" +
                "    \"type\": 0,\n" +
                "    \"name\": \"technology\",\n" +
                "    \"cnName\": \"技术部\",\n" +
                "    \"users\": [\n" +
                "        \"xxxx\",\n" +
                "        \"yyyy\"\n" +
                "    ],\n" +
                "    \"parentGroupId\": " + saleResult.getData() + "\n" +
                "}";

        HeraProjectGroupDataRequest heraProjectGroupDataRequest2 = JSON.parseObject(testStr2, HeraProjectGroupDataRequest.class);
        Result<Integer> technologyResult = projectGroupService.create(heraProjectGroupDataRequest2);

        String testStr3 = "{\n" +
                "    \"relationObjectId\": 6,\n" +
                "    \"type\": 0,\n" +
                "    \"name\": \"newSale\",\n" +
                "    \"cnName\": \"newSale\",\n" +
                "    \"users\": [\n" +
                "        \"xxxx\",\n" +
                "        \"yyyy\"\n" +
                "    ],\n" +
                "    \"parentGroupId\": "+technologyResult.getData()+"\n" +
                "}";

        HeraProjectGroupDataRequest heraProjectGroupDataRequest3 = JSON.parseObject(testStr3, HeraProjectGroupDataRequest.class);
        projectGroupService.create(heraProjectGroupDataRequest3);
    }

    @Test
    public void update(){
        HeraProjectGroupDataRequest request = new HeraProjectGroupDataRequest();

//        String testStr = "{\n" +
//                "    \"id\": 1,\n" +
//                "    \"relationObjectId\": 2,\n" +
//                "    \"type\": 0,\n" +
//                "    \"name\": \"xiaomi\",\n" +
//                "    \"cnName\": \"小米\",\n" +
//                "    \"users\": [\n" +
//                "        \"tanghaokun\",\n" +
//                "        \"maqianli\"\n" +
//                "    ],\n" +
//                "    \"apps\": [\n" +
//                "        {\n" +
//                "            \"appId\": \"605\",\n" +
//                "            \"appName\": \"uc-admin\",\n" +
//                "            \"platFormType\": 8,\n" +
//                "        },\n" +
//                "        {\n" +
//                "            \"appId\": \"604\",\n" +
//                "            \"appName\": \"mi-permission-api\",\n" +
//                "            \"platFormType\": 8,\n" +
//                "        }\n" +
//                "    ],\n" +
//                "    \"parentGroupId\": -1\n" +
//                "}";

        String testStr = "{\n" +
                "    \"id\": 6,\n" +
                "    \"relationObjectId\": 6,\n" +
                "    \"type\": 0,\n" +
                "    \"name\": \"efficiency\",\n" +
                "    \"cnName\": \"xiaoneng\",\n" +
                "    \"users\": [\n" +
                "        \"gaoxihui\",\n" +
                "        \"wangtao\"\n" +
                "    ],\n" +
                "    \"apps\": [\n" +
                "        {\n" +
                "            \"appId\": \"667\",\n" +
                "            \"appName\": \"zzytest\",\n" +
                "            \"platFormType\": 0,\n" +
                "        },\n" +
                "        {\n" +
                "            \"appId\": \"91102\",\n" +
                "            \"appName\": \"dingtao-test\",\n" +
                "            \"platFormType\": 0,\n" +
                "        }\n" +
                "    ],\n" +
                "    \"parentGroupId\": 5\n" +
                "}";

        HeraProjectGroupDataRequest heraProjectGroupDataRequest = JSON.parseObject(testStr, HeraProjectGroupDataRequest.class);
        projectGroupService.update(heraProjectGroupDataRequest);
    }

    @Test
    public void delete(){
        projectGroupService.delete(1);
    }

}
