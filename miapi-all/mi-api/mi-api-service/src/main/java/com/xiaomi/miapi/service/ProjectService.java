package com.xiaomi.miapi.service;

import com.xiaomi.miapi.bo.ApiEnvBo;
import com.xiaomi.miapi.bo.Project;
import com.xiaomi.miapi.bo.ProjectGroupBo;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.pojo.ApiEnv;
import com.xiaomi.miapi.pojo.BusProjectGroup;
import com.xiaomi.miapi.vo.BusProjectVo;

import java.util.List;
import java.util.Map;

public interface ProjectService {
	Result<Boolean> addProject(Project project, String username);

	boolean focusProject(Integer projectId,String username);

	Result<Boolean> unFocusProject(Integer projectId,String username);

	List<BusProjectVo> getFocusProject(String username);

	boolean deleteProject(Integer projectID,String username);

	Result<List<BusProjectVo>> getProjectList(String username);

	Result<Map<String,List<Map<String,Object>>>> indexSearch(String keyword);

	Result<List<BusProjectVo>> getProjectListByProjectGroupId(Integer projectGroupID,String username);

	boolean editProject(Project project,String username);

	Result<Map<String, Object>> getProject(Integer projectID,String username);

	List<BusProjectVo> getRecentlyProjectList(String username);

	Result<Map<String,Object>> getMyProjects(String username);

	List<Map<String, Object>> getProjectLogList(Integer projectID, Integer page, Integer pageSize);

	int getProjectLogCount(Integer projectID, int dayOffset);

	int getApiNum(Integer projectID);

	Result<Boolean> createProjectGroup(ProjectGroupBo projectGroupBo, String username);

	Result<Boolean> updateProjectGroup(ProjectGroupBo projectGroupBo);

	List<BusProjectGroup> getAllProjectGroup();

	Result<BusProjectGroup> getProjectGroupById(Integer id);

	Result<Boolean> deleteProjectGroup(Integer projectGroupId, String userName);

	Result<Boolean> addApiEnv(ApiEnvBo bo, String opUsername);

	Result<Boolean> editApiEnv(ApiEnvBo bo,String opUsername);

	Result<Boolean> deleteApiEnv(Integer envID,String opUsername);

	Result<ApiEnv> getApiEnv(Integer envID);

	Result<List<ApiEnv>> getApiEnvList(Integer projectID);

}
