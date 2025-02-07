package run.mone.m78.api.bo.flow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NodeOutputInfo implements Serializable {

    private String name;

    //for 知识库 临时方案，已经没用了？
    private String subName;

    //String、Object、Array<String>、Array<Object>、code(llm专有出参类型)
    private String valueType;

    private String value;

    private String desc;

    //关联reference、输入value
    private String type;

    //关联的nodeId
    private int referenceNodeId;

    //关联node的哪个出参
    private String referenceName;

    //for 知识库 临时方案，已经没用了？
    private String referenceSubName;

    private List<String> referenceInfo;//固定为[referenceNodeId, referenceName] 前端需要

    private String schema;

}
