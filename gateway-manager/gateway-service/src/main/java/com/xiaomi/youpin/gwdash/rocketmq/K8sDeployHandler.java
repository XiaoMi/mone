//package com.xiaomi.youpin.gwdash.rocketmq;
//
//
//import com.google.gson.Gson;
//import com.xiaomi.youpin.gwdash.bo.DataMessage;
//import com.xiaomi.youpin.gwdash.bo.DeployInfo;
//import com.xiaomi.youpin.gwdash.bo.DockerResData;
//import com.xiaomi.youpin.gwdash.common.PipelineStatusEnum;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
//import com.xiaomi.youpin.gwdash.service.DockerfileService;
//import com.xiaomi.youpin.gwdash.service.LogService;
//import com.xiaomi.youpin.gwdash.service.ProjectEnvService;
//import com.xiaomi.youpin.gwdash.ws.CiCdWebSocketHandler;
//import com.xiaomi.youpin.mischedule.api.service.bo.DockerBuildTaskStatus;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
//
//@Component
//@Slf4j
//public class K8sDeployHandler {
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
//    @Value("${rocket.tag.k8s.deploy}")
//    private String k8sDeploy;
//
//    public static String k8sDeployTag;
//
//    public static final String K8S_DEPLOY_INFO = "k8s-deploy-status";
//
//    public static final String K8S_DEPLOY_LOGS = "k8s-deploy-logs";
//
//    @PostConstruct
//    public void init() {
//        k8sDeployTag = k8sDeploy;
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
//        DataMessage message = new DataMessage();
//        ciCdResData.setMessage(ciCdResData.getMessage());
//
//        message.setData(new Gson().toJson(ciCdResData));
//
//        message.setStage(CiCdWebSocketHandler.DEPLOY_STAGE);
//        message.setMsgType(K8S_DEPLOY_INFO);
//
//        logService.saveLog(LogService.ProjectDeployment, id, ciCdResData.getMessage());
//        if (StringUtils.isNotEmpty(pushMsgId)) {
//            CiCdWebSocketHandler.pushMsg(pushMsgId, new Gson().toJson(message));
//        }
//    }
//}
