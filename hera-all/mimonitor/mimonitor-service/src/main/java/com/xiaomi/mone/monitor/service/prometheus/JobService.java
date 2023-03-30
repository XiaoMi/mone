package com.xiaomi.mone.monitor.service.prometheus;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xiaomi.mone.monitor.dao.AppScrapeJobDao;
import com.xiaomi.mone.monitor.dao.model.AppScrapeJob;
import com.xiaomi.mone.monitor.result.ErrorCode;
import com.xiaomi.mone.monitor.result.Result;
import com.xiaomi.mone.monitor.service.alertmanager.AlertServiceAdapt;
import com.xiaomi.mone.monitor.service.alertmanager.client.Request;
import com.xiaomi.mone.monitor.service.alertmanager.client.model.HttpMethodName;
import com.xiaomi.mone.monitor.service.alertmanager.impl.MiCloudAlertManager;
import com.xiaomi.mone.monitor.service.model.PageData;
import com.xiaomi.mone.monitor.service.scrapeJob.ScrapeJobAdapt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author zhangxiaowei
 */

@Slf4j
@Service
public class JobService {

    @Value("${alarm.domain:}")
    private String alarmDomain;
    @Value("${prometheus.alarm.env:staging}")
    private String prometheusAlarmEnv;

    @Autowired
    MiCloudAlertManager alarmService;

    @Autowired
    AppScrapeJobDao appScrapeJobDao;

    @Autowired
    ScrapeJobAdapt scrapeJobAdapt;

    public static final byte CREATE_JOB_FAIL = 0;
    public static final byte CREATE_JOB_SUCCESS = 1;
    public static final byte DELETED_JOB = 2;

    private final Gson gson = new Gson();

    //创建prometheus抓取的job
    public Result createJob(Integer iamId, String user, String jobJson, String jobDesc) {
        if (iamId == null) {
            iamId = alarmService.getDefaultIamId();
        }
        //检验传入的json合法性
        String checkResult = checkJobJson(jobJson);
        if (!"ok".equals(checkResult)) {
            log.error("AlarmService.createjob request jobJson invalid :{}", checkResult);
            return Result.fail(ErrorCode.invalidParamError);
        }
        //改造成适配器模式
        try {
            JsonObject jsonObject = gson.fromJson(jobJson, JsonObject.class);
            Result result = scrapeJobAdapt.addScrapeJob(jsonObject, String.valueOf(iamId), user);
            String data = String.valueOf(result.getData());
            //成不成功都入库
            //获取json中的job_name
            String jobName = jsonObject.get("job_name").getAsString();
            //model填充数据
            AppScrapeJob appScrapeJob = new AppScrapeJob();
            appScrapeJob.setJobName(jobName);
            appScrapeJob.setJobJson(jobJson);
            appScrapeJob.setIamId(iamId);
            appScrapeJob.setUser(user);
            appScrapeJob.setMessage(result.getMessage());
            if (result.getCode() == 0 && result.getMessage().equals("success")) {
                appScrapeJob.setStatus(CREATE_JOB_SUCCESS);
                appScrapeJob.setData(data);
                appScrapeJob.setJobDesc(jobDesc);
            } else {
                appScrapeJob.setStatus(CREATE_JOB_FAIL);
                appScrapeJob.setData("fail");
            }
            int dbResult = appScrapeJobDao.insertScrapeJob(appScrapeJob);
            log.info("AlarmService.createJob response:{},dbResult: {}", new Gson().toJson(result).toString(), dbResult);
            //return jsonObjectResult;
            return Result.success(new Gson().toJson(data));
        } catch (Exception e) {
            log.error("AlarmService.createJob error : {}", e.toString());
            return Result.fail(ErrorCode.unknownError);
        }
    }

    //查看prometheus抓取的job
    public Result searchJob(Integer iamId, String user, int scrapeConfigId) {
        if (iamId == null) {
            iamId = alarmService.getDefaultIamId();
        }
        AppScrapeJob job = appScrapeJobDao.searchScrapeJob(scrapeConfigId);
        //改成适配器模式
        Result result = scrapeJobAdapt.queryScrapeJob(Integer.parseInt(job.getData()), String.valueOf(iamId), user);
        String data = String.valueOf(result.getData());
        log.info("JobService.searchJob,response:{}", new Gson().toJson(result).toString());
        //jsonObjectResult.getData().getAsString();
        return Result.success(job);
    }

    //查看prometheus抓取的job
    public Result searchJobByName(Integer iamId, String user, String scrapeConfigName) {
        if (iamId == null) {
            iamId = alarmService.getDefaultIamId();
        }
        //改成适配器模式
        try {
            Result result = scrapeJobAdapt.queryScrapeJobByName(scrapeConfigName, String.valueOf(iamId), user);
            String data = String.valueOf(result.getData());
            log.info("JobService.searchJob,response:{}", new Gson().toJson(result).toString());
            //jsonObjectResult.getData().getAsString();
            return Result.success(data);
        } catch (Exception e) {
            log.error("searchJobByName error :{}",e.getMessage());
            return Result.fail(ErrorCode.unknownError);
        }
    }

