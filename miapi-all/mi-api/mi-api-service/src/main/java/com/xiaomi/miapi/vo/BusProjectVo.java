
package com.xiaomi.miapi.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
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
