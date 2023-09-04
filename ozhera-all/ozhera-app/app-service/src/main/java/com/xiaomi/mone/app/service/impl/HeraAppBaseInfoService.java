package com.xiaomi.mone.app.service.impl;

import com.google.gson.Gson;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.HeraAppBaseInfoParticipant;
import com.xiaomi.mone.app.api.model.HeraAppBaseQuery;
import com.xiaomi.mone.app.dao.HeraBaseInfoDao;
import com.xiaomi.mone.app.model.HeraAppBaseInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2023/4/26 2:19 下午
 */
@Service
@Slf4j
public class HeraAppBaseInfoService {

    @Autowired
    HeraBaseInfoDao heraBaseInfoDao;

    public HeraAppBaseInfo queryById(Long id) {
        return heraBaseInfoDao.getById(id.intValue());
    }

    public Long count(HeraAppBaseInfoModel baseInfo) {
        return heraBaseInfoDao.count(baseInfo);
    }

    public List<HeraAppBaseInfo> query(HeraAppBaseInfoModel baseInfo, Integer pageCount, Integer pageNum) {
        return heraBaseInfoDao.query(baseInfo,pageCount,pageNum);
    }

    public HeraAppBaseInfo getById(Integer id) {
        return heraBaseInfoDao.getById(id);
    }

    public int delById(Integer id) {
        return heraBaseInfoDao.delById(id);
    }

    public int create(HeraAppBaseInfo heraAppBaseInfo) {
        return heraBaseInfoDao.create(heraAppBaseInfo);
    }

    public int update(HeraAppBaseInfo heraAppBaseInfo) {
        return heraBaseInfoDao.update(heraAppBaseInfo);
    }

    public void deleAppByBindIdAndPlat(String bindId, Integer plat) {

        if (StringUtils.isBlank(bindId) || plat == null) {
            log.error("invalid param,bindId:{},plat:{}", bindId, plat);
            return;
        }

        HeraAppBaseInfoModel query = new HeraAppBaseInfoModel();
        query.setBindId(bindId);
        query.setPlatformType(plat);
        List<HeraAppBaseInfo> list = heraBaseInfoDao.query(query, null, null);

        if (CollectionUtils.isEmpty(list)) {
            log.info("deleAppByBindIdAndPlat no data found! bindId:{},plat:{}", bindId, plat);
        }

        for (HeraAppBaseInfo baseInfo : list) {
            Integer integer = heraBaseInfoDao.delById(baseInfo.getId());
            if (integer.intValue() > 0) {
                log.info("deleAppByBindIdAndPlat success!baseInfo:{}", new Gson().toJson(baseInfo));
            } else {
                log.error("deleAppByBindIdAndPlat success!baseInfo:{}", new Gson().toJson(baseInfo));
            }
        }
    }

    public Long countByParticipant(HeraAppBaseQuery query) {

        if (!"yes".equals(query.getMyParticipant())) {
            query.setMyParticipant(null);
        }

        if (StringUtils.isBlank(query.getAppName())) {
            query.setAppName(null);
        }
        Long aLong = heraBaseInfoDao.countByParticipant(query);

        log.info("countByParticipant query:{},result:{}", query.toString(), aLong);
        return aLong;

    }

    public List<HeraAppBaseInfoParticipant> queryByParticipant(HeraAppBaseQuery query) {

        if (!"yes".equals(query.getMyParticipant())) {
            query.setMyParticipant(null);
        }

        if (StringUtils.isBlank(query.getAppName())) {
            query.setAppName(null);
        }

        List<HeraAppBaseInfoParticipant> heraAppBaseInfoParticipants = heraBaseInfoDao.queryByParticipant(query);
        log.info("queryByParticipant query:{},result:{}", new Gson().toJson(query), new Gson().toJson(heraAppBaseInfoParticipants));
        return heraAppBaseInfoParticipants;
    }

}
