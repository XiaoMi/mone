package com.metagpt.test;

import com.metagpt.roles.Engineer;
import com.metagpt.schema.Message;
import com.metagpt.Team;
import com.metagpt.Context;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class EngineerTest {
    private static final Logger logger = LoggerFactory.getLogger(EngineerTest.class);

    @Test
    public void testEmptyTeam() {
        // 创建上下文
        Context context = new Context();
        context.setLanguage("Chinese");

        // 创建团队
        Team team = new Team(context);
        team.invest(3.0); // 设置预算

        // 创建工程师
        Engineer engineer = new Engineer();
        engineer.setContext(context);

        // 运行测试
        CompletableFuture<Message> future = team.run("Build a simple search system. I will upload my files later.");
        Message result = future.join();

        logger.info("Test result: {}", result);
    }

    @Test
    public void testSoftwareCompany() {
        // 创建上下文
        Context context = new Context();
        context.setLanguage("English");

        // 创建团队
        Team team = new Team(context);
        team.invest(5.0);

        // 创建工程师
        Engineer engineer = new Engineer();
        engineer.setContext(context);
        team.hire(engineer);

        // 运行测试 - 创建贪吃蛇游戏
        CompletableFuture<Message> future = team.run("Make a cli snake game");
        Message result = future.join();

        logger.info("Test result: {}", result);
    }

    @Test
    public void testSoftwareCompanyWithRunTests() {
        // 创建上下文
        Context context = new Context();
        context.setLanguage("English");

        // 创建团队并设置配置
        Team team = new Team(context);
        team.invest(8.0);
        team.setMaxRounds(8);
        team.setRunTests(true);

        // 创建工程师
        Engineer engineer = new Engineer();
        engineer.setContext(context);
        team.hire(engineer);

        // 运行测试 - 创建贪吃蛇游戏并包含测试
        CompletableFuture<Message> future = team.run("Make a cli snake game");
        Message result = future.join();

        logger.info("Test result with tests: {}", result);
    }
} 