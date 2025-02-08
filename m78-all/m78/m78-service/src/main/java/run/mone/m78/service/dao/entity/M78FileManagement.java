package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author zhangzhiyong
 * @since 2024-08-09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "m78_file_management")
public class M78FileManagement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer state;

    private Long ctime;

    private Long utime;

    private Integer fileid;

    private String filename;

    private String filetype;

    private Long filesize;

    private Long uploaddate;

    private Long lastmodifieddate;

    private Integer owneruserid;

    private String accesspermissions;

    private String storagelocation;

    private String filedescription;

    private Integer status;

    private String content;

    @Column("moonshotId")
    private String moonshotId;

}
