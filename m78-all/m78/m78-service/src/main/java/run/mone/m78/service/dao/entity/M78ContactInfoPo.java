package run.mone.m78.service.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static run.mone.m78.api.constant.TableConstant.CONTACT_INFO_TABLE;


/**
 * @author dp
 * @date 1/17/24
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(CONTACT_INFO_TABLE)
public class M78ContactInfoPo {

    @Id(keyType = KeyType.Auto)
    private Long id;

    @Column("user_name")
    private String userName;

    // store only
    @Column("create_time")
    private String createTime;

    // store only
    @Column("modify_time")
    private String updateTime;

    @Column(value = "contact_name")
    private String contactName;

    @Column(value = "contact_email")
    private String contactEmail;

    @Column(value = "contact_subject")
    private String contactSubject;

    @Column(value = "contact_content")
    private String contactContent;

    @Column(value = "processing_status")
    private int processingStatus;

    @Column(value = "processing_person_name")
    private String processingPersonName;


}
