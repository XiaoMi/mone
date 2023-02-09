
package com.xiaomi.miapi.vo;

import java.io.Serializable;
import java.util.Objects;

public class BusProjectVo implements Serializable {
    private int id;
    private String name;
    private String description;
    private long ctime;
    private long utime;
    private int status;
    private String gitAddress;
    private int version;
    private String gitGroup;
    private String gitName;
    private boolean isFocus;
    private int apiCount;
    private String projectUpdateTime;


    public BusProjectVo() {
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public long getCtime() {
        return this.ctime;
    }

    public long getUtime() {
        return this.utime;
    }

    public int getStatus() {
        return this.status;
    }

    public String getGitAddress() {
        return this.gitAddress;
    }

    public int getVersion() {
        return this.version;
    }

    public String getGitGroup() {
        return this.gitGroup;
    }

    public String getGitName() {
        return this.gitName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public void setUtime(long utime) {
        this.utime = utime;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setGitAddress(String gitAddress) {
        this.gitAddress = gitAddress;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setGitGroup(String gitGroup) {
        this.gitGroup = gitGroup;
    }

    public void setGitName(String gitName) {
        this.gitName = gitName;
    }

    public boolean isFocus() {
        return isFocus;
    }

    public void setFocus(boolean focus) {
        isFocus = focus;
    }

    public int getApiCount() {
        return apiCount;
    }

    public void setApiCount(int apiCount) {
        this.apiCount = apiCount;
    }

    public String getProjectUpdateTime() {
        return projectUpdateTime;
    }

    public void setProjectUpdateTime(String projectUpdateTime) {
        this.projectUpdateTime = projectUpdateTime;
    }

    protected boolean canEqual(Object other) {
        return other instanceof BusProjectVo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusProjectVo that = (BusProjectVo) o;
        return id == that.id &&
                ctime == that.ctime &&
                utime == that.utime &&
                status == that.status &&
                version == that.version &&
                isFocus == that.isFocus &&
                apiCount == that.apiCount &&
                Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(gitAddress, that.gitAddress) &&
                Objects.equals(gitGroup, that.gitGroup) &&
                Objects.equals(gitName, that.gitName) &&
                Objects.equals(projectUpdateTime, that.projectUpdateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, ctime, utime, status, gitAddress, version, gitGroup, gitName, isFocus, apiCount, projectUpdateTime);
    }

    @Override
    public String toString() {
        return "BusProjectVo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", ctime=" + ctime +
                ", utime=" + utime +
                ", status=" + status +
                ", gitAddress='" + gitAddress + '\'' +
                ", version=" + version +
                ", gitGroup='" + gitGroup + '\'' +
                ", gitName='" + gitName + '\'' +
                ", isFocus=" + isFocus +
                ", apiCount=" + apiCount +
                ", projectUpdateTime='" + projectUpdateTime + '\'' +
                '}';
    }
}
