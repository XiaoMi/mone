package com.xiaomi.miapi.pojo;

import lombok.Data;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class ApiCache
{
	private Integer cacheID;
	private Integer projectID;
	private Integer groupID;
	private Integer apiID;
	private String apiJson;
	private Integer starred;
	private String updateUsername;
}
