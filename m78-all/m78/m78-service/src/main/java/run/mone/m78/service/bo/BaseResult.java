package run.mone.m78.service.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/5/20
 */
@Data
public class BaseResult<T> implements Serializable {

    private int code;

    private String msg;

    private BaseMessage.MetaInfo metaInfo;

    private String traceId;

    private T data;

    public BaseResult(int code, String msg, BaseMessage.MetaInfo metaInfo, T data) {
        this.code = code;
        this.msg = msg;
        this.metaInfo = metaInfo;
        this.data = data;
    }

    public BaseResult(int code, BaseMessage.MetaInfo metaInfo, T data) {
        this.code = code;
        this.metaInfo = metaInfo;
        this.data = data;
    }

    public BaseResult(int code, String msg, BaseMessage.MetaInfo metaInfo) {
        this.code = code;
        this.msg = msg;
        this.metaInfo = metaInfo;
    }


    @Data
    public static class MetaInfo {
        private Integer appId;
        private String id;
        private String type;
        private boolean end;
    }
}
