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

package com.xiaomi.youpin.gwdash.bo;

import com.xiaomi.youpin.gwdash.dao.model.ApiInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author goodjava@qq.com
 */
public class ApiInfoDetail extends ApiInfo {

    private boolean allowCache;
    private boolean allowLog;
    private boolean allowMock;
    private boolean allowScript;
    private boolean allowCors;
    private boolean allowToken;
    private boolean offline;
    private boolean allowAuth;
    private boolean allowIpAntiBrush;
    private boolean allowUidAntiBrush;
    private boolean allowPreview;

    private boolean useQpsLimit;
    private boolean hasCollected;
    private  String groupName;

    /**
     * mock 数据
      */
    private String mockData;

    /**
     * mock 数据描述
     */
    private String mockDataDesc;

    private String contentType;

    private String script;

    private String scriptParams;

    private String scriptMethodName;

    private int scriptType;

    /**
     * API 编排配置
     */
    private String groupConfig;

    private String gitProjectId;

    private String gitToken;

    private String gitPath;

    private String gitBranch;

    private String commit;

    private String dsIds;

    /**
     * filter
     */
    private String filterParams;
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getFilterParams() {
        return filterParams;
    }

    public void setFilterParams(String filterParams) {
        this.filterParams = filterParams;
    }

    public boolean isAllowCache() {
        return allowCache;
    }

    public void setAllowCache(boolean allowCache) {
        this.allowCache = allowCache;
    }

    public boolean isAllowLog() {
        return allowLog;
    }

    public void setAllowLog(boolean allowLog) {
        this.allowLog = allowLog;
    }

    public boolean isAllowMock() {
        return allowMock;
    }

    public void setAllowMock(boolean allowMock) {
        this.allowMock = allowMock;
    }

    public boolean isAllowScript() {
        return allowScript;
    }

    public void setAllowScript(boolean allowScript) {
        this.allowScript = allowScript;
    }

    public boolean isAllowCors() {
        return allowCors;
    }

    public void setAllowCors(boolean allowCors) {
        this.allowCors = allowCors;
    }

    public boolean isAllowToken() {
        return allowToken;
    }

