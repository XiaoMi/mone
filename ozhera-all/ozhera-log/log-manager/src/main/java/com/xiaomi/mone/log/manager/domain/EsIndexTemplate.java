/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.domain;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.common.Es;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.mapper.MilogEsClusterMapper;
import com.xiaomi.mone.log.manager.mapper.MilogEsIndexMapper;
import com.xiaomi.mone.log.manager.model.dto.EsInfoDTO;
import com.xiaomi.mone.log.manager.model.pojo.MilogEsClusterDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogEsIndexDO;
import com.xiaomi.mone.log.manager.model.vo.CreateIndexTemplatePropertyCommand;
import com.xiaomi.mone.log.manager.model.vo.CreateOrUpdateLogStoreCmd;
import com.xiaomi.mone.log.manager.model.vo.UpdateIndexTemplateCommand;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.IndexTemplatesExistRequest;
import org.elasticsearch.client.indices.PutIndexTemplateRequest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

import static com.xiaomi.mone.log.manager.common.Utils.getRandomNum;

@Service
@Slf4j
public class EsIndexTemplate {

    @Resource
    private EsCluster esCluster;

    @Resource
    private MilogEsIndexMapper esIndexMapper;

    @Resource
    private MilogEsClusterMapper milogEsClusterMapper;

    /**
     * Create an index template
     *
     * @param command
     * @return
     * @throws IOException
     */
    public boolean createIndexTemplate(CreateOrUpdateLogStoreCmd command) throws IOException {
        // Verify that the template exists
        if (existTemplate(command.getEsIndex())) {
            return false;
        }
        PutIndexTemplateRequest request = new PutIndexTemplateRequest(command.getEsIndex());
        // Match the name
        request.patterns(Arrays.asList(command.getEsIndex() + "*"));
        // Set up an alias alias
        request.alias(new Alias(command.getEsIndex()));
        // Priority
        request.order(20);
        // Version number
        request.version(0);
        // Template settings
        buildSetting(request, command);
        // Template mapping
        buildMapping(request, command);
        EsService esService = esCluster.getEsService(null);
        return esService.createIndexTemplate(request) && createIndex(command.getEsIndex());
    }

    public boolean createIndex(String indexName) throws IOException {
        if (existTemplate(indexName) == false) {
            return false;
        }
        CreateIndexRequest request = new CreateIndexRequest(indexName + Es.indexPostfix());
        EsService esService = esCluster.getEsService(null);
        CreateIndexResponse response = esService.createIndex(request);
        return response.isAcknowledged();
    }

    /**
     * Build the setting
     *
     * @param request
     * @param command
     */
    public void buildSetting(PutIndexTemplateRequest request, CreateOrUpdateLogStoreCmd command) {
//        request.settings(Settings.builder()
//                // Number of shards
//                .put("index.number_of_shards" , command.getShardCnt())
//                // ILM name
//                .put("index.lifecycle.name", command.getStorePeriod() + "Del")
//                // Rollback index aliases
////                .put("index.lifecycle.rollover_alias", createIndexTemplateCommand.getIndexTemplateName() + indexTemplate.getIndexPostfix())
//        );
    }

    /**
     * Build the setting
     *
     * @param request
     * @param updateIndexTemplateCommand
     */
    public void buildSetting(PutIndexTemplateRequest request, UpdateIndexTemplateCommand updateIndexTemplateCommand) {
//        request.settings(Settings.builder()
//                // Number of shards
//                .put("index.number_of_shards" , updateIndexTemplateCommand.getIndexShards())
//                .put("index.number_of_replicas", updateIndexTemplateCommand.getIndexReplicas())
//                // ILM name
//                .put("index.lifecycle.name",updateIndexTemplateCommand.getLifecycle())
        // Rollback index aliases
//                .put("index.lifecycle.rollover_alias", updateIndexTemplateCommand.getIndexTemplateName() + indexTemplate.getIndexPostfix())
//        );
    }

    /**
     * Build mapping
     *
     * @param request
     * @param command
     */
    public void buildMapping(PutIndexTemplateRequest request, CreateOrUpdateLogStoreCmd command) {
        String[] keyArray = command.getKeyList().split(",");
        String[] keyTypeArray = command.getColumnTypeList().split(",");
        Map<String, Object> mapping = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> property;
        for (int i = 0; i < keyArray.length; i++) {
            property = new HashMap<>();
            property.put("type", keyTypeArray[i]);
            properties.put(keyArray[i], property);
        }
        mapping.put("properties", properties);
        request.mapping(mapping);
    }

    /**
     * Build mapping
     *
     * @param request
     * @param updateIndexTemplateCommand
     */
    public void buildMapping(PutIndexTemplateRequest request, UpdateIndexTemplateCommand updateIndexTemplateCommand) {
        List<CreateIndexTemplatePropertyCommand> propertyList = updateIndexTemplateCommand.getPropertyList();
        if (propertyList == null || propertyList.isEmpty()) {
            return;
        }
        Map<String, Object> mapping = new HashMap<>();
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> property;
        for (CreateIndexTemplatePropertyCommand p : propertyList) {
            property = new HashMap<>();
            property.put("type", p.getType());
            properties.put(p.getName(), property);
        }
        mapping.put("properties", properties);
        request.mapping(mapping);
    }

