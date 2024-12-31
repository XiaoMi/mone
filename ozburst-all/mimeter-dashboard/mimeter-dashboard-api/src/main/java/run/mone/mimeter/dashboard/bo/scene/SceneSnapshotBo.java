package run.mone.mimeter.dashboard.bo.scene;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SceneSnapshotBo {

    private Long id;

    private Integer type;

    private Long sceneId;

    private String snapshotId;

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private Integer version;

    @HttpApiDocClassDefine(value = "md5", description = "场景快照数据sha256")
    private String md5;

    @HttpApiDocClassDefine(value = "scene", description = "场景快照json数据, SceneDTO")
    private String scene;

    public boolean checkCreate() {
        return this.sceneId != null && this.sceneId > 0 && StringUtils.isNotBlank(this.createBy) &&
                StringUtils.isNotBlank(this.scene);
    }
}
