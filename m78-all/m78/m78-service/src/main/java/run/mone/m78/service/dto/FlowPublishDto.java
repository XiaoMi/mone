package run.mone.m78.service.dto;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.util.List;

/**
 * @author liuchuankang
 * @Type FlowPublishDto.java
 * @Desc
 * @date 2024/8/21 10:33
 */
@Data
public class FlowPublishDto {

	@HttpApiDocClassDefine(value = "flowId", required = false, description = "flowId", defaultValue = "0")
	private Long flowId;
	@HttpApiDocClassDefine(value = "publish", required = false, description = "1:上架，0:下架", defaultValue = "0")
	private Boolean publish;
	@HttpApiDocClassDefine(value = "flowCategorys", required = false, description = "分类", defaultValue = "0")
	private List<Long> flowCategorys;
}
