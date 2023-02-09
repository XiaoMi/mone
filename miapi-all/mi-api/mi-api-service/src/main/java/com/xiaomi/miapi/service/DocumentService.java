package com.xiaomi.miapi.service;

import com.xiaomi.miapi.dto.DocumentDTO;
import com.xiaomi.miapi.common.pojo.Document;
import com.xiaomi.miapi.common.Result;

import java.util.List;
import java.util.Map;

/**
 * 项目文档
 */
public interface DocumentService
{

	/**
	 * 添加文档
	 *
	 * @param document
	 * @return
	 * @throws RuntimeException
	 */
	public Result<Boolean> addDocument(Document document, String opUsername) throws RuntimeException;

	/**
	 * 修改文档
	 *
	 * @param document
	 * @return
	 * @throws RuntimeException
	 */
	public Result<Boolean> editDocument(Document document,String opUsername);

//	/**
//	 * 获取文档列表
//	 *
//	 * @return
//	 * @throws RuntimeException
//	 */
//	public List<DocumentDTO> getDocumentList(int groupID,int projectID) throws RuntimeException;

	/**
	 * 获取文档列表
	 *
	 * @param projectID
	 * @return
	 * @throws RuntimeException
	 */
	public Result<List<DocumentDTO>> getAllDocumentList(int projectID);

	/**
	 * 搜索文档
	 *
	 * @param projectID
	 * @param tips
	 * @return
	 * @throws RuntimeException
	 */
	public Result<List<DocumentDTO>> searchDocument(int projectID, String tips,Integer type);

	/**
	 * 获取文档详情
	 *
	 * @param documentID
	 * @return
	 * @throws RuntimeException
	 */
	public Result<Map<String, Object>> getDocument(int documentID);

	/**
	 * 批量文档
	 *
	 * @return
	 * @throws RuntimeException
	 */
	public Result<Boolean> deleteBatchDocument(List<Integer> documentIDs, int projectID,int userId,String opUsername);
}
