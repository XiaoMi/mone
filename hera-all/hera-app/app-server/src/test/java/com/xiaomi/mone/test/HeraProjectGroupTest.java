package com.xiaomi.mone.test;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.xiaomi.mone.app.AppBootstrap;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.app.api.model.HeraAppBaseQuery;
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupDataRequest;
import com.xiaomi.mone.app.api.model.project.group.ProjectGroupTreeNode;
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
        Result<ProjectGroupTreeNode> fullTree = projectGroupService.getTreeByUser("maqianli",0,null);
        System.out.println(new Gson().toJson(fullTree.getData()));
    }

    @Test
    public void create(){
        HeraProjectGroupDataRequest request = new HeraProjectGroupDataRequest();

//        String testStr = "{\n" +
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
//                "            \"platFormType\": 9,\n" +
//                "        }\n" +
//                "    ],\n" +
//                "    \"parentGroupId\": -1\n" +
//                "}";


//        String testStr = "{\n" +
//                "    \"relationObjectId\": 3,\n" +
//                "    \"type\": 0,\n" +
//                "    \"name\": \"chinaArea\",\n" +
//                "    \"cnName\": \"中国区\",\n" +
//                "    \"users\": [\n" +
//                "        \"gaoxihui\",\n" +
//                "        \"wangtao\"\n" +
//                "    ],\n" +
//                "    \"parentGroupId\": 2\n" +
//                "}";

        String testStr1 = "{\n" +
                "    \"relationObjectId\": 4,\n" +
                "    \"type\": 0,\n" +
                "    \"name\": \"newSale\",\n" +
                "    \"cnName\": \"新零售\",\n" +
                "    \"users\": [\n" +
                "        \"gaoxihui\",\n" +
                "        \"wangtao\"\n" +
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
                "        \"gaoxihui\",\n" +
                "        \"wangtao\"\n" +
                "    ],\n" +
                "    \"parentGroupId\": " + saleResult.getData() + "\n" +
                "}";

        HeraProjectGroupDataRequest heraProjectGroupDataRequest2 = JSON.parseObject(testStr2, HeraProjectGroupDataRequest.class);
        Result<Integer> technologyResult = projectGroupService.create(heraProjectGroupDataRequest2);

        String testStr3 = "{\n" +
                "    \"relationObjectId\": 6,\n" +
                "    \"type\": 0,\n" +
                "    \"name\": \"newSale\",\n" +
                "    \"cnName\": \"新零售\",\n" +
                "    \"users\": [\n" +
                "        \"gaoxihui\",\n" +
                "        \"wangtao\"\n" +
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
                "    \"cnName\": \"效能组\",\n" +
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

    public static void main(String[] args) {
        String testStr = "{\n" +
                "    \"relationObjectId\": 2,\n" +
                "    \"type\": 0,\n" +
                "    \"name\": \"xiaomi\",\n" +
                "    \"cnName\": \"小米\",\n" +
                "    \"users\": [\n" +
                "        \"tanghaokun\",\n" +
                "        \"maqianli\"\n" +
                "    ],\n" +
                "    \"apps\": [\n" +
                "        {\n" +
                "            \"appId\": \"10000675\",\n" +
                "            \"appName\": \"unidata_cn_pro\",\n" +
                "            \"platFormType\": 8,\n" +
                "        },\n" +
                "        {\n" +
                "            \"appId\": \"10000391\",\n" +
                "            \"appName\": \"eam_cn_pro\",\n" +
                "            \"platFormType\": 8,\n" +
                "        }\n" +
                "    ],\n" +
                "    \"parentGroupId\": -1\n" +
                "}";

        HeraProjectGroupDataRequest heraProjectGroupDataRequest = JSON.parseObject(testStr, HeraProjectGroupDataRequest.class);
        System.out.println("parseResult:" + heraProjectGroupDataRequest.toString());
    }


}
