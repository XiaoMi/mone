package run.mone.m78.service.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Data
public class ApiKeyDTO implements Serializable {

    private Long id;

    private Long typeId;

    /**
     * 1-空间，2-bot，3-flow，4-知识库
     */
    @Builder.Default
    private Integer type = 1;

    private String apiKey;

    private String creator;

    private LocalDateTime createTime;
}
