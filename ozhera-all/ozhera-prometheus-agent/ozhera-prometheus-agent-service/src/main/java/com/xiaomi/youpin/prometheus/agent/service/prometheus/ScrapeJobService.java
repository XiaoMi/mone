package com.xiaomi.youpin.prometheus.agent.service.prometheus;

import com.google.gson.Gson;
import com.xiaomi.youpin.prometheus.agent.Impl.ScrapeConfigDao;
import com.xiaomi.youpin.prometheus.agent.enums.ScrapeJobStatusEnum;
import com.xiaomi.youpin.prometheus.agent.param.prometheus.Scrape_configs;
import com.xiaomi.youpin.prometheus.agent.param.scrapeConfig.ScrapeConfigDetail;
import com.xiaomi.youpin.prometheus.agent.result.Result;
import com.xiaomi.youpin.prometheus.agent.entity.ScrapeConfigEntity;
import com.xiaomi.youpin.prometheus.agent.param.scrapeConfig.ScrapeConfigParam;
import com.xiaomi.youpin.prometheus.agent.vo.PageDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class ScrapeJobService {

    @Autowired
    ScrapeConfigDao dao;

    public static final Gson gson = new Gson();

    public Result CreateScrapeConfig(ScrapeConfigParam param) {
        log.info("ScrapeJobService.CreateScrapeConfig  param : {}", gson.toJson(param));
        param = beforeCreateJob(param);
        ScrapeConfigEntity scrapeConfigEntity = new ScrapeConfigEntity();
        scrapeConfigEntity.setEnv(param.getEnv());
        scrapeConfigEntity.setBody(gson.toJson(getScrapeJobBody(param)));
        scrapeConfigEntity.setCreatedBy("xxx");  //TODO:Replace with real user
        scrapeConfigEntity.setCreateTime(new Date());
        scrapeConfigEntity.setJobName(param.getJob_name());
        scrapeConfigEntity.setRegion(param.getRegion());
        scrapeConfigEntity.setPromCluster(param.getPromCluster());
        scrapeConfigEntity.setUpdateTime(new Date());
        scrapeConfigEntity.setZone(param.getZone());
        scrapeConfigEntity.setStatus(ScrapeJobStatusEnum.PENDING.getDesc());
        Long id = dao.CreateScrapeConfig(scrapeConfigEntity);
        log.info("ScrapeJobService.CreateScrapeConfig  res : {}", id);
        return Result.success(String.valueOf(id));
    }

    public Result DeleteScrapeConfig(String id) {
        log.info("ScrapeJobService.DeleteScrapeConfig id : {}", id);
        int res = dao.DeleteScrapeConfig(id);
        log.info("ScrapeJobService.DeleteScrapeConfig res : {}", res);
        return Result.success(res);
    }

    public Result GetScrapeConfig(String id) {
        log.info("ScrapeJobService.GetScrapeConfig id : {}", id);
        ScrapeConfigEntity scrapeConfigEntity = dao.GetScrapeConfig(id);
        log.info("ScrapeJobService.GetScrapeConfig res : {}", gson.toJson(scrapeConfigEntity));
        return Result.success(scrapeConfigEntity);
    }

    public Result GetScrapeConfigByName(String name) {
        log.info("ScrapeJobService.GetScrapeConfigByName name : {}", name);
        ScrapeConfigEntity scrapeConfigEntity = dao.GetScrapeConfigByName(name);
        log.info("ScrapeJobService.GetScrapeConfigByName res : {}", gson.toJson(scrapeConfigEntity));
        return Result.success(scrapeConfigEntity);
    }

    public Result UpdateScrapeConfig(String id, ScrapeConfigParam param) {
        log.info("ScrapeJobService.UpdateScrapeConfig  param : {}", gson.toJson(param));
        param = beforeCreateJob(param);
        ScrapeConfigEntity scrapeConfigEntity = new ScrapeConfigEntity();
        scrapeConfigEntity.setId(Long.parseLong(id));
        scrapeConfigEntity.setBody(gson.toJson(getScrapeJobBody(param)));
        scrapeConfigEntity.setJobName(param.getJob_name());
        scrapeConfigEntity.setUpdateTime(new Date());
        String res = dao.UpdateScrapeConfigList(id, scrapeConfigEntity);
        log.info("ScrapeJobService.UpdateScrapeConfig res: {}", res);
        return Result.success(res);
    }

    public Result GetScrapeConfigList(Integer pageSize, Integer pageNo) {
        log.info("ScrapeJobService.GetScrapeConfigList pageSize : {} pageNo : {}", pageSize, pageNo);
        List<ScrapeConfigEntity> lists = dao.GetScrapeConfigList(pageSize, pageNo);
        PageDataVo<ScrapeConfigEntity> pdo = new PageDataVo<ScrapeConfigEntity>();
        pdo.setPageNo(pageNo);
        pdo.setPageSize(pageSize);
        pdo.setTotal(dao.CountScrapeConfig());
        pdo.setList(lists);
        log.info("ScrapeJobService.GetScrapeConfigs count : {}", pdo.getTotal());
        return Result.success(pdo);
    }

    //TODO: Temporary method provided to prometheusClient and needs to be refactored in the future
    public List<ScrapeConfigEntity> getAllScrapeConfigList(String status) {
        List<ScrapeConfigEntity> scrapeConfigEntities = dao.GetAllScrapeConfigList(status);
        return scrapeConfigEntities;
    }

    public void setPendingScrapeConfig() {
        List<ScrapeConfigEntity> allScrapeConfigList = getAllScrapeConfigList(ScrapeJobStatusEnum.SUCCESS.getDesc());
        if (allScrapeConfigList.size() > 0) {
            allScrapeConfigList.forEach(scrapeConfigEntity -> {
                scrapeConfigEntity.setStatus(ScrapeJobStatusEnum.PENDING.getDesc());
                dao.UpdateScrapeConfigList(String.valueOf(scrapeConfigEntity.getId()), scrapeConfigEntity);
            });
        }

    }

    public void updateAllScrapeConfigListStatus(String status, List<Scrape_configs> allConfigs) {
        log.info("ScrapeJobService.updateScrapeConfigListStatus  status : {}", status);
        AtomicInteger failNum = new AtomicInteger(allConfigs.size());
        allConfigs.forEach(config -> {
            int affectRow = dao.UpdateScrapeConfigListByJobName(config.getJob_name(), ScrapeJobStatusEnum.SUCCESS.getDesc());
            failNum.addAndGet(-affectRow);
        });
        log.info("ScrapeJobService.updateScrapeConfigListStatus fail num:{}", failNum.get());
    }

    private ScrapeConfigParam beforeCreateJob(ScrapeConfigParam param) {
        if (StringUtils.isBlank(param.getPromCluster())) {
            param.setPromCluster("public");
        }
        if (StringUtils.isBlank(param.getEnv())) {
            param.setEnv("staging");
        }
        return param;
    }

    private ScrapeConfigDetail getScrapeJobBody(ScrapeConfigParam param) {
        ScrapeConfigDetail scrapeConfigDetail = new ScrapeConfigDetail();
        scrapeConfigDetail.setJob_name(param.getJob_name());
        scrapeConfigDetail.setScrape_interval(param.getScrape_interval());
        scrapeConfigDetail.setScrape_timeout(param.getScrape_timeout());
        scrapeConfigDetail.setHttp_sd_configs(param.getHttp_sd_configs());
        scrapeConfigDetail.setRelabel_configs(param.getRelabel_configs());
        scrapeConfigDetail.setStatic_configs(param.getStatic_configs());
        scrapeConfigDetail.setBasic_auth(param.getBasic_auth());
        scrapeConfigDetail.setMetric_relabel_configs(param.getMetric_relabel_configs());
        scrapeConfigDetail.setScheme(param.getScheme());
        scrapeConfigDetail.setMetrics_path(param.getMetrics_path());
        scrapeConfigDetail.setHonor_labels(param.isHonor_labels());
        scrapeConfigDetail.setHonor_timestamps(param.isHonor_timestamps());
        scrapeConfigDetail.setParams(param.getParams());
        return scrapeConfigDetail;
    }

}
