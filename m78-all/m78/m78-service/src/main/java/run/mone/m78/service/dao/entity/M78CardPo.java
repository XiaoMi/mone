package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static run.mone.m78.api.constant.TableConstant.CARD_TABLE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(CARD_TABLE)
public class M78CardPo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column
    private String name;

    @Column("element_id")
    private Long elementId;

    @Column("workspace_id")
    private Long workspaceId;

    @Column
    private String type;

    @Column
    private Integer status;

    @Column
    private Integer official;

    @Column
    private String description;

    @Column
    private String creator;

    @Column
    private String updater;

    @Column
    private Long ctime;

    @Column
    private Long utime;

}