    //更新prometheus抓取的job
    public Result updateJob(Integer iamId, String user, String jobJson, int primaryId, String jobDesc) {
        if (iamId == null) {
            iamId = alarmService.getDefaultIamId();
        }
        //检验传入的json合法性
        String checkResult = checkJobJson(jobJson);
        if (!"ok".equals(checkResult)) {
            log.error("JobService.updateJob request jobJson invalid :{}", checkResult);
            return Result.fail(ErrorCode.invalidParamError);
        }
        AppScrapeJob appScrapeJob = appScrapeJobDao.searchScrapeJob(primaryId);
        if (appScrapeJob == null) {
            return Result.fail(ErrorCode.CannotUpdateANonExistingJob);
        }
        if (!(appScrapeJob.getStatus() == CREATE_JOB_SUCCESS)) {
            return Result.fail(ErrorCode.OnlyJobsThatHaveBeenCreatedSuccessfullyCanBeUpdated);
        }
        String scrapeConfigId = appScrapeJob.getData();
        //请求更新prometheus job接口
        //修改成适配器
        JsonObject jsonObject = gson.fromJson(jobJson, JsonObject.class);
        Result result = scrapeJobAdapt.editScrapeJob(Integer.parseInt(scrapeConfigId), jsonObject, String.valueOf(iamId), user);
        log.info("JobService.updateJob response:{}", new Gson().toJson(result).toString());
        //更新库
        if (result.getCode() != 0 || !result.getMessage().equals("success")) {
            return Result.fail(ErrorCode.UpdateJobFail);
        }
        String jobName = jsonObject.get("job_name").getAsString();
        appScrapeJob.setJobName(jobName);
        appScrapeJob.setJobJson(jobJson);
        appScrapeJob.setJobDesc(jobDesc);
        appScrapeJobDao.updateScrapeJob(appScrapeJob);
        // return jsonObjectResult;
        return Result.success(new Gson().toJson(result.getData()).toString());
    }

    //删除prometheus抓取的job
    public Result deleteJob(Integer iamId, String user, int primaryId) {
        if (iamId == null) {
            iamId = alarmService.getDefaultIamId();
        }
        //查库
        try {
            AppScrapeJob appScrapeJob = appScrapeJobDao.searchScrapeJob(primaryId);
            if (appScrapeJob == null) {
                return Result.fail(ErrorCode.nonExistentScrapeId);
            }
            String scrapeTaskId = appScrapeJob.getData();
            if (StringUtils.isEmpty(scrapeTaskId)) {
                return Result.fail(ErrorCode.nonExistentScrapeId);
            }
            if (appScrapeJob.getStatus() == DELETED_JOB) {
                //软删除状态不能再删除
                return Result.fail(ErrorCode.CannotDeleteADeletedJob);
            }
            //修改为适配器模式
            Result result = scrapeJobAdapt.delScrapeJob(Integer.parseInt(scrapeTaskId), String.valueOf(iamId), user);
            String data = String.valueOf(result.getData());
            log.info("JobService.deleteJob response:{}", new Gson().toJson(result).toString());
            //软删除,更新状态
            if (result.getCode() != 0 || !result.getMessage().equals("success")) {
                return Result.fail(ErrorCode.DeleteJobFail);
            }
            appScrapeJob.setStatus(DELETED_JOB);
            appScrapeJobDao.updateScrapeJob(appScrapeJob);
            return Result.success(new Gson().toJson(data).toString());
        } catch (Exception e) {
            log.error("JobService.deleteJob fail error : {}", e.toString());
            return Result.fail(ErrorCode.DeleteJobFail);
        }
    }

    //查看prometheus抓取的job列表
    public Result searchJobList(Integer iamId, String user, Integer pageSize, Integer pageNo) {
        if (iamId == null) {
            iamId = alarmService.getDefaultIamId();
        }
       /* String region = "";
        String zone = "";
        if (prometheusAlarmEnv.equals("staging")) {
            region = REGION_STAGING;
            zone = ZONE_STAGING;
        }
        if (prometheusAlarmEnv.equals("production")) {
            region = REGION_ONLINE;
            zone = ZONE_ONLINE;
        }
        StringBuilder url = new StringBuilder(alarmDomain).append(MiCloudAlertManager.alarm_job_option_uri_list)
                .append("?region=").append(region).append("&zone=").append(zone).append("&page_size=").append(pageSize).append("&page_no=").append(pageNo);
        System.out.println(url);
        Request request = alarmService.createRequest(HttpMethodName.GET, url.toString(), iamId, user);
        Result<JsonElement> jsonObjectResult = alarmService.executeRequest(request);
        log.info("JobService.searchJobList request : {},response:{}", new Gson().toJson(request).toString(), new Gson().toJson(jsonObjectResult).toString());
        //jsonObjectResult.getData().getAsString();
        */
        PageData pd = new PageData();
        pd.setPage(pageNo);
        pd.setPageSize(pageSize);
        pd.setTotal(appScrapeJobDao.getJobSuccessTotal());
        pd.setList(appScrapeJobDao.searchScrapeJobList(pageSize, pageNo));
        return Result.success(pd);
    }

    //检验传入的prometheus job json的合法性
    private String checkJobJson(String jobJson) {
        //1、region、zone、env、job_name不为空
        //2、region为chn-beijing zone为c3 ,env与配置相同
        try {
            JsonObject jsonObject = gson.fromJson(jobJson, JsonObject.class);
            String jobName = jsonObject.get("job_name").getAsString();
            String region = jsonObject.get("region").getAsString();
            String zone = jsonObject.get("zone").getAsString();
            String env = jsonObject.get("env").getAsString();
            if (StringUtils.isEmpty(jobName) || StringUtils.isEmpty(region) || StringUtils.isEmpty(zone) || StringUtils.isEmpty(env)) {
                return "Missing some request parameters";
            }
            return "ok";
        } catch (Exception e) {
            String errStr = "prometheus job json not right, error is: " + e;
            log.error(errStr);
            return errStr;
        }
    }
}
