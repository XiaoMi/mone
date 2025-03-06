package run.mone.moon.function.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class HttpParam implements Serializable {
    /**
     * 请求地址
     */
    private String url;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 请求头
     */
    private Map<String,String> headers;
    /**
     * 结果解析模式
     */
    private String responseParseMode;
    /**
     * 结果解析参数
     */
    private String responseParseParams;
}
