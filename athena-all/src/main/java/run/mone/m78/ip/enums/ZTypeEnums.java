package run.mone.m78.ip.enums;

/**
 * @author caobaoyu
 * @description: 对应Z平台的prompt
 * @date 2023-04-21 15:27
 */
public enum ZTypeEnums {

    // 64-Athena 61-codegen

    CMD(64, "cmd", "cmd命令行prompt"),
    COMMENT(64, "comment", "生成注释"),
    REMOVE_COMMENT(64, "removeComment", "删除注释"),
    GENERATE_METHOD(64, "generateMethod", "生成方法"),
    COMPLETION(64, "completion", "检索"),

    FILED_DESC(64, "filedDesc", "类属性描述"),
    METHOD_DESC(64, "methodDesc", "类方法描述"),

    GENERATE_HANDLER(64, "odin_handler", "生成一个handler"),

    GENERATE_MOON_HANDLER(64, "moon_handler", "生成一个moon handler"),

    CODE_CHECK(64, "code_check", "sonar代码检查Service"),

    MI_API(64, "mi_api", "生成miApi注解"),

    TEST_CODE(64, "test_code", "生成测试方法"),

    ODIN_MIDDLEWARE(64, "odin_middleware", "odin 中间件的一个使用demo");

    ZTypeEnums(int type, String info, String desc) {
        this.type = type;
        this.info = info;
        this.desc = desc;
    }

    int type;
    String info;
    String desc;

    public int getType() {
        return type;
    }

    public String getInfo() {
        return info;
    }
}
