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

import java.util.Objects;

public class CollectionParam {
    private  int apiInfoId;

    public int getApiInfoId() {
        return apiInfoId;
    }

    public void setApiInfoId(int apiInfoId) {
        this.apiInfoId = apiInfoId;
    }

    public CollectionParam() {
    }

    public CollectionParam(int apiInfoId) {
        this.apiInfoId = apiInfoId;
    }

    @Override
    public String toString() {
        return "CollectionParam{" +
                "apiInfoId=" + apiInfoId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CollectionParam that = (CollectionParam) o;
        return apiInfoId == that.apiInfoId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiInfoId);
    }
}
