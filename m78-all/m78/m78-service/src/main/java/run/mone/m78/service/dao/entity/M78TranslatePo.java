package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static run.mone.m78.api.constant.TableConstant.TRANSLATE_TABLE;

/**
 * @author dp
 * @date 1/16/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(TRANSLATE_TABLE)
public class M78TranslatePo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String uuid;

    @Column("user_name")
    private String userName;

    //是否收藏 1收藏 0没有收藏
    @Column("favorite")
    private Integer favorite;

    //这个内容是当meta信息用的
    @Column("custom_knowledge")
    private String customKnowledge;

    @Column("status")
    private Integer status;

    /**
     * @see TranslateType
     */
    @Column("type")
    private String type;

    // store only
    @Column("create_time")
    private String createTime;

    // store only
    @Column("modify_time")
    private String updateTime;

    @Column(value = "from_text")
    private String fromText;

    @Column(value = "to_text")
    private String toText;

    @Column(value = "from_language")
    private String fromLanguage;

    @Column(value = "to_language")
    private String toLanguage;


}
