package com.xiaomi.youpin.gwdash.bo;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author jiangzheng
 * @version 1.0
 * @description: groupList DTO对象
 */
public class ApiGroupInfoListResultDTO implements Serializable {


    private int total;

    private List<ApiGroupInfoDTO> groupList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ApiGroupInfoDTO> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<ApiGroupInfoDTO> groupList) {
        this.groupList = groupList;
    }

    @Override
    public String toString() {
        return "ApiGroupInfoListResult{" +
                "total=" + total +
                ", groupList=" + groupList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiGroupInfoListResultDTO that = (ApiGroupInfoListResultDTO) o;
        return total == that.total &&
                Objects.equals(groupList, that.groupList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, groupList);
    }

}
