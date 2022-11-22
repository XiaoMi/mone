package com.xiaomi.youpin.gwdash.bo;

public class AutoStartContextDTO {

    private Long projectId;

    private Long projectEnvId;

    private String autoBuild;

    private String autoDeploy;

    private SessionAccount sessionAccount;

    /**
     * 最后一次提交的的信息
     */
    private String commitMessage;

    private String projectName;

    public boolean contextEnough(){
        if(projectId == null || projectEnvId == null || projectId<=0 || projectEnvId <=0){
            return false;
        }
        if(sessionAccount == null || sessionAccount.getId() == null || sessionAccount.getUsername() == null || sessionAccount.getName() == null || sessionAccount.getToken() == null){
            return false;
        }
        return true;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectEnvId() {
        return projectEnvId;
    }

    public void setProjectEnvId(Long projectEnvId) {
        this.projectEnvId = projectEnvId;
    }

    public String getAutoBuild() {
        return autoBuild;
    }

    public void setAutoBuild(String autoBuild) {
        this.autoBuild = autoBuild;
    }

    public String getAutoDeploy() {
        return autoDeploy;
    }

    public void setAutoDeploy(String autoDeploy) {
        this.autoDeploy = autoDeploy;
    }

    public SessionAccount getSessionAccount() {
        return sessionAccount;
    }

    public void setSessionAccount(SessionAccount sessionAccount) {
        this.sessionAccount = sessionAccount;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
