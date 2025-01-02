package run.mone.hive.examples;

import run.mone.hive.context.Context;
import run.mone.hive.roles.Engineer;
import run.mone.hive.schema.Message;
import run.mone.hive.Team;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class EngineerDemo {
    public static void main(String[] args) {
        try {
            // 创建上下文
            Context context = new Context();
            context.setLanguage("Chinese");  // 设置输出语言

            // 创建团队并设置预算
            Team team = new Team(context);
            team.invest(10.0);  // 设置预算

            // 初始化工程师角色
            Engineer engineer = new Engineer(
                    "Alex",         // 名字
                    "Engineer",     // 角色
                    "Write clean and efficient code", // 目标
                    "Follow clean code principles"    // 约束
            );

            // 准备开发需求
            String requirement = """
                    Please create a simple calculator application with the following requirements:
                    
                    1. Basic Operations:
                       - Addition
                       - Subtraction
                       - Multiplication
                       - Division
                    
                    2. Features:
                       - Command line interface
                       - Support for decimal numbers
                       - Error handling for invalid inputs
                       - Basic memory function (store/recall)
                    
                    3. Technical Requirements:
                       - Use Java
                       - Follow OOP principles
                       - Include unit tests
                       - Include documentation
                    """;

            // 创建消息
            Message message = Message.builder()
                    .content(requirement)
                    .role("user")
                    .causeBy("UserRequirement")
                    .build();

            // 雇佣工程师
            team.hire(Collections.singletonList(engineer));

            // 运行项目
            System.out.println("=== Starting Development Process ===");
            team.runProject(requirement, "", "Engineer");
            team.run(5, "", "", true).join();  // 最多运行5轮

            System.out.println("=== Development Process Completed ===");

        } catch (Exception e) {
            log.error("Error running EngineerDemo", e);
            e.printStackTrace();
        }
    }
} 