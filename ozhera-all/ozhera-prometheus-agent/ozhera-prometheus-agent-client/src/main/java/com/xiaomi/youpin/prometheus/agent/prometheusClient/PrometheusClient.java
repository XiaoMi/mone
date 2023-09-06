package com.xiaomi.youpin.prometheus.agent.prometheusClient;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.google.gson.Gson;
import com.xiaomi.youpin.prometheus.agent.Commons;
import com.xiaomi.youpin.prometheus.agent.client.Client;
import com.xiaomi.youpin.prometheus.agent.entity.ScrapeConfigEntity;
import com.xiaomi.youpin.prometheus.agent.enums.ScrapeJobStatusEnum;
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
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.xiaomi.youpin.prometheus.agent.Commons.HTTP_GET;
import static com.xiaomi.youpin.prometheus.agent.Commons.HTTP_POST;

@Slf4j
@Service
public class PrometheusClient implements Client {

    @NacosValue(value = "${job.prometheus.healthAddr}", autoRefreshed = true)
    private String healthAddr;

    @NacosValue(value = "${job.prometheus.reloadAddr}", autoRefreshed = true)
    private String reloadAddr;

    @NacosValue(value = "${job.prometheus.filePath}", autoRefreshed = true)
    private String filePath;

    private String backFilePath;

    @NacosValue(value = "${job.prometheus.enabled}", autoRefreshed = true)
    private String enabled;

    // Set to true after the first GetLocalConfigs
    private boolean firstInitSign = false;

    @Autowired
    ScrapeJobService scrapeJobService;

    public static final Gson gson = new Gson();
    private List<Scrape_configs> localConfigs = new ArrayList<>();

    @PostConstruct
    public void init() {
        backFilePath = filePath + ".bak";
        if (enabled.equals("true")) {
            // Initialization, request the health interface to verify if it is available.
            log.info("PrometheusClient request health url :{}", healthAddr);
            String getHealthRes = Http.innerRequest("", healthAddr, HTTP_GET);
            log.info("PrometheusClient request health res :{}", getHealthRes);
            if (getHealthRes.equals("200")) {
                // In the first phase, we will not do status management, just convert to pending and reload
                scrapeJobService.setPendingScrapeConfig();
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
        // Get all pending collection tasks from the db every 30 seconds.
        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
            try {

                log.info("PrometheusClient start GetLocalConfigs");
                List<ScrapeConfigEntity> allScrapeConfigList = scrapeJobService.getAllScrapeConfigList(ScrapeJobStatusEnum.PENDING.getDesc());
                // First, clear the results from the last time
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
                log.info("PrometheusClient GetLocalConfigs done ,and jobNum :{}", localConfigs.size());
                firstInitSign = true;
            } catch (Exception e) {
                log.error("PrometheusClient GetLocalConfigs error :{}", e.getMessage());
            }
        }, 0, 30, TimeUnit.SECONDS);
    }

