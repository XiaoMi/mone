package run.mone.m78.api.bo.flow;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/2/29
 */
@Data
public class KnowledgeSetting implements Serializable {

    private String knowledgeBaseId;
    private String maxRecall;
    private String minMatch;
    private String query;

}
