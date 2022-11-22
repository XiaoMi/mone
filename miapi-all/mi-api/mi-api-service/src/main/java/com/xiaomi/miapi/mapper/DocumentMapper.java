package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.dto.DocumentDTO;
import com.xiaomi.miapi.common.pojo.Document;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 项目文档[数据库操作]
 */
public interface DocumentMapper
{

	/**
	 * 添加文档
	 *
	 * @param document
	 * @return
	 */
	public int addDocument(Document document);

	/**
	 * 删除文档
	 *
	 * @return
	 */
	public int deleteDocument(@Param("documentID") int documentID);

	/**
	 * 根据分组获取文档列表
	 *
	 * @return
	 */
	public List<DocumentDTO> getDocumentList(@Param("groupIDS") List<Integer> groupIDS);

	/**
	 * 获取所有文档列表
	 *
	 * @return
	 */
	public List<DocumentDTO> getAllDocumentList(@Param("projectID") int projectID);

	/**
	 * 修改文档
	 *
	 * @param document
	 * @return
	 */
	public int editDocument(Document document);

	/**
	 * 项目内搜索文档
	 *
	 * @param tips
	 * @return
	 */
	public List<DocumentDTO> searchDocument(@Param("projectID") int projectID, @Param("tips") String tips);

	/**
	 * 项目内根据标题搜索文档
	 *
	 * @param tips
	 * @return
	 */
	public List<DocumentDTO> searchDocumentByTitle(@Param("projectID") int projectID, @Param("tips") String tips);

	/**
	 * 项目内根据内容搜索文档
	 *
	 * @param tips
	 * @return
	 */
	public List<DocumentDTO> searchDocumentByContent(@Param("projectID") int projectID, @Param("tips") String tips);

	/**
	 * 搜索全部文档
	 *
	 * @param
	 * @return
	 */
	public List<DocumentDTO> searchAllDocument(@Param("keyword") String keyword);
	/**
	 * 获取文档详情
	 *
	 * @return
	 */
	public Map<String, Object> getDocument(@Param("documentID") int documentID);

	/**
	 * 批量删除文档
	 *
	 * @param documentIDList
	 * @param projectID
	 * @return
	 */
	public int deleteDocuments(@Param("documentIDList") List<Integer> documentIDList,
			@Param("projectID") int projectID);

	/**
	 * 获取文档标题
	 *
	 * @param documentIDList
	 * @return
	 */
	public String getDocumentTitle(@Param("documentIDList") List<Integer> documentIDList);
}
