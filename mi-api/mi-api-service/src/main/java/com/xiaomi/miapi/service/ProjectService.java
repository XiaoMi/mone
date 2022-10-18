package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.bo.ApiEnvBo;
import com.xiaomi.miapi.common.bo.ProjectGroupBo;
import com.xiaomi.miapi.common.pojo.ApiEnv;
import com.xiaomi.miapi.common.pojo.Project;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.vo.BusProjectVo;
import com.xiaomi.youpin.hermes.entity.ProjectGroup;

import java.util.List;
import java.util.Map;

/**
 * 自动化测试用例
 */
public interface ProjectService
{

	// 新增项目
	public Result<Boolean> addProject(Project project, Integer userID, String username);

	public boolean focusProject(Integer projectId,Integer accountId);

	public Result<Boolean> unFocusProject(Integer projectId,Integer accountId);

	public List<BusProjectVo> getFocusProject(Integer accountId);

	// 删除项目
	public boolean deleteProject(Integer projectID,Integer userId,String username);

	// 获取项目列表
	public Result<List<BusProjectVo>> getProjectList(Integer userId);

	public Result<Map<String,List<Map<String,Object>>>> indexSearch(String keyword);

	public Result<List<BusProjectVo>> getProjectListByProjectGroupId(Integer projectGroupID,Integer userId,String username);

	// 修改项目
	public boolean editProject(Project project,String username);

	//获取项目详情
	public Result<Map<String, Object>> getProject(Integer projectID,Integer userId);

	public List<BusProjectVo> getRecentlyProjectList(Integer userId);

	public Result<Map<String,Object>> getMyProjects(Integer userId);
	//获取项目日志列表
	public List<Map<String, Object>> getProjectLogList(Integer projectID, Integer page, Integer pageSize);

	//获取项目日志条数
	public int getProjectLogCount(Integer projectID, int dayOffset);

	//获取接口数量
	public int getApiNum(Integer projectID);

	//===========项目组相关==========
	Result<Boolean> createProjectGroup(ProjectGroupBo projectGroupBo,int userId);

	Result<Boolean> updateProjectGroup(ProjectGroupBo projectGroupBo);

	List<ProjectGroup> getAllProjectGroup();

	List<ProjectGroup> getAllAccessableProjectGroup(int accountId);

	Result<ProjectGroup> getProjectGroupById(Integer id);

	Result<Boolean> deleteProjectGroup(Integer projectGroupId, String userName);

	public Result<Boolean> addApiEnv(ApiEnvBo bo, String opUsername);

	public Result<Boolean> editApiEnv(ApiEnvBo bo,String opUsername);

	public Result<Boolean> deleteApiEnv(Integer envID,String opUsername);

	public Result<ApiEnv> getApiEnv(Integer envID);

	public Result<List<ApiEnv>> getApiEnvList(Integer projectID);


//	public Result<Boolean> importMioneProject(Project project, Integer userID,String username);

}
