package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.GsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

import static run.mone.m78.api.constant.TableConstant.BOT_FSM_TABLE;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table("m78_lck_test")
public class M78LckTest implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column(value = "name", typeHandler = GsonTypeHandler.class)
    private Map<String, String> name;

}
