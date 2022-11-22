package com.xiaomi.youpin.gwdash.service;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.mysql.jdbc.Connection;
import com.xiaomi.youpin.gwdash.bo.ApiGroupInfoDTO;
import com.xiaomi.youpin.gwdash.bo.IdListParam;
import com.xiaomi.youpin.gwdash.bo.MetaDataParam;
import com.xiaomi.youpin.gwdash.common.MetaDataRelationTypeEnum;
import com.xiaomi.youpin.gwdash.common.MetaDataTypeEnum;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.MetaData;
import com.xiaomi.youpin.gwdash.dao.model.MetaDataRelation;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.trans.Atom;
import org.nutz.trans.Trans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Service(group="${owner.dubbo.group}", interfaceClass = GroupClusterServiceAPI.class, timeout = 2000)
public class ApiGroupClusterService implements GroupClusterServiceAPI{

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGroupClusterService.class);

    @Autowired
    private Dao dao;

    @Autowired
    private GroupServiceApiRpc groupServiceAPI;

    @Autowired
    private MetaDataService metaDataService;

    @Autowired
    private MetaDataRelationService metaDataRelationService;

    @NacosValue(value = "${gateway.default.domain}", autoRefreshed = true)
    private String defaultDomain;

    /**
     * 新增ApiGroupCluster
     *
     * @param param
     * @return
     */
    public Result<Void> newApiGroupCluster(MetaDataParam param) {
        if (metaDataService.verifyExistByName(param.getName(), MetaDataTypeEnum.APiGroupCluster.getType())) {
            return new Result<>(CommonError.UnknownError.getCode(), "存在相同Name");
        }
        return metaDataService.newMetadata(param, MetaDataTypeEnum.APiGroupCluster.getType());
    }

    /**
     * 更新ApiGroupCluster
     *
     * @param param
     * @return
     */
    public Result<Void> updateApiGroupCluster(MetaDataParam param) {
        MetaData metaData = metaDataService.getMetaDataById(param.getId());
        if (metaData == null) {
            return new Result<>(CommonError.UnknownError.getCode(), "记录不存在");
        }
        if (metaData.getType() != MetaDataTypeEnum.APiGroupCluster.getType()) {
            return new Result<>(CommonError.UnknownError.getCode(), "参数异常");
        }
        return metaDataService.updateMetaData(param);
    }

    /**
     * 删除ApiGroupCluster，级联删除apiGroupClusterRelation
     *
     * @param id
     * @return
     */
    public Result<Void> deleteApiGroupCluster(Integer id) {
        MetaData metaData = metaDataService.getMetaDataById(id);
        if (metaData == null) {
            return new Result<>(CommonError.UnknownError.getCode(), "记录不存在");
        }
        if (metaData.getType() != MetaDataTypeEnum.APiGroupCluster.getType()) {
            return new Result<>(CommonError.UnknownError.getCode(), "参数异常");
        }
        return metaDataService.deleteMetaData(id);
    }

    /**
     * 获取ApiGroupCluster 列表
     *
     * @param page
     * @param pagesize
     * @return
     */
    public Map<String, Object> getApiGroupClusterList(String name, int page, int pagesize) {
        return metaDataService.getMetaDataList(name, page, pagesize, MetaDataTypeEnum.APiGroupCluster.getType());
    }

    /**
     * 对api分组或者domain进行聚合到apigroupcluster
     *
     * @param param
     * @param type
     * @return
     */
    public Result<Void> updateMetaDataRelation(IdListParam param, int type) {
        MetaData md = metaDataService.getMetaDataById(param.getId());
        if (md == null) {
            return new Result<>(CommonError.UnknownError.getCode(), "分组聚合不存在");
        }
        if (md.getType() != MetaDataTypeEnum.APiGroupCluster.getType()) {
            return new Result<>(CommonError.UnknownError.getCode(), "参数异常");
        }

        List<Integer> oldTargetIds = null;
        oldTargetIds = metaDataRelationService.getTargetListBySource(new ArrayList<Integer>() {{
            add(param.getId());
        }}, type);

        List<MetaDataRelation> list = new ArrayList<>();
        for (int target : param.getList()
        ) {
            MetaDataRelation mdr = new MetaDataRelation(param.getId(), target, type);
            list.add(mdr);
        }
        Gson gson = new Gson();
        LOGGER.info("updateMetaDataRelation param:[{}],type:[{}],list:[{}]",gson.toJson(param),type,gson.toJson(list));
        // 原子执行
        Trans.exec(Connection.TRANSACTION_REPEATABLE_READ, new Atom() {
            @Override
            public void run() {
                dao.clear(MetaDataRelation.class, Cnd.where("source", "=", param.getId()).and("type", "=", type));
                dao.insert(list);
            }
        });
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
    }

    public Result<Void> deleteMetaDataRelaotion(IdListParam param, int type) {
        MetaData md = metaDataService.getMetaDataById(param.getId());
        if (md == null) {
            return new Result<>(CommonError.UnknownError.getCode(), "分组聚合不存在");
        }
        if (md.getType() != MetaDataTypeEnum.APiGroupCluster.getType()) {
            return new Result<>(CommonError.UnknownError.getCode(), "参数异常");
        }
        dao.clear("metadata_relation", Cnd.where("source", "=", param.getId()).and("target", "in", param.getList()).and("type", "=", type));
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
    }

    public Map<String, Object> getDomainListByApiGroupClusterId(int id) {
        List<MetaDataRelation> mdrList = metaDataRelationService.getMetaDataRelationBySource(id, MetaDataRelationTypeEnum.ApiGroupCluster2Domain.getType());
        List<Integer> list = new ArrayList<>();
        for (MetaDataRelation mdr : mdrList
        ) {
            list.add(mdr.getTarget());
        }
        List<MetaData> metaDataList = metaDataService.getMetaDataList(list, MetaDataTypeEnum.Domain.getType());
        Map<String, Object> ret = new HashMap<>();
        ret.put("metaDataList", metaDataList);
        ret.put("total", metaDataRelationService.getMetaDataRelationCountsById(id, MetaDataRelationTypeEnum.ApiGroupCluster2Domain.getType()));
        return ret;
    }

    @Override
    public Map<String, Object> getApiGroupByApiGroupClusterId(int id) {
        List<MetaDataRelation> mdrList = metaDataRelationService.getMetaDataRelationBySource(id, MetaDataRelationTypeEnum.ApiGroupCluster2ApiGroup.getType());
        List<Integer> list = new ArrayList<>();
        for (MetaDataRelation mdr : mdrList
        ) {
            list.add(mdr.getTarget());
        }
//        List<GwApiGroup> gag = dao.query(GwApiGroup.class, Cnd.where("id", "in", list));
        List<ApiGroupInfoDTO> apiGroups = groupServiceAPI.describeGroupsByApiIds(list);
        LOGGER.info("getApiGroupByApiGroupClusterId id:[{}],list:[{}],apiGroups:[{}]",id,list,new Gson().toJson(apiGroups));
        Map<String, Object> ret = new HashMap<>();
        ret.put("metaDataList", apiGroups);
        ret.put("total", metaDataRelationService.getMetaDataRelationCountsById(id, MetaDataRelationTypeEnum.ApiGroupCluster2ApiGroup.getType()));
        return ret;
    }

    @Deprecated
    public Map<String, String> getDomainRefer() {
        return getDomainRefer(null);
    }

    public Map<String, String> getDomainRefer(String tenant) {
        List<MetaData> metaDatas = metaDataService.getMetaDataList(MetaDataTypeEnum.Domain.getType(),tenant);
        if (metaDatas == null || metaDatas.size() == 0) {
            return new HashMap<>();
        }
        return metaDatas.stream().filter(it -> it.getReferHeader() != null).collect(Collectors.toMap(MetaData::getName, MetaData::getReferHeader,(k1, k2)->k1));
    }

    public Map<Integer, List<String>> getApiDomainList(List<Integer> gids) {
        Map<Integer, List<String>> gid2DomainList = new HashMap<>();
        try {
            List<String> defaultDomainList = Arrays.asList(defaultDomain.split(","));
            // List<GwApiGroup> apiGroups = dao.query(GwApiGroup.class, Cnd.where("gid", "in", gids));
            List<ApiGroupInfoDTO> apiGroups = groupServiceAPI.describeGroupsByIds(gids);
            HashMap<Integer, Integer> groupId2gidMap = new HashMap<>();
            for (ApiGroupInfoDTO gag : apiGroups) {
                groupId2gidMap.put(gag.getId(), gag.getGid()); //id - gid
            }

            // 获取 groupId:List<groupClusterId>
            List<MetaDataRelation> groupCluster2GroupListRelation = metaDataRelationService.getMetaDataRelationByTargetList(new ArrayList<Integer>(groupId2gidMap.keySet()), MetaDataRelationTypeEnum.ApiGroupCluster2ApiGroup.getType());
            Map<Integer, List<Integer>> groupId2groupClusterIdList = new HashMap<>();
            for (MetaDataRelation mdr : groupCluster2GroupListRelation) {
                List<Integer> list = groupId2groupClusterIdList.get(mdr.getTarget());
                if (list == null) {
                    list = new ArrayList<>();
                }
                list.add(mdr.getSource());
                groupId2groupClusterIdList.put(mdr.getTarget(), list);
            }

            // 获取groupId : List<Domain>
            Map<Integer, List<String>> groupId2DomainList = new HashMap<>();
            for (Integer groupId:groupId2groupClusterIdList.keySet()){
                List<Integer> groupClusterIds = groupId2groupClusterIdList.get(groupId);
                groupId2DomainList.put(groupId,getDomainListByGroupClusterIds(groupClusterIds));
            }

            for (Integer groupId:groupId2gidMap.keySet()){
                gid2DomainList.put(groupId2gidMap.get(groupId),groupId2DomainList.get(groupId));
            }

            for (Integer groupId : gids) {
                List<String> domainList = gid2DomainList.get(groupId);
                //若metadata_relation表无此gid对应的记录，说明该group仅允许通过默认domain访问
                if (CollectionUtils.isEmpty(domainList) && !"*".equals(defaultDomain)) {
                    gid2DomainList.put(groupId, defaultDomainList);
                }
            }
        } catch (Exception e) {
            LOGGER.error("[ApiGroupClusterService.getApiDomainList] failed , gids: {}, err: {}",
                    Arrays.toString(gids.toArray()), e);
        }
        return gid2DomainList;
    }

    // 获取groupCluster 下所拥有的 domain 列表
    public List<String> getDomainListByGroupClusterIds(List<Integer> groupClusterIds) {
        List<MetaDataRelation> groupCluster2DomainRelation = metaDataRelationService.getMetaDataRelationBySourceList(groupClusterIds, MetaDataRelationTypeEnum.ApiGroupCluster2Domain.getType());
        Set<Integer> domainIdSet = new HashSet<>();
        for (MetaDataRelation mdr : groupCluster2DomainRelation) {
            domainIdSet.add(mdr.getTarget());
        }
        List<Integer> domainIdList = new ArrayList<>(domainIdSet);
        List<MetaData> domainList = metaDataService.getMetaDataList(domainIdList, MetaDataTypeEnum.Domain.getType());
        List<String> domainStrList = new ArrayList<>();
        for (MetaData domain : domainList) {
            domainStrList.add(domain.getName());
        }
        return domainStrList;
    }
}
