package com.xiaomi.mone.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.app.api.model.HeraAppBaseQuery;
import com.xiaomi.mone.app.api.service.HeraAppService;
import com.xiaomi.mone.app.dao.HeraAppBaseInfoMapper;
import com.xiaomi.mone.app.dao.HeraAppExcessInfoMapper;
import com.xiaomi.mone.app.enums.OperateEnum;
import com.xiaomi.mone.app.enums.StatusEnum;
import com.xiaomi.mone.app.exception.AppException;
import com.xiaomi.mone.app.model.HeraAppBaseInfo;
import com.xiaomi.mone.app.model.HeraAppBaseInfoExample;
import com.xiaomi.mone.app.model.HeraAppExcessInfo;
import com.xiaomi.mone.app.model.dto.MisAppInfoDTO;
import com.xiaomi.mone.app.model.vo.HeraAppEnvVo;
import com.xiaomi.mone.app.service.HeraAppBaseInfoService;
import com.xiaomi.mone.app.service.HeraAppEnvService;
import com.xiaomi.mone.app.service.env.DefaultEnvIpFetch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xiaomi.mone.app.common.Constant.GSON;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 12:15
 */
@Service
@Slf4j
public class HeraAppBaseInfoServiceImpl implements HeraAppBaseInfoService {

    private final HeraAppBaseInfoMapper heraAppBaseInfoMapper;
    private final HeraAppExcessInfoMapper heraAppExcessInfoMapper;
    private final HeraAppService heraAppService;
    private final HeraAppEnvService heraAppEnvService;

    private final DefaultEnvIpFetch defaultEnvIpFetch;


    public HeraAppBaseInfoServiceImpl(HeraAppBaseInfoMapper heraAppBaseInfoMapper, HeraAppExcessInfoMapper milogAppTopicRel, HeraAppService heraAppService, DefaultEnvIpFetch defaultEnvIpFetch, HeraAppEnvService heraAppEnvService) {
        this.heraAppBaseInfoMapper = heraAppBaseInfoMapper;
        this.heraAppExcessInfoMapper = milogAppTopicRel;
        this.heraAppService = heraAppService;
        this.defaultEnvIpFetch = defaultEnvIpFetch;
        this.heraAppEnvService = heraAppEnvService;
    }

    @Override
    public HeraAppBaseInfo queryById(Long id) {
        return heraAppBaseInfoMapper.selectById(id);
    }

