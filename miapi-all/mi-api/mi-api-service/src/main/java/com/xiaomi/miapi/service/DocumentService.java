package com.xiaomi.miapi.service;

import com.xiaomi.miapi.dto.DocumentDTO;
import com.xiaomi.miapi.pojo.Document;
import com.xiaomi.miapi.common.Result;

import java.util.List;
import java.util.Map;

public interface DocumentService
{

	Result<Boolean> addDocument(Document document, String opUsername) throws RuntimeException;

	Result<Boolean> editDocument(Document document,String opUsername);


	Result<List<DocumentDTO>> getAllDocumentList(int projectID);

	Result<List<DocumentDTO>> searchDocument(int projectID, String tips,Integer type);

	Result<Map<String, Object>> getDocument(int documentID);

	Result<Boolean> deleteBatchDocument(List<Integer> documentIDs, int projectID,int userId,String opUsername);
}
