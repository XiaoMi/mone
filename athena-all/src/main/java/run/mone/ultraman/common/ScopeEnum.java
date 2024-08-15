package run.mone.ultraman.common;

/**
 * @author goodjava@qq.com
 * @date 2024/6/26 08:54
 */
public enum ScopeEnum {

    SMethod("method"),
    SClass("class"),
    SModule("module"),
    SProject("project")
    ;


    private String name;

    ScopeEnum(String name) {
        this.name = name;
    }

}
