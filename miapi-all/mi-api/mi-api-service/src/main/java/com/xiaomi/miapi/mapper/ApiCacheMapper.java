package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.common.pojo.ApiCache;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 接口缓存[数据库操作]
 */
public interface ApiCacheMapper
{

	//添加接口缓存
	public Integer addApiCache(ApiCache apiCache);

	//更新接口缓存
	public Integer updateApiCache(ApiCache apiCache);

	//更新接口缓存数据星标状态
	public Integer updateApiStar(@Param("projectID") Integer projectID, @Param("apiID") Integer apiID,
			@Param("starred") Integer starred);

	//获取接口缓存数据
	public ApiCache getApiCache(@Param("projectID") Integer projectID, @Param("apiID") Integer apiID);

	//修改接口缓存分组
	public int changeApiGroup(@Param("projectID") Integer projectID, @Param("apiIDs") List<Integer> apiIDs,
			@Param("groupID") Integer groupID);
	
	//根据分组ID获取接口缓存列表
	public List<ApiCache> getApiCacheByGroupID(@Param("projectID") Integer projectID,
			@Param("groupID") Integer groupID);

}
