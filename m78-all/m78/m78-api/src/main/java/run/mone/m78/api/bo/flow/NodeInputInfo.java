package run.mone.m78.api.bo.flow;

import lombok.Data;
import run.mone.m78.api.enums.InputValueTypeEnum;

import java.io.Serializable;
import java.util.List;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
public class NodeInputInfo implements Serializable {

    private String name;

    //关联reference、输入value、关联图片imageReference
    private String type;

    private List<String> referenceInfo;//固定为[referenceNodeId, referenceName] 前端需要

    //关联的nodeId
    private int referenceNodeId;

    //关联node的哪个出参
    private String referenceName;

    //输入
    private String value;

    //start类型时，入参是否必填
    private boolean required;

    //String、Object、Array<string>、Array<object>、Image
    /**
     * @see InputValueTypeEnum
     */
    private String valueType;

    private String desc = "";

    private String schema;
}