    @Override
    @SneakyThrows
    public void CompareAndReload() {

        new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
            try {

                if (localConfigs.size() <= 0) {
                    // no pending crawl jobs, return directly
                    log.info("prometheus scrapeJob no need to reload");
                    return;
                }
                // If there are changes, call the reload interface
                // Read the local Prometheus configuration file
                if (!firstInitSign) {
                    log.info("PrometheusClient CompareAndReload waiting..");
                    return;
                }
                log.info("PrometheusClient start CompareAndReload");
                PrometheusConfig prometheusConfig = getPrometheusConfig(filePath);
                if (prometheusConfig == null || prometheusConfig.getScrape_configs().size() == 0 || prometheusConfig.getGlobal() == null) {
                    // problem with the configuration, end directly
                    log.error("prometheusConfig null and return");
                    return;
                }
                // Compare and deduplicate the Prometheus data with the data to be reloaded
                List<Scrape_configs> promScrapeConfig = prometheusConfig.getScrape_configs();
                HashSet<Scrape_configs> configSet = new HashSet<>(promScrapeConfig);
                configSet.addAll(localConfigs);
                ArrayList<Scrape_configs> configList = new ArrayList<>(configSet);
                log.info("prometheusYMLJobNum: {},dbPEndingJobNum: {},after Deduplication JobNum: {}", promScrapeConfig.size(), localConfigs.size(), configSet.size());
                // Replace the scrapeConfig part
                prometheusConfig.setScrape_configs(configList);
                // Generate yaml and overwrite the configuration
                log.info("PrometheusClient write final config:{}", gson.toJson(prometheusConfig));
                writePrometheusConfig2Yaml(prometheusConfig);
                log.info("PrometheusClient request reload url :{}", reloadAddr);
                String getReloadRes = Http.innerRequest("", reloadAddr, HTTP_POST);
                log.info("PrometheusClient request reload res :{}", getReloadRes);
                if (getReloadRes.equals("200")) {
                    log.info("PrometheusClient request reload success");
                    // After success, delete the backup and write the data back to the database with the status as success
                    scrapeJobService.updateAllScrapeConfigListStatus(ScrapeJobStatusEnum.SUCCESS.getDesc(), configList);
                    deleteBackConfig();
                } else {
                    // restore the configuration using the backup
                    log.info("PrometheusClient request reload fail and begin rollback config");
                    boolean rollbackRes = restoreConfiguration(backFilePath, filePath);
                    log.info("PrometheusClient request reload fail and rollbackRes: {}", rollbackRes);
                }
            } catch (Exception e) {
                log.error("PrometheusClient CompareAndReload error :{}", e.getMessage());
            }
        }, 0, 30, TimeUnit.SECONDS);

    }

    private synchronized PrometheusConfig getPrometheusConfig(String path) {
        log.info("PrometheusClient getPrometheusConfig path : {}", path);
        String content = FileUtil.LoadFile(path);
        PrometheusConfig prometheusConfig = YamlUtil.toObject(content, PrometheusConfig.class);
        log.info("PrometheusClient config : {}", prometheusConfig);
        //System.out.println(content);
        // Convert to Prometheus configuration class
        return prometheusConfig;
    }

    private void writePrometheusConfig2Yaml(PrometheusConfig prometheusConfig) {
        // Convert to yaml
        String promYml = YamlUtil.toYaml(prometheusConfig);
        log.info("checkNull promyml");
        // Check if the file exists.
        if (!isFileExists(filePath)) {
            log.error("PrometheusClient PrometheusClient no files here path: {}", filePath);
            return;
        }
        // backup
        backUpConfig();
        // Overwrite configuration
        String writeRes = FileUtil.WriteFile(filePath, promYml);
        if (StringUtils.isEmpty(writeRes)) {
            log.error("PrometheusClient WriteFile Error");
        }
        log.info("PrometheusClient WriteFile res : {}", writeRes);
    }

    // back config file
    private void backUpConfig() {
        // Check if the file exists
        if (!isFileExists(filePath)) {
            log.error("PrometheusClient backUpConfig no files here path: {}", filePath);
            return;
        }

        // Create a backup file if it does not exist
        if (!isFileExists(backFilePath)) {
            log.info("PrometheusClient backUpConfig backFile does not exist and begin create");
            FileUtil.GenerateFile(backFilePath);
        }

        // Get the current configuration file
        String content = FileUtil.LoadFile(filePath);
        // write backup
        String writeRes = FileUtil.WriteFile(backFilePath, content);
        if (StringUtils.isEmpty(writeRes)) {
            log.error("PrometheusClient backUpConfig WriteFile Error");
        } else {
            log.info("PrometheusClient backUpConfig WriteFile success");
        }
    }

    // After the reload is successful, delete the backup configuration
    private void deleteBackConfig() {
        //Check if the file exists.
        if (!isFileExists(backFilePath)) {
            log.error("PrometheusClient deleteBackConfig no files here path: {}", backFilePath);
            return;
        }
        //Delete backup files.
        boolean deleteRes = FileUtil.DeleteFile(backFilePath);
        if (deleteRes) {
            log.info("PrometheusClient deleteBackConfig delete success");
        } else {
            log.error("PrometheusClient deleteBackConfig delete fail");
        }
    }

    //Check if the file exists.
    private boolean isFileExists(String filePath) {
        return FileUtil.IsHaveFile(filePath);
    }

    //Restore the original file using the backup file.
    private boolean restoreConfiguration(String oldFilePath, String newFilePath) {
        log.info("PrometheusClient restoreConfiguration oldPath: {}, newPath: {}", oldFilePath, newFilePath);
        boolean b = FileUtil.RenameFile(oldFilePath, newFilePath);
        return b;
    }
}
