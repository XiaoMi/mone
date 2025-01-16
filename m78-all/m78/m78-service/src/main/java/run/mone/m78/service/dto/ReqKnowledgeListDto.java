package run.mone.m78.service.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReqKnowledgeListDto  implements Serializable {

    private String knowledgeName;

    private String createUserName;

    private Integer pageNum;

    private Integer pageSize;
}
