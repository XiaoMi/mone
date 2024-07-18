package com.xiaomi.youpin.tesla.ip.common;

/**
 * @author goodjava@qq.com
 * @date 2023/5/29 10:31
 */
public enum PromptType {

    createClass("创建类"),
    modifyClass("修改类"),
    createMethod("创建方法"),
    createMethod2("创建方法2(可以指定参数)"),
    modifyMethod("修改方法"),
    comment("添加注释"),
    lineByLineComment("添加逐行注释(也可以用来给方法添加注释)"),
    createFile("创建文件"),
    select("进入选择流程(类和方法)"),
    removeComment("删除注释"),
    checkPomVersion("检查POM依赖版本"),
    generateBootStrapAnno("给启动类添加注解"),
    showInfo("展示信息到对话框<打字机效果>"),
    createClass2("创建类2"),
    createClass4("创建类(直接就生成一个完整的类)"),
    testPrompt("在文本文件中测试prompt"),
    repleaceSelectContent("替换选中的文本内容"),
    genBizMethodCode("生成业务方法"),
    inlayHint("镶嵌"),
    bot("机器人指令"),
    generateMethod("生成技术代码"),
    generateMiapiMethod("根据miapi知识库生成某接口调用方法"),
    generateTestMethod("生成测试代码"),
    generateInterface("提取公共方法生成接口"),
    executeBot("执行bot(m78)"),
    question("ai会向你提问")
    ;

    private String desc;

    PromptType(String desc) {
        this.desc = desc;
    }
}
