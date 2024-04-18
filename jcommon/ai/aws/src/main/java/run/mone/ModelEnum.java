package run.mone;

/**
 * @author goodjava@qq.com
 * @date 2024/4/12 14:59
 */
public enum ModelEnum {


    Sonnet("anthropic.claude-3-sonnet-20240229-v1:0"),
    Haiku("anthropic.claude-3-haiku-20240307-v1:0");


    public String modelName;

    ModelEnum(String name) {
        this.modelName = name;
    }

}
