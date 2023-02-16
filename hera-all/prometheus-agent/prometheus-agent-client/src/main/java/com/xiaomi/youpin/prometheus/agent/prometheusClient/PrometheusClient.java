package com.xiaomi.youpin.prometheus.agent.prometheusClient;

import com.google.gson.Gson;
import com.xiaomi.youpin.prometheus.agent.Commons;
import com.xiaomi.youpin.prometheus.agent.client.Client;
import com.xiaomi.youpin.prometheus.agent.entity.ScrapeConfigEntity;
import com.xiaomi.youpin.prometheus.agent.param.prometheus.PrometheusConfig;
import com.xiaomi.youpin.prometheus.agent.param.prometheus.Scrape_configs;
import com.xiaomi.youpin.prometheus.agent.param.scrapeConfig.ScrapeConfigDetail;
import com.xiaomi.youpin.prometheus.agent.service.prometheus.ScrapeJobService;
import com.xiaomi.youpin.prometheus.agent.util.FileUtil;
import com.xiaomi.youpin.prometheus.agent.util.Http;
import com.xiaomi.youpin.prometheus.agent.util.YamlUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.xiaomi.youpin.prometheus.agent.Commons.HTTP_GET;
import static com.xiaomi.youpin.prometheus.agent.Commons.HTTP_POST;

@Slf4j
@Service
public class PrometheusClient implements Client {

    @Value("${job.prometheus.healthAddr}")
    private String healthAddr;

    @Value("${job.prometheus.reloadAddr}")
    private String reloadAddr;

    @Value("${job.prometheus.filePath}")
    private String filePath;

    private String backFilePath;

    @Value("${job.prometheus.enabled}")
    private String enabled;

    //第一次GetLocalConfigs后置位true
    private boolean firstInitSign = false;

    @Autowired
    ScrapeJobService scrapeJobService;

    public static final Gson gson = new Gson();
    private List<Scrape_configs> localConfigs = new ArrayList<>();

    @PostConstruct
    public void init() {
        backFilePath = filePath + ".bak";
        if (enabled.equals("true")) {
            //初始化，请求health接口验证是否可用
            log.info("PrometheusClient request health url :{}", healthAddr);
            String getHealthRes = Http.innerRequest("", healthAddr, HTTP_GET);
            log.info("PrometheusClient request health res :{}", getHealthRes);
            if (getHealthRes.equals("200")) {
                GetLocalConfigs();
                CompareAndReload();
            } else {
                log.error("PrometheusClient request health fail !!!");
                System.exit(-1);
            }
        } else {
            log.info("PrometheusClient not init");
        }

    }

