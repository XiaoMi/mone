package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.Fastjson2TypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import run.mone.m78.api.bo.model.ModelConfig;

import java.util.List;
import java.util.Map;

import static run.mone.m78.api.constant.TableConstant.CHAT_TOPICS_TABLE;
import static run.mone.m78.api.constant.TableConstant.USER_CONFIG_TABLE;

/**
 * @author goodjava@qq.com
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(USER_CONFIG_TABLE)
//m78_user_config
public class UserConfigPo {

    @Id(keyType = KeyType.Auto)
    private Integer id;

    @Column("ctime")
    private Long ctime;

    @Column("utime")
    private Long utime;

    @Column("state")
    private Integer state;

    @Column("user_name")
    private String userName;

    @Column(value = "model_config", typeHandler = Fastjson2TypeHandler.class)
    private ModelConfig modelConfig;

}
