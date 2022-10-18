package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.common.pojo.Api;
import com.xiaomi.miapi.common.pojo.ApiHeader;
import com.xiaomi.miapi.common.pojo.ApiResultParam;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * 接口
 */
public interface ApiMapper
{
	//获取项目接口数量
	public Integer getApiCount(@Param("projectID") Integer projectID);

	//添加接口
	public Integer addApi(Api api);

	//删除分组下的接口
	public Integer deleteApiByGroupID(@Param("groupIDS") List<Integer> groupIDS, @Param("removeTime") Timestamp removeTime);

	//添加接口请求头部
	public Integer addApiHeader(ApiHeader header);

	public List<ApiHeader> getApiHeaders(Integer apiID);

	//添加接口返回结果参数
	public Integer addResultParam(ApiResultParam resultParam);

	//更新接口
	public Integer updateApi(Api api);

	//移除接口到回收站
	public Integer removeApi(@Param("projectID") Integer projectID, @Param("apiIDs") List<Integer> apiIDs,
			@Param("updateTime") Timestamp updateTime);

	//获取接口名称
	public String getApiNameByIDs(@Param("apiIDs") List<Integer> apiIDs);

	public List<Api> getApiListByIDs(@Param("apiIDs") List<Integer> apiIDs);

	public List<Api> getApiListByProjectIdAndGroupId(@Param("projectID") Integer projectID,@Param("groupID") Integer groupID);
	//恢复接口
	public Integer recoverApi(@Param("projectID") Integer projectID, @Param("groupID") Integer groupID,
			@Param("apiIDs") List<Integer> apiIDs);

	//彻底删除接口
	public Integer deleteApi(@Param("projectID") Integer projectID, @Param("apiIDs") List<Integer> apiIDs);

	//批量删除接口请求头部
	public void batchDeleteApiHeader(@Param("apiIDs") List<Integer> apiIDs);

	//批量删除接口请求头部参数
	public void batchDeleteRequestParam(@Param("apiIDs") List<Integer> apiIDs);

	//批量删除接口返回参数
	public void batchDeleteResultParam(@Param("apiIDs") List<Integer> apiIDs);

	//获取接口
	public Map<String, Object> getApi(@Param("projectID") Integer projectID, @Param("apiID") Integer apiID);

	public Map<String, Object> getApiById(@Param("apiID") Integer apiID);

	//获取接口信息
	public Api getApiInfo(@Param("projectID") Integer projectID, @Param("apiID") Integer apiID);

	//根据api的url获取api信息
	public Api getApiInfoByUrl(@Param("url") String url,@Param("apiRequestType") Integer apiRequestType);

	public List<Map<String, Object>> getApiByProjectId(@Param("projectID") Integer projectID,
													   @Param("groupIDS") List<Integer> groupIDS, @Param("orderBy") String orderBy,
													   @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

	//获取接口列表
	public List<Map<String, Object>> getApiList(@Param("projectID") Integer projectID,
			@Param("groupIDS") List<Integer> groupIDS, @Param("orderBy") String orderBy,
												@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

	public Integer getApiListNum(@Param("projectID") Integer projectID, @Param("groupIDS") List<Integer> groupIDS);

	public Integer getApiNum();

	public List<Map<String, Object>> getGroupApiViewList(@Param("projectID") Integer projectID);

	public List<Map<String, Object>> getAllIndexGroupApiViewListByIndices(@Param("indexIds") List<Integer> indexIds);

	//获取全部接口列表
	public List<Map<String, Object>> getAllApiList(@Param("projectID") Integer projectID, @Param("orderBy") String orderBy,@Param("offset") Integer offset,@Param("pageSize") Integer pageSize);

	//搜索接口
	public List<Map<String, Object>> searchApi(@Param("projectID") Integer projectID, @Param("tips") String tips);

	public List<Map<String, Object>> searchApiByName(@Param("projectID") Integer projectID, @Param("tips") String tips);

	public List<Map<String, Object>> searchAllApiByKeyword(@Param("keyword") String keyword,@Param("apiProtocol") Integer apiProtocol);


	public List<Map<String, Object>> searchApiByPath(@Param("projectID") Integer projectID, @Param("tips") String tips);

	public List<Api> searchAllApi(@Param("keyword") String keyword);

	//修改接口分组
	public int changeApiGroup(@Param("projectID") Integer projectID, @Param("apiIDs") List<Integer> apiIDs,
			@Param("groupID") Integer groupID);


	//获取项目全部接口
	public List<Api> getAllApiByProjectID(@Param("projectID") Integer projectID);


	//根据接口ID获取项目ID
	public Integer getProjectID(@Param("apiID") Integer apiID);

	public List<String> getApiUsers();

	public List<String> getTestApiUsers();

}