    @Override
    public void GetLocalConfigs() {
        //30s一次从从db里获取所有pending的采集任务
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
            log.info("PrometheusClient start GetLocalConfigs");
            List<ScrapeConfigEntity> allScrapeConfigList = scrapeJobService.getAllScrapeConfigList();
            //先清空上一次结果
            localConfigs.clear();
            allScrapeConfigList.forEach(item -> {
                ScrapeConfigDetail detail = gson.fromJson(item.getBody(), ScrapeConfigDetail.class);
                Scrape_configs sc = new Scrape_configs();
                sc.setRelabel_configs(detail.getRelabel_configs());
                sc.setMetric_relabel_configs(detail.getMetric_relabel_configs());
                sc.setStatic_configs(detail.getStatic_configs());
                sc.setJob_name(detail.getJob_name());
                sc.setParams(detail.getParams());
                sc.setMetrics_path(detail.getMetrics_path());
                sc.setHonor_labels(detail.isHonor_labels());
                sc.setHttp_sd_configs(detail.getHttp_sd_configs());
                sc.setHttp_sd_configs(detail.getHttp_sd_configs());
                localConfigs.add(sc);
            });
            log.info("PrometheusClient GetLocalConfigs done!");
            firstInitSign = true;
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    @SneakyThrows
    public void CompareAndReload() {

        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
            //如果有变动，调用reload接口，一期直接reload
            //读取本地prometheus配置文件
            if (!firstInitSign) {
                log.info("PrometheusClient CompareAndReload waiting..");
                return;
            }
            log.info("PrometheusClient start CompareAndReload");
            PrometheusConfig prometheusConfig = getPrometheusConfig(filePath);
            if (prometheusConfig == null) {
                //如果配置出现问题，直接结束
                log.error("prometheusConfig null and return");
                return;
            }
            //替换scrapeConfig部分
            prometheusConfig.setScrape_configs(localConfigs);
            //生成yaml 并覆盖配置
            writePrometheusConfig2Yaml(prometheusConfig);
            log.info("PrometheusClient request reload url :{}", reloadAddr);
            String getReloadRes = Http.innerRequest("", reloadAddr, HTTP_POST);
            log.info("PrometheusClient request reload res :{}", getReloadRes);
            if (getReloadRes.equals("200")) {
                log.info("PrometheusClient request reload success");
                //成功后，删除备份
                deleteBackConfig();
            } else {
                //如果reload失败，用备份恢复配置
                log.info("PrometheusClient request reload fail and begin rollback config");
                boolean rollbackRes = restoreConfiguration(backFilePath, filePath);
                log.info("PrometheusClient request reload fail and rollbackRes: {}", rollbackRes);
            }
        }, 0, 30, TimeUnit.SECONDS);

    }

    private PrometheusConfig getPrometheusConfig(String path) {
        log.info("PrometheusClient getPrometheusConfig path : {}", path);
        String content = FileUtil.LoadFile(path);
        PrometheusConfig prometheusConfig = YamlUtil.toObject(content, PrometheusConfig.class);
        log.info("PrometheusClient config : {}", prometheusConfig);
        //System.out.println(content);
        //转换成prometheus配置类
        return prometheusConfig;
    }

    private void writePrometheusConfig2Yaml(PrometheusConfig prometheusConfig) {
        //转换成yaml
        String promYml = YamlUtil.toYaml(prometheusConfig);
        log.info("checkNull promyml");
        //检验文件是否存在
        if (!isFileExists(filePath)) {
            log.error("PrometheusClient PrometheusClient no files here path: {}", filePath);
            return;
        }
        //备份
        backUpConfig();
        //覆盖写配置
        String writeRes = FileUtil.WriteFile(filePath, promYml);
        if (StringUtils.isEmpty(writeRes)) {
            log.error("PrometheusClient WriteFile Error");
        }
        log.info("PrometheusClient WriteFile res : {}", writeRes);
    }

    //备份配置文件
    private void backUpConfig() {
        //检验文件是否存在
        if (!isFileExists(filePath)) {
            log.error("PrometheusClient backUpConfig no files here path: {}", filePath);
            return;
        }

        //如果没有备份文件则创建
        if (!isFileExists(backFilePath)) {
            log.info("PrometheusClient backUpConfig backFile does not exist and begin create");
            FileUtil.GenerateFile(backFilePath);
        }

        //获取当前配置文件
        String content = FileUtil.LoadFile(filePath);
        //写备份
        String writeRes = FileUtil.WriteFile(backFilePath, content);
        if (StringUtils.isEmpty(writeRes)) {
            log.error("PrometheusClient backUpConfig WriteFile Error");
        } else {
            log.info("PrometheusClient backUpConfig WriteFile success");
        }
    }

    //reload成功后，删除备份配置
    private void deleteBackConfig() {
        //检验文件是否存在
        if (!isFileExists(backFilePath)) {
            log.error("PrometheusClient deleteBackConfig no files here path: {}", backFilePath);
            return;
        }
        //删除备份文件
        boolean deleteRes = FileUtil.DeleteFile(backFilePath);
        if (deleteRes) {
            log.info("PrometheusClient deleteBackConfig delete success");
        } else {
            log.error("PrometheusClient deleteBackConfig delete fail");
        }
    }

    //校验文件是否存在
    private boolean isFileExists(String filePath) {
        return FileUtil.IsHaveFile(filePath);
    }

    //用备份文件恢复原文件
    private boolean restoreConfiguration(String oldFilePath, String newFilePath) {
        log.info("PrometheusClient restoreConfiguration oldPath: {}, newPath: {}", oldFilePath, newFilePath);
        boolean b = FileUtil.RenameFile(oldFilePath, newFilePath);
        return b;
    }
}
