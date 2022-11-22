/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.dao;

import com.xiaomi.youpin.gwdash.bo.ListParam;
import com.xiaomi.youpin.gwdash.bo.UserCollectionInfo;
import com.xiaomi.youpin.gwdash.common.Consts;
import com.xiaomi.youpin.gwdash.dao.mapper.ApiInfoMapper;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfo;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfoExample;
import com.xiaomi.youpin.gwdash.service.impl.TenantComponent;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ApiInfoDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiInfoDao.class);

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private Dao dao;

    @Autowired
    private TenantComponent tenementComponent;

    public int newApiInfo(ApiInfo info) {
        if (null == info) {
            LOGGER.error("[ApiInfoDao.newApiInfo] null ApiInfo");
            return 0;
        }

        long now = System.currentTimeMillis();
        info.setTenement(tenementComponent.getTenement());
        info.setCtime(now);
        info.setUtime(now);

        try {
            int affected = apiInfoMapper.insert(info);
            if (affected < 1) {
                LOGGER.warn("[ApiInfoDao.newApiInfo] failed to insert ApiGroupInfo: {}", info);
                return 0;
            }
        } catch (Exception e) {
            LOGGER.error("[ApiInfoDao.newApiInfo] failed to insert ApiGroupInfo: {}, err: {}", info, e);
            return 0;
        }
        return 1;
    }

    /**
     * 不 catch  用来transition 回滚的
     *
     * @param info
     * @return
     */
    public int newApiInfoV2(ApiInfo info) {
        if (null == info) {
            LOGGER.error("[ApiInfoDao.newApiInfo] null ApiInfo");
            return 0;
        }

        long now = System.currentTimeMillis();

        info.setCtime(now);
        info.setUtime(now);
        info.setTenement(this.tenementComponent.getTenement());

        int affected = apiInfoMapper.insert(info);
        if (affected < 1) {
            LOGGER.warn("[ApiInfoDao.newApiInfo] failed to insert ApiGroupInfo: {}", info);
            return 0;
        }

        return 1;
    }

    public List<ApiInfo> getApiInfoDetailByUrl(String url) {
        return getApiInfoDetailByUrl(url,this.tenementComponent.getTenement());
    }

    public List<ApiInfo> getApiInfoDetailByUrl(String url, String tenant) {
        ApiInfoExample example = new ApiInfoExample();
        ApiInfoExample.Criteria ca = example.createCriteria().andUrlEqualTo(url).andTenementEqualTo(tenant);
        List<ApiInfo> rawList = apiInfoMapper.selectByExample(example);
        LOGGER.warn("[ApiInfoDao.getApiInfoDetailByUrl] rawList: {}", rawList);
        return rawList;
    }

    public List<ApiInfo> getApiInfoById(long id) {
        ApiInfoExample example = new ApiInfoExample();
        ApiInfoExample.Criteria ca = example.createCriteria().andIdEqualTo(id).andTenementEqualTo(this.tenementComponent.getTenement());
        List<ApiInfo> rawList = apiInfoMapper.selectByExampleWithBLOBs(example);
        return rawList;
    }

    public List<ApiInfo> getMyCollections(ListParam param, int offset, int pageSize, String curUser) {
        LOGGER.info("getMyCollections,offset:{},pageSize:{}", offset, pageSize);
        List<UserCollectionInfo> myCollections = dao.query(UserCollectionInfo.class,
                Cnd.where("username", "=", curUser)
                        .and("status", "=", 1)
                        .desc("id"), dao.createPager(param.getPageNo(), param.getPageSize()));
        if (CollectionUtils.isEmpty(myCollections)) {
            return new ArrayList<>();
        }
        return dao.query(ApiInfo.class, Cnd.where("id", "in", myCollections.stream().map(UserCollectionInfo::getApiInfoId).collect(Collectors.toList())));
    }

    public Boolean hasCollected(String username, long apiInfoId) {
        List<UserCollectionInfo> myCollections = dao.query(UserCollectionInfo.class, Cnd.where("username", "=", username).and("status", "=", 1).and("apiInfoId", "=", apiInfoId));
        return myCollections.size() > 0;
    }

    public List<ApiInfo> getApiList(ListParam param, int offset, int pageSize, String curUserName) {
        int groupType = param.getGroupType();
        if (groupType == Consts.GROUP_TYPE_QUERY_MyCOLLECTION) {
            return getMyCollections(param, offset, pageSize, curUserName);
        }
        Cnd condition = buildApiInfoCondition(param, curUserName, groupType);
        condition.and("tenement", "=", tenementComponent.getTenement());
        condition.orderBy("ctime", "desc");
        return dao.query(ApiInfo.class, condition, dao.createPager(param.getPageNo(), param.getPageSize()));
    }

    public Cnd buildApiInfoCondition(ListParam param, String curUserName, int groupType) {
        Cnd cnd = Cnd.where("status", "=", Consts.STATUS_VALID);
        if (groupType == Consts.GROUP_TYPE_QUERY_CREATEDBYME) {
            cnd.and("creator", "=", curUserName);
        } else if (groupType == Consts.GROUP_TYPE_QUERY_UPDATEDBYME) {
            cnd.and("updater", "=", curUserName);
        }
        if (StringUtils.isNotEmpty(param.getHttpMethod())) {
            cnd.and("http_method", "=", param.getHttpMethod());
        }
        if (param.getGroupId() != null) {
            cnd.and("group_id", "=", param.getGroupId());
        }
        if (param.getRouteType() != null) {
            cnd.and("route_type", "=", param.getRouteType());
        }
        SqlExpressionGroup group = null;
        if (StringUtils.isNotEmpty(param.getName())) {
            group = Cnd.exps("name", "like", "%" + param.getName() + "%");
        }
        if (StringUtils.isNotEmpty(param.getServiceName())) {
            if (group == null) {
                group = Cnd.exps("service_name", "like", "%" + param.getServiceName() + "%");
            } else {
                group.or("service_name", "like", "%" + param.getServiceName() + "%");
            }
        }

        if (StringUtils.isNotEmpty(param.getUrlString())) {
            if (group == null) {
                group = Cnd.exps("url", "like", "%" + param.getUrlString() + "%");
            } else {
                group.or("url", "like", "%" + param.getUrlString() + "%");
            }
        }

        if (StringUtils.isNotEmpty(param.getPathString())) {
            if (group == null) {
                group = Cnd.exps("path", "like", "%" + param.getPathString() + "%");
            } else {
                group.or("path", "like", "%" + param.getPathString() + "%");
            }
        }
        cnd.and("tenement", "=", tenementComponent.getTenement());
        cnd.and(group);
        return cnd;
    }

    public int getApiTotalCount(ListParam param, String curUserName) {
        int totalCount = 0;
        if (param.getGroupType() == Consts.GROUP_TYPE_QUERY_MyCOLLECTION) {
            List<UserCollectionInfo> myCollections = dao.query(UserCollectionInfo.class,
                    Cnd.where("username", "=", curUserName)
                            .and("tenement", "=", this.tenementComponent)
                            .and("status", "=", 1)
            );
            if (CollectionUtils.isEmpty(myCollections)) {
                return totalCount;
            }
            return dao.count(ApiInfo.class, Cnd.where("id", "in", myCollections.stream().map(UserCollectionInfo::getApiInfoId).collect(Collectors.toList())));
        }
        Cnd cnd = buildApiInfoCondition(param, curUserName, param.getGroupType());
        return dao.count(ApiInfo.class, cnd);
    }

    public int delApiInfo(List<Long> ids) {
        ApiInfoExample example = new ApiInfoExample();
        ApiInfoExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(ids);

        List<ApiInfo> rawList = apiInfoMapper.selectByExample(example);

        if (rawList == null) {
            return 0;
        }

        int deleted = rawList.stream().mapToInt(it -> {
            ApiInfo info = new ApiInfo();
            info.setId(it.getId());
            info.setUrl(UUID.randomUUID().toString() + it.getUrl());
            info.setStatus(Consts.STATUS_DELETED);
            int affected = apiInfoMapper.updateByPrimaryKeySelective(info);
            return affected;
        }).sum();

        if (deleted < ids.size()) {
            LOGGER.warn("[ApiInfoDao.delApiInfo] failed to delete all api group info, ids: {}", ids);
        }
        return deleted;
    }

    public int updateApiInfoById(ApiInfo info) {
        info.setUtime(System.currentTimeMillis());
        return apiInfoMapper.updateByPrimaryKeySelective(info);
    }

    /**
     * 1. equal
     * 2. cover
     * 3. condition equal
     *
     * @param url
     * @return
     */
    public boolean existUrl(String url, Long id) {
        url = url.trim();
        ApiInfoExample example = new ApiInfoExample();
        ApiInfoExample.Criteria baseCriteria = example.createCriteria();
        baseCriteria.andUrlEqualTo(url).andTenementEqualTo(this.tenementComponent.getTenement());
        if (id != null && id > 0) {
            baseCriteria.andIdNotEqualTo(id);
        }
        int count = (int) apiInfoMapper.countByExample(example);

        return count > 0;
    }

    public ApiInfo getOneById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return apiInfoMapper.selectByPrimaryKey(id);
    }

}
