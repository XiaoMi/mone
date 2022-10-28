//package com.xiaomi.youpin.gwdash.rocketmq;
//
//import com.google.gson.Gson;
//import com.xiaomi.data.push.redis.Redis;
//import com.xiaomi.youpin.gwdash.bo.AutoStartContextDTO;
//import com.xiaomi.youpin.gwdash.bo.DataMessage;
//import com.xiaomi.youpin.gwdash.bo.DeployInfo;
//import com.xiaomi.youpin.gwdash.bo.DockerResData;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.common.PipelineStatusEnum;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectCompileRecord;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
//import com.xiaomi.youpin.gwdash.service.DockerfileService;
//import com.xiaomi.youpin.gwdash.service.FeiShuService;
//import com.xiaomi.youpin.gwdash.service.LogService;
//import com.xiaomi.youpin.gwdash.service.ProjectEnvService;
//import com.xiaomi.youpin.gwdash.ws.CiCdWebSocketHandler;
//import com.xiaomi.youpin.mischedule.api.service.bo.DockerBuildTaskStatus;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.Objects;
//
//@Component
//@Slf4j
//public class WebStaticResourceHandler {
//
//    @Autowired
//    private Dao dao;
//
//    @Autowired
//    private LogService logService;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    @Autowired
//    private DockerfileService dockerDeploymentService;
//
//    @Autowired
//    private FeiShuService feiShuService;
//
//    @Autowired
//    private Redis redis;
//
//    @Autowired
//    private Environment environment;
//
//    @Value("${rocket.tag.web.static.resource.compile}")
//    private String webStaticResourceCompile;
//
//    @Value("${rocket.tag.web.static.resource.deploy}")
//    private String webStaticResourceDeploy;
//
//    public static String compileTag;
//    public static String deployTag;
//
//    public static final String WEB_DEPLOY_INFO = "web-deploy-status";
//    public static final String WEB_COMPILE_INFO = "web-compile-status";
//
//    public static final String WEB_DEPLOY_LOGS = "web-deploy-logs";
//    public static final String WEB_COMPILE_LOGS = "web-compile-logs";
//
//    @PostConstruct
//    public void init() {
//        compileTag = webStaticResourceCompile;
//        deployTag = webStaticResourceDeploy;
//    }
//
//    public void consumeDeployMessage(MessageExt it) {
//        log.info("DockerfileHandler#consumeDeployMessage: {}", it.getMsgId());
//        DockerResData ciCdResData = new Gson().fromJson(new String(it.getBody()), DockerResData.class);
//
//        long id = ciCdResData.getId();
//        String pushMsgId = ciCdResData.getPushMsgId();
//        ProjectPipeline projectPipeline = dao.fetch(ProjectPipeline.class, id);
//        if (null == projectPipeline) {
//            log.warn("DockerfileHandler#consumeDeployMessage record is null: {}", id);
//            return;
//        }
//        DeployInfo deployInfo = projectPipeline.getDeployInfo();
//        if (null == deployInfo) {
//            deployInfo = new DeployInfo();
//            projectPipeline.setDeployInfo(deployInfo);
//        }
//        int step = ciCdResData.getStep();
//        int status = ciCdResData.getStatus();
//        deployInfo.setStep(step);
//        int deployInfoStatus = deployInfo.getStatus();
//        if (deployInfoStatus < status) {
//            deployInfo.setStatus(status);
//            deployInfo.setTime(ciCdResData.getTime());
//            deployInfo.setUtime(System.currentTimeMillis());
//        }
//        if (status == DockerBuildTaskStatus.SUCCESS.getId()) {
//            projectPipeline.setStatus(PipelineStatusEnum.CLOSED.getId());
//            ProjectEnv projectEnv = projectEnvService.getProjectEnvById(projectPipeline.getEnvId()).getData();
//            if (null != projectEnv) {
//                projectEnv.setPipelineId(projectPipeline.getId());
//                dao.update(projectEnv);
//            }
//        }
//        dao.update(projectPipeline);
//        notifyDeployFinished(pushMsgId,status);
//        DataMessage message = new DataMessage();
//        ciCdResData.setMessage(ciCdResData.getMessage() + " [ " + MS2s(ciCdResData.getTime()) + " s ]");
//
//        message.setData(new Gson().toJson(ciCdResData));
//
//        //message.setData(new String(it.getBody()));
//
//
//        message.setStage(CiCdWebSocketHandler.DEPLOY_STAGE);
//        message.setMsgType(WEB_DEPLOY_INFO);
//
////        projectPipeline.setFtime(System.currentTimeMillis());
////        dao.update(projectPipeline);
//
//        logService.saveLog(LogService.ProjectDeployment, id, ciCdResData.getMessage());
//        if (StringUtils.isNotEmpty(pushMsgId)) {
//            if (deployInfoStatus < status) {
//                CiCdWebSocketHandler.pushMsg(pushMsgId, new Gson().toJson(message));
//            }
//            message.setMsgType(WEB_DEPLOY_LOGS);
//            CiCdWebSocketHandler.pushMsg(pushMsgId, new Gson().toJson(message));
//        }
//    }
//
//    public void consumeCompileMessage(MessageExt it) {
//        log.info("DockerfileHandler#consumeCompileMessage: {}, {}", it.getMsgId(), new String(it.getBody()));
//        DockerResData ciCdResData = new Gson().fromJson(new String(it.getBody()), DockerResData.class);
//        long id = ciCdResData.getId();
//        String pushMsgId = ciCdResData.getPushMsgId();
//        ProjectPipeline projectPipeline = dao.fetch(ProjectPipeline.class, id);
//        if (null == projectPipeline) {
//            log.warn("DockerfileHandler#consumeCompileMessage record is null: {}", id);
//            return;
//        }
//        int step = ciCdResData.getStep();
//        int status = ciCdResData.getStatus();
//        DataMessage message = new DataMessage();
//        ProjectCompileRecord projectCompilation = dao.fetch(ProjectCompileRecord.class, projectPipeline.getCompilationId());
//        if (null != projectCompilation) {
//            projectCompilation.setUtime(System.currentTimeMillis());
//            projectCompilation.setStep(step);
//            projectCompilation.setStatus(status);
//            projectCompilation.setJarName(ciCdResData.getImageTags());
//            dao.update(projectCompilation, Cnd.where("step", "<", step)
//                    .or(Cnd.exps("step", "=", step)
//                            .and("status", "<", status)));
//        }
//
//        ciCdResData.setMessage(ciCdResData.getMessage() + " [ " + MS2s(ciCdResData.getTime()) + " s ]");
//
//        message.setData(new Gson().toJson(ciCdResData));
//
//        //message.setData(new String(it.getBody()));
//        message.setStage(CiCdWebSocketHandler.COMPILE_STAGE);
//        message.setMsgType(WEB_COMPILE_INFO);
//
//
//        if (StringUtils.isNotEmpty(pushMsgId)) {
//            CiCdWebSocketHandler.pushMsg(pushMsgId, new Gson().toJson(message));
//            message.setMsgType(WEB_COMPILE_LOGS);
//            CiCdWebSocketHandler.pushMsg(pushMsgId, new Gson().toJson(message));
//        }
//
////        projectPipeline.setFtime(System.currentTimeMillis());
////        dao.update(projectPipeline);
//        logService.saveLog(LogService.ProjectWebBuild, id, ciCdResData.getMessage());
//    }
//
//    /**
//     * 毫秒转秒，保留2位
//     * @param ms
//     * @return
//     */
//    public String MS2s(long ms) {
//        double doubleMS= Double.valueOf(ms) * 0.001;
//        String s = String.format("%.2f", doubleMS);
//        return s;
//    }
//
//    public void notifyDeployFinished(String  pushMsgId,int deployStatus){
//        if(deployStatus == DockerBuildTaskStatus.RUNNING.getId()){
//            return;
//        }
//        boolean deploySuccess = (DockerBuildTaskStatus.SUCCESS.getId() ==deployStatus);
//        String serverEnv = environment.getProperty("server.serverEnv");
//        if(!"staging".equals(serverEnv) && !"dev".equals(serverEnv)){
//            return;
//        }
//        AutoStartContextDTO autoStartContext = redis.get(Consts.AUTO_START_PREFIX + pushMsgId, AutoStartContextDTO.class);
//        if(autoStartContext != null && autoStartContext.contextEnough()){
//            if(Objects.equals("true",autoStartContext.getAutoBuild())){
//                ProjectEnv projectEnv = projectEnvService.getProjectEnvById(autoStartContext.getProjectEnvId()).getData();
//                if(projectEnv != null){
//                    String message = "应用部署"+(deploySuccess?"成功":"失败")+"\n部署环境: " + projectEnv.getGroup() +"\n部署名称：" + projectEnv.getName();
//                    feiShuService.sendMessageAsync(autoStartContext.getSessionAccount().getEmail(), message);
//                }
//            }
//        }
//        redis.del(Consts.AUTO_START_PREFIX + pushMsgId);
//    }
//}
