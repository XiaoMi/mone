package com.xiaomi.miapi.mapper;

import com.xiaomi.miapi.dto.DocumentDTO;
import com.xiaomi.miapi.pojo.Document;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
public interface DocumentMapper {

	int addDocument(Document document);

	int deleteDocument(@Param("documentID") int documentID);


	List<DocumentDTO> getAllDocumentList(@Param("projectID") int projectID);

	int editDocument(Document document);

	List<DocumentDTO> searchDocument(@Param("projectID") int projectID, @Param("tips") String tips);

	List<DocumentDTO> searchDocumentByTitle(@Param("projectID") int projectID, @Param("tips") String tips);

	List<DocumentDTO> searchAllDocument(@Param("keyword") String keyword);

	Map<String, Object> getDocument(@Param("documentID") int documentID);

	int deleteDocuments(@Param("documentIDList") List<Integer> documentIDList,
			@Param("projectID") int projectID);

	String getDocumentTitle(@Param("documentIDList") List<Integer> documentIDList);
}
