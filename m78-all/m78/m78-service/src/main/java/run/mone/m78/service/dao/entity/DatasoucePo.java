package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static run.mone.m78.api.constant.TableConstant.DATASOURCE_TABLE;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/31/24 09:22
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(DATASOURCE_TABLE)
public class DatasoucePo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 数据源唯一标识Md5(ip:port:dbname:user:pwd) */
    private String uuid;

    private String name;

    /** 数据源类型(0:unknown 1:mysql, 其他预留) */
    private Integer type;

    /**
     * 数据源接入方式(0:unknown 1:明文 2:Keycenter密文)
     */
    private Integer joinType;

    private String desc;

    private String connInfo;

    private String extInfo;

    /** 状态(0:未知 1:有效 2:无效) */
    private Integer status;

    private String createTime;

    private String modifyTime;
}
