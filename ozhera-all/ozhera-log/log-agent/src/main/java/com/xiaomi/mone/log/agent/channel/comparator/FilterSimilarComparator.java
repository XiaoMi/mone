/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.agent.channel.comparator;

import com.xiaomi.mone.log.api.model.meta.FilterConf;

import java.util.List;

public class FilterSimilarComparator implements SimilarComparator<List<FilterConf>> {
    private List<FilterConf> filterConf;

    public FilterSimilarComparator(List<FilterConf> confs) {
        this.filterConf = confs;
    }

    @Override
    public boolean compare(List<FilterConf> confs) {
        if (confs == null && filterConf == null) {
            return true;
        } else if (confs != null && filterConf != null) {
            if (confs.size() != filterConf.size()) {
                return false;
            }
            return confs.equals(filterConf);
        }
        return false;
    }
}