    public void setAllowToken(boolean allowToken) {
        this.allowToken = allowToken;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isAllowAuth() {
        return allowAuth;
    }

    public void setAllowAuth(boolean allowAuth) {
        this.allowAuth = allowAuth;
    }

    public boolean isUseQpsLimit() {
        return useQpsLimit;
    }

    public void setUseQpsLimit(boolean useQpsLimit) {
        this.useQpsLimit = useQpsLimit;
    }

    public String getMockData() {
        return mockData;
    }

    public void setMockData(String mockData) {
        this.mockData = mockData;
    }

    public String getMockDataDesc() {
        return mockDataDesc;
    }

    public void setMockDataDesc(String mockDataDesc) {
        this.mockDataDesc = mockDataDesc;
    }

    public String getDsIds() {
        return dsIds;
    }

    public void setDsIds(String dsIds) {
        this.dsIds = dsIds;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScriptParams() {
        return scriptParams;
    }

    public void setScriptParams(String scriptParams) {
        this.scriptParams = scriptParams;
    }

    public String getScriptMethodName() {
        return scriptMethodName;
    }

    public void setScriptMethodName(String scriptMethodName) {
        this.scriptMethodName = scriptMethodName;
    }

    public int getScriptType() {
        return scriptType;
    }

    public void setScriptType(int scriptType) {
        this.scriptType = scriptType;
    }

    public String getGroupConfig() {
        return groupConfig;
    }

    public void setGroupConfig(String groupConfig) {
        this.groupConfig = groupConfig;
    }

    public String getGitProjectId() {
        return gitProjectId;
    }

    public void setGitProjectId(String gitProjectId) {
        this.gitProjectId = gitProjectId;
    }

    public String getGitToken() {
        return gitToken;
    }

    public void setGitToken(String gitToken) {
        this.gitToken = gitToken;
    }

    public String getGitPath() {
        return gitPath;
    }

    public void setGitPath(String gitPath) {
        this.gitPath = gitPath;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    public boolean isAllowIpAntiBrush() {
        return allowIpAntiBrush;
    }

    public void setAllowIpAntiBrush(boolean allowIpAntiBrush) {
        this.allowIpAntiBrush = allowIpAntiBrush;
    }

    public boolean isAllowUidAntiBrush() {
        return allowUidAntiBrush;
    }

    public void setAllowUidAntiBrush(boolean allowUidAntiBrush) {
        this.allowUidAntiBrush = allowUidAntiBrush;
    }

    public boolean isAllowPreview() {
        return allowPreview;
    }

    public void setAllowPreview(boolean allowPreview) {
        this.allowPreview = allowPreview;
    }

    public boolean isHasCollected() {
        return hasCollected;
    }

    public void setHasCollected(boolean hasCollected) {
        this.hasCollected = hasCollected;
    }

    @Override
    public String toString() {
        return "ApiInfoDetail{" +
                "allowCache=" + allowCache +
                ", allowLog=" + allowLog +
                ", allowMock=" + allowMock +
                ", allowScript=" + allowScript +
                ", allowCors=" + allowCors +
                ", allowToken=" + allowToken +
                ", offline=" + offline +
                ", allowAuth=" + allowAuth +
                ", allowIpAntiBrush=" + allowIpAntiBrush +
                ", allowUidAntiBrush=" + allowUidAntiBrush +
                ", allowPreview=" + allowPreview +
                ", useQpsLimit=" + useQpsLimit +
                ", hasCollected=" + hasCollected +
                ", mockData='" + mockData + '\'' +
                ", mockDataDesc='" + mockDataDesc + '\'' +
                ", contentType='" + contentType + '\'' +
                ", script='" + script + '\'' +
                ", scriptParams='" + scriptParams + '\'' +
                ", scriptMethodName='" + scriptMethodName + '\'' +
                ", scriptType=" + scriptType +
                ", groupConfig='" + groupConfig + '\'' +
                ", gitProjectId='" + gitProjectId + '\'' +
                ", gitToken='" + gitToken + '\'' +
                ", gitPath='" + gitPath + '\'' +
                ", gitBranch='" + gitBranch + '\'' +
                ", commit='" + commit + '\'' +
                ", dsIds='" + dsIds + '\'' +
                ", filterParams='" + filterParams + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ApiInfoDetail that = (ApiInfoDetail) o;
        return allowCache == that.allowCache &&
                allowLog == that.allowLog &&
                allowMock == that.allowMock &&
                allowScript == that.allowScript &&
                allowCors == that.allowCors &&
                allowToken == that.allowToken &&
                offline == that.offline &&
                allowAuth == that.allowAuth &&
                allowIpAntiBrush == that.allowIpAntiBrush &&
                allowUidAntiBrush == that.allowUidAntiBrush &&
                allowPreview == that.allowPreview &&
                useQpsLimit == that.useQpsLimit &&
                hasCollected==that.hasCollected &&
                scriptType == that.scriptType &&
                Objects.equals(mockData, that.mockData) &&
                Objects.equals(mockDataDesc, that.mockDataDesc) &&
                Objects.equals(contentType, that.contentType) &&
                Objects.equals(script, that.script) &&
                Objects.equals(scriptParams, that.scriptParams) &&
                Objects.equals(scriptMethodName, that.scriptMethodName) &&
                Objects.equals(groupConfig, that.groupConfig) &&
                Objects.equals(gitProjectId, that.gitProjectId) &&
                Objects.equals(gitToken, that.gitToken) &&
                Objects.equals(gitPath, that.gitPath) &&
                Objects.equals(gitBranch, that.gitBranch) &&
                Objects.equals(commit, that.commit) &&
                Objects.equals(dsIds, that.dsIds) &&
                Objects.equals(filterParams, that.filterParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), allowCache, allowLog, allowMock, allowScript, allowCors, allowToken, offline, allowAuth, allowIpAntiBrush, allowUidAntiBrush, allowPreview, useQpsLimit, mockData, mockDataDesc, contentType, script, scriptParams, scriptMethodName, scriptType, groupConfig, gitProjectId, gitToken, gitPath, gitBranch, commit, dsIds, filterParams);
    }

}
