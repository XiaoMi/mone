package run.mone.local.docean.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2024/2/28 13:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Message implements Serializable {

    @Id
    private Long id;

    @Column
    private String topicId;

    @Column
    private String role;

    @Column
    private String data;

}
