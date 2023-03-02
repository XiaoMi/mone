package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.pojo.Api;
import com.xiaomi.miapi.pojo.ApiHeader;
import com.xiaomi.miapi.pojo.ApiResultParam;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface ApiMapper
{
	Integer getApiCount(@Param("projectID") Integer projectID);

	Integer addApi(Api api);

	Integer deleteApiByGroupID(@Param("groupIDS") List<Integer> groupIDS, @Param("removeTime") Timestamp removeTime);


	Integer updateApi(Api api);

	String getApiNameByIDs(@Param("apiIDs") List<Integer> apiIDs);

	List<Api> getApiListByIDs(@Param("apiIDs") List<Integer> apiIDs);


	Integer deleteApi(@Param("projectID") Integer projectID, @Param("apiIDs") List<Integer> apiIDs);

	void batchDeleteApiHeader(@Param("apiIDs") List<Integer> apiIDs);

	void batchDeleteRequestParam(@Param("apiIDs") List<Integer> apiIDs);

	void batchDeleteResultParam(@Param("apiIDs") List<Integer> apiIDs);

	Map<String, Object> getApi(@Param("projectID") Integer projectID, @Param("apiID") Integer apiID);

	Map<String, Object> getApiById(@Param("apiID") Integer apiID);

	Api getApiInfo(@Param("projectID") Integer projectID, @Param("apiID") Integer apiID);

	Api getApiInfoByUrlAndProject(@Param("url") String url, @Param("apiRequestType") Integer apiRequestType, @Param("projectID") Integer projectID);

	Api getApiInfoByUrl(@Param("url") String url, @Param("apiRequestType") Integer apiRequestType);

	List<Map<String, Object>> getApiByProjectId(@Param("projectID") Integer projectID,
													   @Param("groupIDS") List<Integer> groupIDS, @Param("orderBy") String orderBy,
													   @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

	List<Map<String, Object>> getApiList(@Param("projectID") Integer projectID,
			@Param("groupIDS") List<Integer> groupIDS, @Param("orderBy") String orderBy,
												@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

	Integer getApiListNum(@Param("projectID") Integer projectID, @Param("groupIDS") List<Integer> groupIDS);

	List<Map<String, Object>> getGroupApiViewList(@Param("projectID") Integer projectID,@Param("orderBy") String orderBy);

	List<Map<String, Object>> getAllIndexGroupApiViewListByIndices(@Param("indexIds") List<Integer> indexIds);

	List<Map<String, Object>> getAllApiList(@Param("projectID") Integer projectID, @Param("orderBy") String orderBy,@Param("offset") Integer offset,@Param("pageSize") Integer pageSize);

	List<Map<String, Object>> searchApi(@Param("projectID") Integer projectID, @Param("tips") String tips);

	List<Map<String, Object>> searchApiByName(@Param("projectID") Integer projectID, @Param("tips") String tips);

	List<Map<String, Object>> searchApiByPath(@Param("projectID") Integer projectID, @Param("tips") String tips);

	List<Api> searchAllApi(@Param("keyword") String keyword);

	List<Api> getAllApiByProjectID(@Param("projectID") Integer projectID);

	Integer getProjectID(@Param("apiID") Integer apiID);

}
