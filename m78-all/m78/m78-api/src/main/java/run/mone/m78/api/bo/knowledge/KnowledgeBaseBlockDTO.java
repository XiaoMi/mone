package run.mone.m78.api.bo.knowledge;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class KnowledgeBaseBlockDTO implements Serializable {
    private String id;
    private Date gmtCreate;
    private Date gmtModified;
    private String creator;
    private Long fileId;
    private String fileName;
    private String blockId;
    private String blockContent;

}
