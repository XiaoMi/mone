package run.mone.m78.api.enums;


/**
 * @author wmin
 * @date 2024/2/29
 */
public enum FlowRunStatusEnum {

    NOT_RUN(0,"未运行"),
    RUN_START(-1,"开始运行"),
    RUN_SUCCEED(2,"运行成功"),
    RUN_FAILED(3,"运行失败"),
    RUN_CANCELED(4,"运行取消");

    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    FlowRunStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
