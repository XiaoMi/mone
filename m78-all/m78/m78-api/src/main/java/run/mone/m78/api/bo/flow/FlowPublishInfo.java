package run.mone.m78.api.bo.flow;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import run.mone.m78.api.enums.FlowRunStatusEnum;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuchuankang
 * @Type FlowPublishInfo.java
 * @Desc
 * @date 2024/8/21 11:38
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowPublishInfo implements Serializable {
	@HttpApiDocClassDefine(value = "id", required = false, description = "id", defaultValue = "0")
	private Integer id;
	@HttpApiDocClassDefine(value = "workSpaceId", required = false, description = "workSpaceId", defaultValue = "0")
	private Long workSpaceId;
	@HttpApiDocClassDefine(value = "name", required = false, description = "名称", defaultValue = "0")
	private String name;
	@HttpApiDocClassDefine(value = "creator", required = false, description = "创建者", defaultValue = "0")
	private String creator;
	@HttpApiDocClassDefine(value = "desc", required = false, description = "描述", defaultValue = "0")
	private String desc;

	//0 未删除 1 已删除
	@HttpApiDocClassDefine(value = "state", required = false, description = "0 未删除 1 已删除", defaultValue = "0")
	private Integer state;
	@HttpApiDocClassDefine(value = "publishStatus", required = false, description = "上架状态", defaultValue = "0")
	private Integer publishStatus;

	//头像地址
	@HttpApiDocClassDefine(value = "avatarUrl", required = false, description = "头像地址", defaultValue = "0")
	private String avatarUrl;
	@HttpApiDocClassDefine(value = "ctime", required = false, description = "创建时间", defaultValue = "0")
	private Long ctime;
	@HttpApiDocClassDefine(value = "utime", required = false, description = "更新时间", defaultValue = "0")
	private Long utime;
	@HttpApiDocClassDefine(value = "categorys", required = false, description = "分类", defaultValue = "0")
	private List<String> categorys;
	@HttpApiDocClassDefine(value = "official", required = false, description = "是否官方插件，0(非官方)，1(官方)", defaultValue = "0")
	private Integer official;
	@HttpApiDocClassDefine(value = "useCount", required = false, description = "使用量", defaultValue = "0")
	private Integer useCount;
	@HttpApiDocClassDefine(value = "flowAvgStar", required = false, description = "评分", defaultValue = "0")
	private Double flowAvgStar;
}
