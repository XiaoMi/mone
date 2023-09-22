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
package com.xiaomi.mone.log.manager.model.page;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/22 11:48
 */
@Data
@NoArgsConstructor
public class PageInfo<T> implements Serializable {
    private Integer page;
    private Integer pageSize;
    private Integer total;
    private Integer totalPageCount;
    private List<T> list;

    public static PageInfo emptyPageInfo() {
        return new PageInfo(0, 0, 0, Collections.emptyList());
    }


    public PageInfo(int page, int pageSize, int total, List<T> list) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        initPage(page, pageSize, total, list, true);
    }

    /**
     * @param page     // How many pages are current
     * @param pageSize // Per page size
     * @param total    // total
     * @param list     // Data carriers
     * @param hasPaged // Whether pagination has been made
     */
    public PageInfo(int page, int pageSize, int total, List<T> list, boolean hasPaged) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        initPage(page, pageSize, total, list, hasPaged);
    }

    private void initPage(int pageIndex, int pageSize, int totalItemCount, List<T> data, boolean hasPaged) {
        // Calculate the total number of pages
        if (totalItemCount > 0 && pageSize > 0) {
            this.totalPageCount = totalItemCount / pageSize + 1;
        }
        // Intercepts the current page data
        if (null == data) {
            this.list = new ArrayList<T>();
        } else if (data.isEmpty() || hasPaged) {
            this.list = data;
        } else {
            if (pageIndex == this.totalPageCount) { // Last page
                this.list = data.subList((pageIndex - 1) * pageSize, data.size());
            } else if (pageIndex < this.totalPageCount) { // Not the last page
                this.list = data.subList((pageIndex - 1) * pageSize, pageIndex * pageSize);
            } else { // The current page exceeds the total number of pages
                throw new IndexOutOfBoundsException("The current number of pages exceeds the total number of pages");
            }
        }
    }
}
