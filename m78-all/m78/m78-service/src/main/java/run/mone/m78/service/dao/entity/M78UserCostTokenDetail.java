package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static run.mone.m78.api.constant.TableConstant.USER_COST_TOKEN_DETAIL_TABLE;

/**
 * 实体类。
 *
 * @author hoho
 * @since 2024-09-20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(USER_COST_TOKEN_DETAIL_TABLE)
public class M78UserCostTokenDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String user;

    // 1-Bot 2-Flow
    private Integer type;

    private Long relationId;

    private String input;

    private String output;

    private Long costToken;

    private LocalDateTime date;

}
