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
     * 创建索引模板
     *
     * @param command
     * @return
     * @throws IOException
     */
    public boolean createIndexTemplate(CreateOrUpdateLogStoreCmd command) throws IOException {
        // 校验模板是否存在
        if (existTemplate(command.getEsIndex())) {
            return false;
        }
        PutIndexTemplateRequest request = new PutIndexTemplateRequest(command.getEsIndex());
        // 匹配名称
        request.patterns(Arrays.asList(command.getEsIndex() + "*"));
        // 设置别名别名
        request.alias(new Alias(command.getEsIndex()));
        // 优先级
        request.order(20);
        // 版本号
        request.version(0);
        // 模板设置
        buildSetting(request, command);
        // 模板映射
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
     * 构建setting
     *
     * @param request
     * @param command
     */
    public void buildSetting(PutIndexTemplateRequest request, CreateOrUpdateLogStoreCmd command) {
//        request.settings(Settings.builder()
//                // 分片数
//                .put("index.number_of_shards" , command.getShardCnt())
//                // ILM名称
//                .put("index.lifecycle.name", command.getStorePeriod() + "Del")
//                // 回滚索引别名
////                .put("index.lifecycle.rollover_alias", createIndexTemplateCommand.getIndexTemplateName() + indexTemplate.getIndexPostfix())
//        );
    }

    /**
     * 构建setting
     *
     * @param request
     * @param updateIndexTemplateCommand
     */
    public void buildSetting(PutIndexTemplateRequest request, UpdateIndexTemplateCommand updateIndexTemplateCommand) {
//        request.settings(Settings.builder()
//                // 分片数
//                .put("index.number_of_shards" , updateIndexTemplateCommand.getIndexShards())
//                .put("index.number_of_replicas", updateIndexTemplateCommand.getIndexReplicas())
//                // ILM名称
//                .put("index.lifecycle.name",updateIndexTemplateCommand.getLifecycle())
        // 回滚索引别名
//                .put("index.lifecycle.rollover_alias", updateIndexTemplateCommand.getIndexTemplateName() + indexTemplate.getIndexPostfix())
//        );
    }

    /**
     * 构建mapping
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
     * 构建mapping
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
     * 模板是否存在
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
     * 获取区域下此类型的索引
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
     * 获取机房下此类型的索引
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
     * 获取es集群下此类型的索引
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
     * 索引选择逻辑
     *
     * @param indexList
     * @return
     */
    private String indexChooseAlgo(List<MilogEsIndexDO> indexList) {
        return randomChoose(indexList);
    }

    /**
     * 随机选择索引
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
     * 获取ES信息
     *
     * @param machineRoom
     * @param logType
     * @return
     */
    public EsInfoDTO getEsInfo(String machineRoom, Integer logType) {
        MilogEsClusterDO cluster;
        // 没有机房，按照国内的逻辑处理
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
                    "ES:%s,日志类型:%s索引不存在,请先去资源管理页配置", milogEsClusterDO.getName(), LogTypeEnum.queryNameByType(logType)));
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
                    "ES:%s,日志类型:%s索引不存在,请先去资源管理页配置", milogEsClusterDO.getName(), LogTypeEnum.queryNameByType(logType)));
        }
        if (StringUtils.isNotEmpty(exIndex)) {
            Optional<MilogEsIndexDO> doOptional = milogEsIndexDOS.stream()
                    .filter(indexDO -> Objects.equals(exIndex, indexDO.getIndexName()))
                    .findFirst();
            if (doOptional.isPresent()) {
                return new EsInfoDTO(doOptional.get().getClusterId(), exIndex);
            }
        }
        // 随机选择一个
        MilogEsIndexDO milogEsIndexDO = milogEsIndexDOS.get(getRandomNum(milogEsIndexDOS.size()));
        return new EsInfoDTO(clusterId, milogEsIndexDO.getIndexName());
    }

}
