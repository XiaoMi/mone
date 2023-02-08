package com.xiaomi.miapi.common.pojo;
/**
 * 接口缓存bean
 */
public class ApiCache
{
	private Integer cacheID;//缓存ID
	private Integer projectID;//项目ID
	private Integer groupID;//分组ID
	private Integer apiID;//接口ID
	private String apiJson;//接口缓存数据
	private Integer starred;//接口星标状态
	private String updateUsername;//接口更新成员ID
	public Integer getCacheID() {
		return cacheID;
	}
	public void setCacheID(Integer cacheID) {
		this.cacheID = cacheID;
	}
	public Integer getProjectID() {
		return projectID;
	}
	public void setProjectID(Integer projectID) {
		this.projectID = projectID;
	}
	public Integer getGroupID() {
		return groupID;
	}
	public void setGroupID(Integer groupID) {
		this.groupID = groupID;
	}
	public Integer getApiID() {
		return apiID;
	}
	public void setApiID(Integer apiID) {
		this.apiID = apiID;
	}
	public String getApiJson() {
		return apiJson;
	}
	public void setApiJson(String apiJson) {
		this.apiJson = apiJson;
	}
	public Integer getStarred() {
		return starred;
	}
	public void setStarred(Integer starred) {
		this.starred = starred;
	}
	public String getUpdateUsername() {
		return updateUsername;
	}
	public void setUpdateUsername(String updateUsername) {
		this.updateUsername = updateUsername;
	}
}
