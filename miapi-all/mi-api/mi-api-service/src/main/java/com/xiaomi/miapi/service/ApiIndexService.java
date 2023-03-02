package com.xiaomi.miapi.service;

import com.xiaomi.miapi.pojo.IndexInfo;
import com.xiaomi.miapi.common.Result;

import java.util.List;
import java.util.Map;

public interface ApiIndexService {
    Result<Boolean> batchGroupApis(String apiIDs, Integer indexID, String username);

    Result<Boolean> removeApiFromIndex(Integer apiID, Integer indexID, String username);

    Result<Integer> addIndex(Integer projectId, String indexName, String description,String indexDoc, String username);

    Result<Integer> editIndex(String indexName, Integer indexId, String description,String indexDoc, String username);

    Result<Boolean> deleteIndex(Integer indexId, String username);

    Result<List<IndexInfo>> getIndexList(Integer projectId);

    Result<List<Map<String,Object>>> getIndexPageInfo(String indexIDs);
}

