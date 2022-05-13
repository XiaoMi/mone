package com.xiaomi.youpin.docean.plugin.es;

import com.xiaomi.mone.es.EsClient;
import com.xiaomi.mone.es.EsProcessor;
import com.xiaomi.mone.es.ProcessorConf;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.indices.GetIndexTemplatesRequest;
import org.elasticsearch.client.indices.IndexTemplateMetadata;
import org.elasticsearch.client.indices.IndexTemplatesExistRequest;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class EsService {
    private EsClient esClient;

    public EsClient getEsClient() {
        return this.esClient;
    }

    public EsService(String address, String useranme, String password) {
        this.esClient = new EsClient(address, useranme, password);
    }

    public SearchResponse search(SearchRequest searchRequest) throws IOException {
        return esClient.search(searchRequest);
    }

    public void insertDoc(String index, Map<String, Object> data) throws IOException {
        esClient.insertDoc(index, data);
    }

    public void insertDocAsyn(String index, Map<String, Object> data, ActionListener<IndexResponse> listener) {
        esClient.insertDocAsyn(index, data, listener);
    }

    /**
     * 批量保存 可能会丢失数据，建议使用bulkProcessor
     *
     * @param index
     * @param dataList
     * @return
     * @throws IOException
     */
    @Deprecated
    public boolean bulkInsertDoc(String index, List<Map<String, Object>> dataList) throws IOException {
        return esClient.bulkInsert(index, dataList);
    }

//    /**
//     * 批量插入
//     * @param indexName
//     * @param data
//     */
//    public void bulkInsertUseProcessor(String indexName, Map<String, Object> data) {
//        esProcessorClient.bulkInsert(indexName, data);
//    }

    public EsProcessor getEsProcessor(EsProcessorConf conf) {
        return new EsProcessor(new ProcessorConf(conf.getBulkActions(), conf.getByteSize(), conf.getConcurrentRequest(), conf.getFlushInterval(), conf.getRetryNumber(), conf.getRetryInterval(), this.esClient, conf.getListener()));
    }

    public void createIndex(String name, String mapping) throws IOException {
        esClient.createIndex(name, mapping);
    }

    public CreateIndexResponse createIndex(CreateIndexRequest request) throws IOException {
        return esClient.createIndex(request);
    }

    public org.elasticsearch.client.indices.CreateIndexResponse createIndex(org.elasticsearch.client.indices.CreateIndexRequest request) throws IOException {
        return esClient.createIndex(request);
    }

    public GetResponse get(GetRequest request) throws IOException {
        return esClient.get(request);
    }

    public SearchResponse queryByIndex(String index) throws IOException {
        return esClient.queryByIndex(index);
    }

    public DeleteResponse delete(DeleteRequest request) throws IOException {
        return esClient.delete(request);
    }

    public UpdateResponse update(UpdateRequest request) throws IOException {
        return esClient.update(request);
    }

    /**
     * 创建索引模板
     *
     * @param request
     * @return
     * @throws IOException
     */
    public boolean createIndexTemplate(PutIndexTemplateRequest request) throws IOException {
        return esClient.createIndexTemplate(request);
    }

    /**
     * 索引模板是否存在
     *
     * @param request
     * @return
     * @throws IOException
     */
    public boolean existsTemplate(IndexTemplatesExistRequest request) throws IOException {
        return esClient.existsTemplate(request);
    }

    /**
     * 获取索引模板
     *
     * @param request
     * @return
     * @throws IOException
     */
    public List<IndexTemplateMetadata> getIndexTemplates(GetIndexTemplatesRequest request) throws IOException {
        return esClient.getIndexTemplates(request);
    }



    /**
     * 数据直方图
     *
     * @param indexName
     * @param interval
     * @param startTime
     * @param endTime
     * @return
     */
    public EsClient.EsRet dateHistogram(String indexName, String interval, long startTime, long endTime, BoolQueryBuilder builder) throws IOException {
        return esClient.dateHistogram(indexName, interval, startTime, endTime, builder);
    }

    /**
     * 统计
     *
     * @param countRequest
     * @return
     * @throws IOException
     */
    public Long count(CountRequest countRequest) throws IOException {
        return esClient.count(countRequest);
    }

    public Integer getClusterHealth() throws IOException {
        return esClient.getClusterHealth();
    }

    public void searchAsync(SearchRequest searchRequest, ActionListener<SearchResponse> listener) {
        esClient.searchAsync(searchRequest, listener);
    }

}
