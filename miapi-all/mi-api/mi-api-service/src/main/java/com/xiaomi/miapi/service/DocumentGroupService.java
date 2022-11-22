package com.xiaomi.miapi.service;

import com.xiaomi.miapi.common.pojo.DocumentGroup;

import java.util.List;

/**
 * 项目文档分组
 */
public interface DocumentGroupService
{

	/**
	 * 添加文档分组
	 *
	 * @param documentGroup
	 * @return
	 * @throws RuntimeException
	 */
	public int addGroup(DocumentGroup documentGroup, String username) throws RuntimeException;

	/**
	 * 删除文档分组
	 *
	 * @param groupID
	 * @return
	 * @throws RuntimeException
	 */
	public int deleteGroup(int groupID,int projectId,String username) throws RuntimeException;

	/**
	 * 获取所有文档列表
	 *
	 * @param projectID
	 * @return
	 * @throws RuntimeException
	 */
	public List<DocumentGroup> getGroupList(int projectID) throws RuntimeException;

	/**
	 * 修改文档
	 *
	 * @return
	 * @throws RuntimeException
	 */
	public int editGroup(DocumentGroup documentGroup,String username) throws RuntimeException;

	/**
	 * 修改文档分组列表排序
	 *
	 * @param projectID
	 * @return
	 * @throws RuntimeException
	 */
	public int sortGroup(int projectID, String orderList,String username) throws RuntimeException;

}
