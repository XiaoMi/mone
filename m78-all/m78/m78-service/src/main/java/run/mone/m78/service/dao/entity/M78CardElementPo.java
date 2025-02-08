package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static run.mone.m78.api.constant.TableConstant.CARD_ELEMENT_TABLE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(CARD_ELEMENT_TABLE)
public class M78CardElementPo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column("workspace_id")
    private Long workspaceId;

    @Column("card_id")
    private Long cardId;

    @Column("unique_key")
    private String uniqueKey;

    @Column
    private String type;

    /**
     * 基础配置
     */
    @Column
    private String property;


    @Column
    private String children;

}
