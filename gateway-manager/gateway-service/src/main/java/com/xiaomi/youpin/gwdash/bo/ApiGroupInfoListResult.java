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

import com.xiaomi.youpin.gwdash.dao.model.ApiGroupInfo;

import java.util.List;
import java.util.Objects;

public class ApiGroupInfoListResult {

    private int total;

    private List<ApiGroupInfo> groupList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<ApiGroupInfo> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<ApiGroupInfo> groupList) {
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
        ApiGroupInfoListResult that = (ApiGroupInfoListResult) o;
        return total == that.total &&
                Objects.equals(groupList, that.groupList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, groupList);
    }
}
