package run.mone.moon.function.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DubboParam implements Serializable {
    /**
     * 注册的版本
     */
    private String version;
    /**
     * 注册的服务名
     */
    private String serviceName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 注册的group
     */
    private String group;
    /**
     * 单次执行重试次数
     */
    private Integer retries;
    /**
     * 是否没有返回值(oneway模式)
     */
    private Boolean isOneway;
    /**
     * 请求参数类型
     */
    private String parameterTypes;
    /**
     * 结果解析模式
     */
    private String responseParseMode;
    /**
     * 结果解析参数
     */
    private String responseParseParams;
    /**
     * dubbo tag
     */
    private String tag;
    /**
     * 启用dubbo tag
     */
    private boolean tagOn;
}
