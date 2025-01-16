package run.mone.m78.api.bo.workspace;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author liuchuankang
 * @Type M78WorkspaceInfoResp.java
 * @Desc
 * @date 2024/8/23 10:55
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceInfoResp implements Serializable {

	@HttpApiDocClassDefine(value = "id",description = "工作空间id")
	private Long id;

	/**
	 * workspace名称
	 */
	@HttpApiDocClassDefine(value = "name",description = "工作空间名称")
	private String name;

	private String avatarUrl;

	@HttpApiDocClassDefine(value = "remark",description = "工作空间描述")
	private String remark;

	/**
	 * 所有者
	 */
	@HttpApiDocClassDefine(value = "owner",description = "所有者")
	private String owner;

	/**
	 * 创建人
	 */
	@HttpApiDocClassDefine(value = "creator",description = "创建人")
	private String creator;

	/**
	 * 是否删除0-否 1-是
	 */
	@HttpApiDocClassDefine(value = "deleted",description = "是否删除0-否 1-是")
	private Integer deleted;
	@HttpApiDocClassDefine(value = "createTime",description = "创建时间")
	private LocalDateTime createTime;
	/**
	 * 更新人
	 */
	@HttpApiDocClassDefine(value = "updater",description = "更新人")
	private String updater;

	@HttpApiDocClassDefine(value = "updateTime",description = "更新时间")
	private LocalDateTime updateTime;

	@HttpApiDocClassDefine(value = "version", description = "版本")
	private Integer version;

	@HttpApiDocClassDefine(value = "canOperate", description = "版本")
	private Boolean canOperate;
}
