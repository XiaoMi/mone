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

import com.xiaomi.youpin.codegen.BizGen;
import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * BizGen单元测试
 *
 * @author goodjava@qq.com
 * @date 2025/10/7
 */
public class BizGenTest {

    @Test
    public void testGenerateSimpleShop() throws Exception {
        BizGen bizGen = new BizGen();
        
        String projectPath = "/tmp/biz-test";
        String projectName = "my-shop";
        String groupId = "run.mone";
        String packageName = "run.mone.shop";
        String author = "goodjava@qq.com";
        String versionId = "1.0.0";
        String description = "My E-commerce System";

        Result<String> result = bizGen.generateAndZip(
                projectPath, projectName, groupId, packageName, author, versionId, description
        );

        assertTrue("生成应该成功", result.getCode() == 0);
        assertNotNull("返回的路径不应该为null", result.getData());
        
        // 验证生成的zip文件存在
        File zipFile = new File(result.getData());
        assertTrue("zip文件应该存在", zipFile.exists());
        
        System.out.println("✅ 简单Shop项目生成成功: " + result.getData());
    }

    @Test
    public void testGenerateCompleteProject() throws Exception {
        BizGen bizGen = new BizGen();
        
        String projectPath = "/tmp/biz-test";
        String projectName = "ecommerce-platform";
        String groupId = "com.example";
        String packageName = "com.example.ecommerce";
        String author = "developer@example.com";
        String versionId = "2.0.0";
        String description = "Complete E-commerce Platform";
        String springBootVersion = "3.2.0";
        String javaVersion = "21";
        String serverPort = "8090";
        String dbName = "ecommerce_db";
        String jwtSecret = "MyVerySecureJWTSecretKey123456789";
        String jwtExpiration = "86400000";

        Result<String> result = bizGen.generateAndZip(
                projectPath, projectName, groupId, packageName, author, versionId,
                description, springBootVersion, javaVersion, serverPort, dbName,
                jwtSecret, jwtExpiration
        );

        assertTrue("生成应该成功", result.getCode() == 0);
        assertNotNull("返回的路径不应该为null", result.getData());
        
        File zipFile = new File(result.getData());
        assertTrue("zip文件应该存在", zipFile.exists());
        
        System.out.println("✅ 完整电商平台项目生成成功: " + result.getData());
    }

    @Test
    public void testGenerateWithCustomPorts() throws Exception {
        BizGen bizGen = new BizGen();
        
        String projectPath = "/tmp/biz-test";
        String projectName = "custom-service";
        String groupId = "run.mone";
        String packageName = "run.mone.custom";
        String author = "goodjava@qq.com";
        String versionId = "1.0.0";
        String description = "Custom Service with Different Port";
        String springBootVersion = "3.2.0";
        String javaVersion = "21";
        String serverPort = "9090";
        String dbName = "custom_db";
        String jwtSecret = "CustomSecretKey123456789012345";
        String jwtExpiration = "43200000"; // 12小时

        Result<String> result = bizGen.generateAndZip(
                projectPath, projectName, groupId, packageName, author, versionId,
                description, springBootVersion, javaVersion, serverPort, dbName,
                jwtSecret, jwtExpiration
        );

        assertTrue("生成应该成功", result.getCode() == 0);
        
        File zipFile = new File(result.getData());
        assertTrue("zip文件应该存在", zipFile.exists());
        
        System.out.println("✅ 自定义端口项目生成成功: " + result.getData());
    }

    @Test
    public void testGenerateMultipleProjects() throws Exception {
        BizGen bizGen = new BizGen();
        
        String[] projectNames = {"user-service", "order-service", "payment-service"};
        
        for (String name : projectNames) {
            String projectPath = "/tmp/biz-test";
            String groupId = "run.mone.microservice";
            String packageName = "run.mone.microservice." + name.replace("-", "");
            String author = "goodjava@qq.com";
            String versionId = "1.0.0";
            String description = name + " microservice";

            Result<String> result = bizGen.generateAndZip(
                    projectPath, name, groupId, packageName, author, versionId, description
            );

            assertTrue(name + " 生成应该成功", result.getCode() == 0);
            
            File zipFile = new File(result.getData());
            assertTrue(name + " zip文件应该存在", zipFile.exists());
            
            System.out.println("✅ " + name + " 生成成功: " + result.getData());
        }
    }

    @Test
    public void testProjectNameConversion() throws Exception {
        BizGen bizGen = new BizGen();
        
        // 测试连字符命名转驼峰
        String projectPath = "/tmp/biz-test";
        String projectName = "my-awesome-shop-service";
        String groupId = "run.mone";
        String packageName = "run.mone.shop";
        String author = "test@qq.com";
        String versionId = "1.0.0";
        String description = "Test Camel Case Conversion";

        Result<String> result = bizGen.generateAndZip(
                projectPath, projectName, groupId, packageName, author, versionId, description
        );

        assertTrue("生成应该成功", result.getCode() == 0);
        
        File zipFile = new File(result.getData());
        assertTrue("zip文件应该存在", zipFile.exists());
        
        System.out.println("✅ 驼峰命名转换测试成功: " + result.getData());
    }
}

