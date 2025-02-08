package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_api_key")
public class M78ApiKey implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column("type_id")
    private Long typeId;

    /**
     * 1-空间，2-bot，3-flow，4-知识库
     */
    @Builder.Default
    private Integer type = 1;

    @Column("api_key")
    private String apiKey;

    private String creator;

    @Column("create_time")
    private LocalDateTime createTime;
}