    @Override
    public Boolean synchronousMisApp(List<MisAppInfoDTO> data) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        log.info("初始化mis项目开始,共有{}个", data.size());
        AtomicInteger count = new AtomicInteger();
        for (MisAppInfoDTO misAppInfoDTO : data) {
            int increment = count.getAndIncrement();
            log.debug("mis应用同步,总有{}个应用，开始执行第{}个，还剩下{}个", data.size(), increment, data.size() - increment);
            HeraAppBaseInfo heraAppBaseInfo = geAppBasesInfo(misAppInfoDTO);
            if (null == heraAppBaseInfo.getIamTreeId()) {
                log.info("IAM treeIds is null,app name:{},cName:{}", heraAppBaseInfo.getAppName(), heraAppBaseInfo.getAppCname());
                continue;
            }
            HeraAppBaseInfo existAppBaseInfo = queryAppBaseInfo(heraAppBaseInfo);
            if (null == existAppBaseInfo) {
                heraAppBaseInfoMapper.insert(heraAppBaseInfo);
                HeraAppExcessInfo appExcessInfo = getAppExcessInfo(heraAppBaseInfo.getId(), misAppInfoDTO);
                heraAppExcessInfoMapper.insert(appExcessInfo);
            } else {
                HeraAppExcessInfo heraAppExcessInfo = queryAppExcessInfo(existAppBaseInfo.getId());
                if (null == heraAppExcessInfo) {
                    HeraAppExcessInfo appExcessInfo = getAppExcessInfo(existAppBaseInfo.getId(), misAppInfoDTO);
                    heraAppExcessInfoMapper.insert(appExcessInfo);
                } else {
                    //比较IP是否变化--更新
                    heraAppExcessInfo.setTreeIds(misAppInfoDTO.getTree_id());
                    heraAppExcessInfo.setNodeIPs(misAppInfoDTO.getCluster_info());
                    heraAppExcessInfoMapper.updateById(heraAppExcessInfo);
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stopwatch.stop();
        log.info("初始化mis项目结束，花费时间：{} s", stopwatch.elapsed(TimeUnit.SECONDS));
        return true;
    }

    private HeraAppBaseInfo queryAppBaseInfo(HeraAppBaseInfo heraAppBaseInfo) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("bind_id", heraAppBaseInfo.getBindId());
        queryWrapper.eq("app_name", heraAppBaseInfo.getAppName());
        queryWrapper.eq("iam_tree_id", heraAppBaseInfo.getIamTreeId());
        return heraAppBaseInfoMapper.selectOne(queryWrapper);
    }

    private HeraAppExcessInfo queryAppExcessInfo(Integer appBaseId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("app_base_id", appBaseId);
        return heraAppExcessInfoMapper.selectOne(queryWrapper);
    }

    private HeraAppBaseInfo geAppBasesInfo(MisAppInfoDTO misAppInfoDTO) {
        HeraAppBaseInfo milogAppTopicRel = new HeraAppBaseInfo();
        milogAppTopicRel.setBindId(misAppInfoDTO.getService_id().toString());
        milogAppTopicRel.setBindType(0);
        milogAppTopicRel.setAppName(misAppInfoDTO.getService());
        milogAppTopicRel.setAppCname(misAppInfoDTO.getService());
        milogAppTopicRel.setAppCname(misAppInfoDTO.getService_cn());
        milogAppTopicRel.setAppType(0);
        milogAppTopicRel.setStatus(0);
        milogAppTopicRel.setCreateTime(new Date());
        milogAppTopicRel.setUpdateTime(new Date());
        return milogAppTopicRel;
    }

    private HeraAppExcessInfo getAppExcessInfo(Integer appBaseId, MisAppInfoDTO misAppInfoDTO) {
        HeraAppExcessInfo milogAppTopicRel = new HeraAppExcessInfo();
        milogAppTopicRel.setAppBaseId(appBaseId);
        milogAppTopicRel.setTreeIds(misAppInfoDTO.getTree_id());
        milogAppTopicRel.setNodeIPs(misAppInfoDTO.getCluster_info());
        milogAppTopicRel.setManagers(misAppInfoDTO.getManagers());
        milogAppTopicRel.setCreateTime(new Date());
        milogAppTopicRel.setUpdateTime(new Date());
        return milogAppTopicRel;
    }


    @Override
    public Long countByParticipant(HeraAppBaseQuery query) {

        try {
            if (StringUtils.isBlank(query.getMyParticipant())) {
                query.setMyParticipant(null);
            }

            if (StringUtils.isBlank(query.getAppName())) {
                query.setAppName(null);
            }
            Long aLong = heraAppBaseInfoMapper.countByParticipant(query);
            log.info("countByParticipant query:{},result:{}", query.toString(), aLong);
            return aLong;
        } catch (Exception e) {
            log.error("countByParticipant error!" + e.getMessage(), e);
            return null;
        }

    }

    @Override
    public List<HeraAppBaseInfoParticipant> queryByParticipant(HeraAppBaseQuery query) {

        if (StringUtils.isBlank(query.getMyParticipant())) {
            query.setMyParticipant(null);
        }

        if (StringUtils.isBlank(query.getAppName())) {
            query.setAppName(null);
        }

        query.initPageParam();
        try {
            List<HeraAppBaseInfoParticipant> heraAppBaseInfoParticipants = heraAppBaseInfoMapper.selectByParticipant(query);
            log.info("queryByParticipant query:{},result:{}", new Gson().toJson(query), new Gson().toJson(heraAppBaseInfoParticipants));
            return heraAppBaseInfoParticipants;
        } catch (Exception e) {
            log.error("queryByParticipant error!" + e.getMessage(), e);
            return null;
        }

    }


    @Override
    public Long count(HeraAppBaseInfoModel baseInfo) {


        HeraAppBaseInfoExample example = new HeraAppBaseInfoExample();

        //默认查询未删除的数据
        HeraAppBaseInfoExample.Criteria ca = example.createCriteria().andStatusEqualTo(0);
        if (baseInfo.getStatus() != null) {
            ca = example.createCriteria().andStatusEqualTo(baseInfo.getStatus());
        }

        if (baseInfo.getBindId() != null) {
            ca.andBindIdEqualTo(baseInfo.getBindId());
        }

        if (baseInfo.getBindType() != null) {
            ca.andBindTypeEqualTo(baseInfo.getBindType());
        }

        if (StringUtils.isNotBlank(baseInfo.getAppName())) {
            ca.andAppNameLike("%" + baseInfo.getAppName() + "%");
        }

        if (StringUtils.isNotBlank(baseInfo.getAppCname())) {
            ca.andAppCnameLike("%" + baseInfo.getAppCname() + "%");
        }

        if (baseInfo.getAppType() != null) {
            ca.andAppTypeEqualTo(baseInfo.getAppType());
        }

        if (StringUtils.isNotBlank(baseInfo.getAppLanguage())) {
            ca.andAppLanguageLike("%" + baseInfo.getAppLanguage() + "%");
        }

        if (baseInfo.getPlatformType() != null) {
            ca.andPlatformTypeEqualTo(baseInfo.getPlatformType());
        }

        if (StringUtils.isNotBlank(baseInfo.getAppSignId())) {
            ca.andAppSignIdLike("%" + baseInfo.getAppSignId() + "%");
        }

        if (baseInfo.getIamTreeId() != null) {
            ca.andIamTreeIdEqualTo(baseInfo.getIamTreeId());
        }

        example.setOrderByClause("id desc");

        try {
            return heraAppBaseInfoMapper.countByExample(example);
        } catch (Exception e) {
            log.error("HeraAppBaseInfoServiceImpl#count error!" + e.getMessage(), e);
            return null;
        }

    }

    @Override
    public List<HeraAppBaseInfo> query(HeraAppBaseInfoModel baseInfo, Integer pageCount, Integer pageNum) {

        if (pageCount == null || pageCount.intValue() <= 0) {
            pageCount = 1;
        }
        if (pageNum == null || pageNum.intValue() <= 0) {
            pageNum = 10;
        }

        HeraAppBaseInfoExample example = new HeraAppBaseInfoExample();

        //默认查询未删除的数据
        HeraAppBaseInfoExample.Criteria ca = example.createCriteria().andStatusEqualTo(0);
        if (baseInfo.getStatus() != null) {
            ca.andStatusEqualTo(baseInfo.getStatus());
        }

        if (baseInfo.getBindId() != null) {
            ca.andBindIdEqualTo(baseInfo.getBindId());
        }

        if (baseInfo.getBindType() != null) {
            ca.andBindTypeEqualTo(baseInfo.getBindType());
        }

        if (StringUtils.isNotBlank(baseInfo.getAppName())) {
            ca.andAppNameLike("%" + baseInfo.getAppName() + "%");
        }

        if (StringUtils.isNotBlank(baseInfo.getAppCname())) {
            ca.andAppCnameLike("%" + baseInfo.getAppCname() + "%");
        }

        if (baseInfo.getAppType() != null) {
            ca.andAppTypeEqualTo(baseInfo.getAppType());
        }

        if (StringUtils.isNotBlank(baseInfo.getAppLanguage())) {
            ca.andAppLanguageLike("%" + baseInfo.getAppLanguage() + "%");
        }

        if (baseInfo.getPlatformType() != null) {
            ca.andPlatformTypeEqualTo(baseInfo.getPlatformType());
        }

        if (StringUtils.isNotBlank(baseInfo.getAppSignId())) {
            ca.andAppSignIdLike("%" + baseInfo.getAppSignId() + "%");
        }

        if (baseInfo.getIamTreeId() != null) {
            ca.andIamTreeIdEqualTo(baseInfo.getIamTreeId());
        }

        example.setOffset((pageCount - 1) * pageNum);
        example.setLimit(pageNum);
        example.setOrderByClause("id desc");

        try {
            return heraAppBaseInfoMapper.selectByExampleWithBLOBs(example);
        } catch (Exception e) {
            log.error("HeraAppBaseInfoServiceImpl#query error!" + e.getMessage(), e);
            return null;
        }

    }


    @Override
    public HeraAppBaseInfo getById(Integer id) {

        if (id == null) {
            log.info("HeraAppBaseInfoServiceImpl.getById id is null!");
            return null;
        }

        return heraAppBaseInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public int delById(Integer id) {

        if (id == null) {
            log.info("HeraAppBaseInfoServiceImpl.delById id is null!");
            return 0;
        }

        return heraAppBaseInfoMapper.deleteByPrimaryKey(id);
    }


    public int create(HeraAppBaseInfo heraAppBaseInfo) {
        if (null == heraAppBaseInfo) {
            log.error("[HeraAppBaseInfoServiceImpl.create] null heraAppBaseInfo");
            return 0;
        }

        heraAppBaseInfo.setCreateTime(new Date());
        heraAppBaseInfo.setUpdateTime(new Date());
        heraAppBaseInfo.setStatus(0);

        try {
            int affected = heraAppBaseInfoMapper.insert(heraAppBaseInfo);
            if (affected < 1) {
                log.warn("[HeraAppBaseInfoServiceImpl.create] failed to insert heraAppBaseInfo: {}", heraAppBaseInfo.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[HeraAppBaseInfoServiceImpl.create] failed to insert heraAppBaseInfo: {}, err: {}", heraAppBaseInfo.toString(), e);
            return 0;
        }
        return 1;
    }

    public int update(HeraAppBaseInfo heraAppBaseInfo) {
        if (null == heraAppBaseInfo) {
            log.error("[HeraAppBaseInfoServiceImpl.update] null heraAppBaseInfo");
            return 0;
        }
        heraAppBaseInfo.setUpdateTime(new Date());

        try {
            int affected = heraAppBaseInfoMapper.updateById(heraAppBaseInfo);
            if (affected < 1) {
                log.warn("[HeraAppBaseInfoServiceImpl.update] failed to update heraAppBaseInfo: {}", heraAppBaseInfo.toString());
                return 0;
            }
        } catch (Exception e) {
            log.error("[HeraAppBaseInfoServiceImpl.update] failed to update heraAppBaseInfo: {}, err: {}", heraAppBaseInfo.toString(), e);
            return 0;
        }
        return 1;
    }

    @Override
    public void deleAppByBindIdAndPlat(String bindId, Integer plat) {

        if (StringUtils.isBlank(bindId) || plat == null) {
            log.error("invalid param,bindId:{},plat:{}", bindId, plat);
            return;
        }

        HeraAppBaseInfoModel query = new HeraAppBaseInfoModel();
        query.setBindId(bindId);
        query.setPlatformType(plat);
        List<HeraAppBaseInfo> list = this.query(query, null, null);

        if (CollectionUtils.isEmpty(list)) {
            log.info("deleAppByBindIdAndPlat no data found! bindId:{},plat:{}", bindId, plat);
        }

        for (HeraAppBaseInfo baseInfo : list) {
            Integer integer = heraAppService.delById(baseInfo.getId());
            if (integer.intValue() > 0) {
                log.info("deleAppByBindIdAndPlat success!baseInfo:{}", new Gson().toJson(baseInfo));
            } else {
                log.error("deleAppByBindIdAndPlat success!baseInfo:{}", new Gson().toJson(baseInfo));
            }
        }
    }

    /**
     * app基本信息操作完后会发送mq消息
     *
     * @param heraAppBaseInfo
     * @param operateEnum
     * @return
     */
    @Override
    public HeraAppBaseInfo appBaseInfoOperate(HeraAppBaseInfo heraAppBaseInfo, OperateEnum operateEnum) {
        try {
            if (OperateEnum.ADD_OPERATE == operateEnum) {
                return addHeraAppBaseInfo(heraAppBaseInfo);
            }
            if (OperateEnum.UPDATE_OPERATE == operateEnum) {
                return updateHeraAppBaseInfo(heraAppBaseInfo);
            }
            if (OperateEnum.DELETE_OPERATE == operateEnum) {
                deleteHeraAppBaseInfo(heraAppBaseInfo);
            }
        } catch (Exception e) {
            log.info("app baseInfo operate error,operateEnum:{},baseInfo:{}", operateEnum.getDescribe(), GSON.toJson(heraAppBaseInfo), e);
            throw new AppException("app baseInfo operate error", e);
        }
        return heraAppBaseInfo;
    }

    private void deleteHeraAppBaseInfo(HeraAppBaseInfo heraAppBaseInfo) {
        heraAppBaseInfo.setUpdateTime(new Date());
        heraAppBaseInfo.setStatus(StatusEnum.DELETED.getCode());
        heraAppBaseInfoMapper.updateById(heraAppBaseInfo);
    }

    private HeraAppBaseInfo updateHeraAppBaseInfo(HeraAppBaseInfo heraAppBaseInfo) {
        HeraAppBaseInfo beforeAppInfo = heraAppBaseInfoMapper.selectById(heraAppBaseInfo.getId());
        heraAppBaseInfo.setUpdateTime(new Date());
        heraAppBaseInfo.setStatus(StatusEnum.NOT_DELETED.getCode());
        heraAppBaseInfoMapper.updateById(heraAppBaseInfo);
        return beforeAppInfo;
    }

    private HeraAppBaseInfo addHeraAppBaseInfo(HeraAppBaseInfo heraAppBaseInfo) {
        heraAppBaseInfo.setCreateTime(new Date());
        heraAppBaseInfo.setUpdateTime(new Date());
        heraAppBaseInfo.setStatus(StatusEnum.NOT_DELETED.getCode());
        heraAppBaseInfoMapper.insert(heraAppBaseInfo);
        //启动一个任务去拉去一次ip配置,初始化一次 pre
        asyncFetchEnvIp(heraAppBaseInfo.getId(), heraAppBaseInfo.getBindId(), heraAppBaseInfo.getAppName());
        return heraAppBaseInfoMapper.selectById(heraAppBaseInfo.getId());
    }

    private void asyncFetchEnvIp(Integer id, String bindId, String appName) {
        CompletableFuture.runAsync(() -> {
            try {
                HeraAppEnvVo appEnvVo = defaultEnvIpFetch.getEnvIpFetch().fetch(id.longValue(), Long.valueOf(bindId), appName);
                heraAppEnvService.addAppEnvNotExist(appEnvVo);
            } catch (Exception e) {
                log.error(String.format("fetch app ip info error,id:%d,bindId:%s,appName:%s", id, bindId, appName), e);
            }
        });
    }

}
