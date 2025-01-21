package run.mone.local.docean.bo;


import lombok.Data;
import run.mone.ai.z.dto.ZKnowledgeBaseFileBlockDTO;

@Data
public class FileRequest {
    String account;
    Long knowledgeId;
    ZKnowledgeBaseFileBlockDTO dto;
}



