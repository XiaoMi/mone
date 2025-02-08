package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static run.mone.m78.api.constant.TableConstant.CARD_VARIABLE_TABLE;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(CARD_VARIABLE_TABLE)
public class M78CardVariablePo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column("card_id")
    private Long cardId;

    @Column
    private String name;

    @Column("class_type")
    private String classType;

    @Column("default_value")
    private String defaultValue;

    @Column
    private String creator;

    @Column
    private String updater;

    @Column
    private Long ctime;

    @Column
    private Long utime;


}
