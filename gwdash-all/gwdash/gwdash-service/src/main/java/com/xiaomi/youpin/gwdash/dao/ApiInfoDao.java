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
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Repository
public class ApiInfoDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiInfoDao.class);

    @Autowired
    private ApiInfoMapper apiInfoMapper;

    @Autowired
    private Dao dao;

    public int newApiInfo(ApiInfo info) {
        if (null == info) {
            LOGGER.error("[ApiInfoDao.newApiInfo] null ApiInfo");
            return 0;
        }

        long now = System.currentTimeMillis();

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

        int affected = apiInfoMapper.insert(info);
        if (affected < 1) {
            LOGGER.warn("[ApiInfoDao.newApiInfo] failed to insert ApiGroupInfo: {}", info);
            return 0;
        }

        return 1;
    }

    public List<ApiInfo> getApiInfoDetailByUrl(String url) {
        ApiInfoExample example = new ApiInfoExample();
        ApiInfoExample.Criteria ca = example.createCriteria().andUrlEqualTo(url);

        List<ApiInfo> rawList = apiInfoMapper.selectByExample(example);
        LOGGER.warn("[ApiInfoDao.getApiInfoDetailByUrl] rawList: {}", rawList);

        return rawList;

    }

    public List<ApiInfo> getApiInfoById(long id) {
        ApiInfoExample example = new ApiInfoExample();
        ApiInfoExample.Criteria ca = example.createCriteria().andIdEqualTo(id);

        List<ApiInfo> rawList = apiInfoMapper.selectByExampleWithBLOBs(example);
//        LOGGER.warn("[ApiInfoDao.getApiInfoDetailByUrl]  rawList.get(0).getParamTemplate: {}", rawList.get(0).getParamTemplate());

        return rawList;
    }

    public List<ApiInfo> getMyCollections(ListParam param, int offset, int pageSize, List<Integer> gids, int role, String curUser) {
        LOGGER.info("getMyCollections,offset:{},pageSize:{}", offset, pageSize);
        List<UserCollectionInfo> myCollections = dao.query(UserCollectionInfo.class, Cnd.where("username", "=", curUser).and("status", "=", 1).desc("id"), dao.createPager(param.getPageNo(), param.getPageSize()));
        Iterator<UserCollectionInfo> it = myCollections.iterator();
        List<ApiInfo> list = new ArrayList<>();
        LOGGER.info(" getMyCollections list:{} ", list.size());
        while (it.hasNext()) {
            List<ApiInfo> apiInfos = getApiInfoById(it.next().getApiInfoId());
            if (apiInfos.size() > 0) {
                list.add(apiInfos.get(0));
            }
        }
        return list;
    }

    public Boolean hasCollected(String username, long apiInfoId) {
        List<UserCollectionInfo> myCollections = dao.query(UserCollectionInfo.class, Cnd.where("username", "=", username).and("status", "=", 1).and("apiInfoId", "=", apiInfoId));
        return myCollections.size() > 0;
    }

    public List<ApiInfo> getApiList(ListParam param, int offset, int pageSize, List<Integer> gids, int role, String curUSer) {
        ApiInfoExample example = new ApiInfoExample();
        int groupType = param.getGroupType();
        if (groupType == Consts.GROUP_TYPE_QUERY_MyCOLLECTION) {
            return getMyCollections(param, offset, pageSize, gids, role, curUSer);
        }
        ;

        //for query begin
        if (StringUtils.isNotEmpty(param.getName())) {
            ApiInfoExample.Criteria ca = example.createCriteria().andNameLike("%" + param.getName() + "%");
            ca.andStatusEqualTo(Consts.STATUS_VALID);
            if (role != Consts.ROLE_ADMIN) {
                ca.andGroupIdIn(gids);
            }
            if (groupType == Consts.GROUP_TYPE_QUERY_CREATEDBYME) {
                ca.andCreatorEqualTo(curUSer);
            } else if (groupType == Consts.GROUP_TYPE_QUERY_UPDATEDBYME) {
                ca.andUpdaterEqualTo(curUSer);
            }
            example.or(ca);
        }

        if (StringUtils.isNotEmpty(param.getServiceName())) {
            ApiInfoExample.Criteria ca = example.createCriteria().andServiceNameLike("%" + param.getServiceName() + "%");
            ca.andStatusEqualTo(Consts.STATUS_VALID);
            if (role != Consts.ROLE_ADMIN) {
                ca.andGroupIdIn(gids);
            }
            if (groupType == Consts.GROUP_TYPE_QUERY_CREATEDBYME) {
                ca.andCreatorEqualTo(curUSer);
            } else if (groupType == Consts.GROUP_TYPE_QUERY_UPDATEDBYME) {
                ca.andUpdaterEqualTo(curUSer);
            }
            example.or(ca);
        }

        if (StringUtils.isNotEmpty(param.getUrlString())) {
            ApiInfoExample.Criteria ca = example.createCriteria().andUrlLike("%" + param.getUrlString() + "%");
            ca.andStatusEqualTo(Consts.STATUS_VALID);
            if (role != Consts.ROLE_ADMIN) {
                ca.andGroupIdIn(gids);

            }
            if (groupType == Consts.GROUP_TYPE_QUERY_CREATEDBYME) {
                ca.andCreatorEqualTo(curUSer);
            } else if (groupType == Consts.GROUP_TYPE_QUERY_UPDATEDBYME) {
                ca.andUpdaterEqualTo(curUSer);
            }
            example.or(ca);
        }

        if (StringUtils.isNotEmpty(param.getPathString())) {
            ApiInfoExample.Criteria ca = example.createCriteria().andPathLike("%" + param.getPathString() + "%");
            ca.andStatusEqualTo(Consts.STATUS_VALID);
            if (role != Consts.ROLE_ADMIN) {
                ca.andGroupIdIn(gids);

            }
            if (groupType == Consts.GROUP_TYPE_QUERY_CREATEDBYME) {
                ca.andCreatorEqualTo(curUSer);
            } else if (groupType == Consts.GROUP_TYPE_QUERY_UPDATEDBYME) {
                ca.andUpdaterEqualTo(curUSer);
            }
            example.or(ca);
        }

        //前端提供name的同时,其他几项都会提供
        if (StringUtils.isEmpty(param.getName())) {
            ApiInfoExample.Criteria ca = example.createCriteria();
            ca.andStatusEqualTo(Consts.STATUS_VALID);
            if (role != Consts.ROLE_ADMIN) {
                ca.andGroupIdIn(gids);

            }
            if (groupType == Consts.GROUP_TYPE_QUERY_CREATEDBYME) {
                ca.andCreatorEqualTo(curUSer);
            } else if (groupType == Consts.GROUP_TYPE_QUERY_UPDATEDBYME) {
                ca.andUpdaterEqualTo(curUSer);
            }
            example.or(ca);
        }


        //for query end

        example.setOffset(offset);
        example.setLimit(pageSize);
        example.setOrderByClause("id desc");

        List<ApiInfo> list = apiInfoMapper.selectByExampleWithBLOBs(example);
        return list;
    }

    public int getApiQty(ListParam param, List<Integer> gids, int role, String curUser) {
        if (param.getGroupType() == Consts.GROUP_TYPE_QUERY_MyCOLLECTION) {
            List<UserCollectionInfo> myCollections = dao.query(UserCollectionInfo.class, Cnd.where("username", "=", curUser).and("status", "=", 1));
            Iterator<UserCollectionInfo> it = myCollections.iterator();
            List<ApiInfo> list = new ArrayList<>();
            LOGGER.info(" getApiQty myCollections:{} ", myCollections.size());
            while (it.hasNext()) {
                List<ApiInfo> apiInfos = getApiInfoById(it.next().getApiInfoId());
                if (apiInfos.size() > 0) {
                    list.add(apiInfos.get(0));
                }
            }
            LOGGER.info(" getApiQty list:{} ", list.size());

            return list.size();
        }

        ApiInfoExample example = new ApiInfoExample();

        // for query begin
        if (StringUtils.isNotEmpty(param.getName())) {
            ApiInfoExample.Criteria ca = example.createCriteria().andNameLike("%" + param.getName() + "%");
            ca.andStatusEqualTo(Consts.STATUS_VALID);
            if (role != Consts.ROLE_ADMIN) {
                ca.andGroupIdIn(gids);
            }
            if (param.getGroupType() == Consts.GROUP_TYPE_QUERY_CREATEDBYME) {
                ca.andCreatorEqualTo(curUser);
            } else if (param.getGroupType() == Consts.GROUP_TYPE_QUERY_UPDATEDBYME) {
                ca.andUpdaterEqualTo(curUser);
            }
            example.or(ca);
        }

        if (StringUtils.isNotEmpty(param.getServiceName())) {
            ApiInfoExample.Criteria ca = example.createCriteria().andServiceNameLike("%" + param.getServiceName() + "%");
            ca.andStatusEqualTo(Consts.STATUS_VALID);
            if (role != Consts.ROLE_ADMIN) {
                ca.andGroupIdIn(gids);
            }
            if (param.getGroupType() == Consts.GROUP_TYPE_QUERY_CREATEDBYME) {
                ca.andCreatorEqualTo(curUser);
            } else if (param.getGroupType() == Consts.GROUP_TYPE_QUERY_UPDATEDBYME) {
                ca.andUpdaterEqualTo(curUser);
            }
            example.or(ca);
        }

        if (StringUtils.isNotEmpty(param.getUrlString())) {
            ApiInfoExample.Criteria ca = example.createCriteria().andUrlLike("%" + param.getUrlString() + "%");
            ca.andStatusEqualTo(Consts.STATUS_VALID);
            if (role != Consts.ROLE_ADMIN) {
                ca.andGroupIdIn(gids);
            }
            if (param.getGroupType() == Consts.GROUP_TYPE_QUERY_CREATEDBYME) {
                ca.andCreatorEqualTo(curUser);
            } else if (param.getGroupType() == Consts.GROUP_TYPE_QUERY_UPDATEDBYME) {
                ca.andUpdaterEqualTo(curUser);
            }
            example.or(ca);
        }

        if (StringUtils.isNotEmpty(param.getPathString())) {
            ApiInfoExample.Criteria ca = example.createCriteria().andPathLike("%" + param.getPathString() + "%");
            ca.andStatusEqualTo(Consts.STATUS_VALID);
            if (role != Consts.ROLE_ADMIN) {
                ca.andGroupIdIn(gids);
            }
            if (param.getGroupType() == Consts.GROUP_TYPE_QUERY_CREATEDBYME) {
                ca.andCreatorEqualTo(curUser);
            } else if (param.getGroupType() == Consts.GROUP_TYPE_QUERY_UPDATEDBYME) {
                ca.andUpdaterEqualTo(curUser);
            }
            example.or(ca);
        }


        //前端提供name的同时,其他几项都会提供
        if (StringUtils.isEmpty(param.getName())) {
            ApiInfoExample.Criteria ca = example.createCriteria();
            ca.andStatusEqualTo(Consts.STATUS_VALID);
            if (param.getGroupType() == Consts.GROUP_TYPE_QUERY_CREATEDBYME) {
                ca.andCreatorEqualTo(curUser);
            } else if (param.getGroupType() == Consts.GROUP_TYPE_QUERY_UPDATEDBYME) {
                ca.andUpdaterEqualTo(curUser);
            }
            if (role != Consts.ROLE_ADMIN) {
                ca.andGroupIdIn(gids);
            }
            example.or(ca);
        }


        //for query end


        return (int) apiInfoMapper.countByExample(example);
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
        baseCriteria.andUrlEqualTo(url);
        if (id != null && id > 0) {
            baseCriteria.andIdNotEqualTo(id);
        }
        int count = (int) apiInfoMapper.countByExample(example);

        return count > 0;
    }

}