    /**
     * Whether the template exists
     *
     * @param templateName
     * @return
     * @throws IOException
     */
    public boolean existTemplate(String templateName) throws IOException {
        if (StringUtils.isEmpty(templateName)) {
            return false;
        }
        IndexTemplatesExistRequest request = new IndexTemplatesExistRequest(templateName);
        EsService esService = esCluster.getEsService(null);
        return esService.existsTemplate(request);
    }

    /**
     * Gets the index of this type under the region
     *
     * @return
     */
    public String getAreaTypeIndex(String area, Integer logType) {
        MilogEsClusterDO cluster = esCluster.getByArea4China(area);
        if (cluster == null) {
            return "";
        }
        return getClusterTypeIndex(cluster.getId(), logType);
    }

    /**
     * Obtain the index of this type under the data center
     *
     * @return
     */
    public String getRegionTypeIndex(String region, Integer logType) {
        MilogEsClusterDO esClusterDO = esCluster.getByRegion(region);
        if (esClusterDO == null) {
            return "";
        }
        return getClusterTypeIndex(esClusterDO.getId(), logType);
    }

    /**
     * Obtain the index of this type under the ES cluster
     *
     * @param logType
     * @return
     */
    public String getClusterTypeIndex(Long esClusterId, Integer logType) {
        if (esClusterId == null || logType == null) {
            return "";
        }
        Map<String, Object> params = new HashMap<>();
        params.put("cluster_id", esClusterId);
        params.put("log_type", logType);
        List<MilogEsIndexDO> indexList = esIndexMapper.selectByMap(params);
        return indexChooseAlgo(indexList);
    }

    /**
     * Index selection logic
     *
     * @param indexList
     * @return
     */
    private String indexChooseAlgo(List<MilogEsIndexDO> indexList) {
        return randomChoose(indexList);
    }

    /**
     * Randomly select an index
     *
     * @param indexList
     * @return
     */
    private String randomChoose(List<MilogEsIndexDO> indexList) {
        if (indexList == null || indexList.isEmpty()) {
            return "";
        }
        return indexList.get(new Random().nextInt(indexList.size())).getIndexName();
    }

    /**
     * Get ES information
     *
     * @param machineRoom
     * @param logType
     * @return
     */
    public EsInfoDTO getEsInfo(String machineRoom, Integer logType) {
        MilogEsClusterDO cluster;
        // There is no machine room, it is processed according to domestic logic
        if (StringUtils.isEmpty(machineRoom)) {
            cluster = esCluster.getCurEsCluster();
        } else {
            cluster = esCluster.getByArea4China(machineRoom);
        }
        if (cluster == null) {
            return null;
        }
        String index = getClusterTypeIndex(cluster.getId(), logType);
        return new EsInfoDTO(cluster.getId(), index);
    }

    public EsInfoDTO getEsInfo(Long clusterId, Integer logType, String exIndex) {
        QueryWrapper<MilogEsIndexDO> queryWrapper = new QueryWrapper<MilogEsIndexDO>()
                .eq("cluster_id", clusterId)
                .eq("log_type", logType);
        List<MilogEsIndexDO> milogEsIndexDOS = esIndexMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(milogEsIndexDOS)) {
            MilogEsClusterDO milogEsClusterDO = milogEsClusterMapper.selectById(clusterId);
            throw new MilogManageException(String.format(
                    "ES:%s,log type:%s The index does not exist, go to the Resource Management page to configure it first", milogEsClusterDO.getName(), LogTypeEnum.queryNameByType(logType)));
        }
        if (StringUtils.isNotEmpty(exIndex)) {
            Optional<MilogEsIndexDO> doOptional = milogEsIndexDOS.stream()
                    .filter(indexDO -> Objects.equals(exIndex, indexDO.getIndexName()))
                    .findFirst();
            if (doOptional.isPresent()) {
                return new EsInfoDTO(doOptional.get().getClusterId(), exIndex);
            }
        }
        String indexName = indexChooseAlgo(milogEsIndexDOS);
        return new EsInfoDTO(clusterId, indexName);
    }

    public EsInfoDTO getEsInfoOtherDept(Long clusterId, Integer logType, String exIndex) {
        QueryWrapper<MilogEsIndexDO> queryWrapper = new QueryWrapper<MilogEsIndexDO>()
                .eq("cluster_id", clusterId)
                .eq("log_type", logType);
        List<MilogEsIndexDO> milogEsIndexDOS = esIndexMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(milogEsIndexDOS)) {
            MilogEsClusterDO milogEsClusterDO = milogEsClusterMapper.selectById(clusterId);
            throw new MilogManageException(String.format(
                    "ES:%s,log type:%s The index does not exist, go to the Resource Management page to configure it first", milogEsClusterDO.getName(), LogTypeEnum.queryNameByType(logType)));
        }
        if (StringUtils.isNotEmpty(exIndex)) {
            Optional<MilogEsIndexDO> doOptional = milogEsIndexDOS.stream()
                    .filter(indexDO -> Objects.equals(exIndex, indexDO.getIndexName()))
                    .findFirst();
            if (doOptional.isPresent()) {
                return new EsInfoDTO(doOptional.get().getClusterId(), exIndex);
            }
        }
        // Choose one at random
        MilogEsIndexDO milogEsIndexDO = milogEsIndexDOS.get(getRandomNum(milogEsIndexDOS.size()));
        return new EsInfoDTO(clusterId, milogEsIndexDO.getIndexName());
    }

}
