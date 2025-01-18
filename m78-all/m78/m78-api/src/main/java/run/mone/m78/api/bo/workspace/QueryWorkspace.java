package run.mone.m78.api.bo.workspace;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

/**
 * @author liuchuankang
 * @Type QueryWorkspace.java
 * @Desc
 * @date 2024/8/23 10:13
 */
@Data
public class QueryWorkspace implements Serializable {
	@HttpApiDocClassDefine(value = "pageSize", required = false, description = "页长", defaultValue = "0")
	private int pageSize=10;
	@HttpApiDocClassDefine(value = "pageNum", required = false, description = "页码", defaultValue = "0")
	private int pageNum=1;
	@HttpApiDocClassDefine(value = "owner", required = false, description = "空间owner", defaultValue = "0")
	private String owner;
	@HttpApiDocClassDefine(value = "name", required = false, description = "空间名称", defaultValue = "0")
	private String name;
}
