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

import com.xiaomi.youpin.codegen.AgentGen;
import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * AgentGen单元测试
 *
 * @author goodjava@qq.com
 * @date 2025/10/7
 */
public class AgentGenTest {

    @Test
    public void testGenerateCoderAgent() throws Exception {
        AgentGen agentGen = new AgentGen();
        
        String projectPath = "/tmp/agent-test";
        String projectName = "mcp-coder-test";
        String groupId = "run.mone";
        String packageName = "run.mone.mcp.coder";
        String author = "goodjava@qq.com";
        String versionId = "1.0.0";
        String agentName = "coder";
        String agentGroup = "staging";
        String agentProfile = "你是一名优秀的软件工程师";
        String agentGoal = "你的目标是根据用户的需求写好代码";
        String agentConstraints = "不要探讨和代码不相关的东西,如果用户问你,你可以直接拒绝掉";

        Result<String> result = agentGen.generateAndZip(
                projectPath, projectName, groupId, packageName, author, versionId,
                agentName, agentGroup, agentProfile, agentGoal, agentConstraints
        );

        assertTrue("生成应该成功", result.isSuccess());
        assertNotNull("返回的路径不应该为null", result.getData());
        
        // 验证生成的zip文件存在
        File zipFile = new File(result.getData());
        assertTrue("zip文件应该存在", zipFile.exists());
        
        System.out.println("✅ Coder Agent项目生成成功: " + result.getData());
    }

    @Test
    public void testGenerateTestAgent() throws Exception {
        AgentGen agentGen = new AgentGen();
        
        String projectPath = "/tmp/agent-test";
        String projectName = "mcp-test-agent";
        String groupId = "run.mone";
        String packageName = "run.mone.mcp.test";
        String author = "goodjava@qq.com";
        String versionId = "1.0.0";
        String agentName = "test";
        String agentGroup = "dev";
        String agentProfile = "你是一名优秀的测试工程师";
        String agentGoal = "你的目标是编写高质量的测试代码";
        String agentConstraints = "不要探讨和测试不相关的东西";

        Result<String> result = agentGen.generateAndZip(
                projectPath, projectName, groupId, packageName, author, versionId,
                agentName, agentGroup, agentProfile, agentGoal, agentConstraints
        );

        assertTrue("生成应该成功", result.isSuccess());
        assertNotNull("返回的路径不应该为null", result.getData());
        
        File zipFile = new File(result.getData());
        assertTrue("zip文件应该存在", zipFile.exists());
        
        System.out.println("✅ Test Agent项目生成成功: " + result.getData());
    }

    @Test
    public void testGenerateCustomAgent() throws Exception {
        AgentGen agentGen = new AgentGen();
        
        String projectPath = "/tmp/agent-test";
        String projectName = "mcp-custom-agent";
        String groupId = "run.mone";
        String packageName = "run.mone.mcp.custom";
        String author = "goodjava@qq.com";
        String versionId = "2.0.0";
        String agentName = "custom";
        String agentGroup = "production";
        String agentProfile = "你是一名AI助手";
        String agentGoal = "帮助用户完成各种任务";
        String agentConstraints = "遵守道德规范,不提供违法内容";

        // 使用完整参数版本
        Result<String> result = agentGen.generateAndZip(
                projectPath, projectName, groupId, packageName, author, versionId,
                agentName, agentGroup, agentProfile, agentGoal, agentConstraints,
                "run.mone", "mcp", "1.6.1-jdk21-SNAPSHOT",
                "9187", "http://127.0.0.1:8080", "deepseek", "21"
        );

        assertTrue("生成应该成功", result.isSuccess());
        assertNotNull("返回的路径不应该为null", result.getData());
        
        File zipFile = new File(result.getData());
        assertTrue("zip文件应该存在", zipFile.exists());
        
        System.out.println("✅ Custom Agent项目生成成功: " + result.getData());
    }

    @Test
    public void testProjectNameToCamelCase() throws Exception {
        AgentGen agentGen = new AgentGen();
        
        // 测试连字符命名转驼峰命名
        String projectPath = "/tmp/agent-test";
        String projectName = "mcp-my-test-agent";  // 应该转换为 McpMyTestAgentBootstrap
        String groupId = "run.mone";
        String packageName = "run.mone.mcp.mytest";
        String author = "test@qq.com";
        String versionId = "1.0.0";
        String agentName = "mytest";
        String agentGroup = "dev";
        String agentProfile = "测试Agent";
        String agentGoal = "测试目标";
        String agentConstraints = "测试约束";

        Result<String> result = agentGen.generateAndZip(
                projectPath, projectName, groupId, packageName, author, versionId,
                agentName, agentGroup, agentProfile, agentGoal, agentConstraints
        );

        assertTrue("生成应该成功", result.isSuccess());
        
        File zipFile = new File(result.getData());
        assertTrue("zip文件应该存在", zipFile.exists());
        
        System.out.println("✅ 驼峰命名测试成功: " + result.getData());
    }
}

