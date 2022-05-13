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

package com.xiaomi.mone.file;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/8 15:57
 */
@Data
public class MLog {

    private Queue<String> error = new LinkedList<>();

    private Queue<String> msgQueue = new LinkedList<>();

    /**
     * 匹配 20xx or [20xx
     */
    private static final Pattern NEW_LINE_PATTERN = Pattern.compile("^20[0-9]{2}|^\\[20[0-9]{2}");
    private static final Pattern ERROR_LINE_PATTERN = Pattern.compile(".*ERROR|.*(WARN|INFO).*(Exception|exception|error|Profiler)");
    private static final int MAX_ERROR_LINE_MATCH_LENGTH = 700;
    /**
     * 最多聚合200行错误栈，避免queue无限增长
     */
    private static final int MAX_MERGE_LINE = 200;

    public List<String> append(String msg) {
        List<String> res = new ArrayList<>();
        boolean isNew = isNew(msg);
        boolean isError = isError(msg);
        if (isNew && !isError) {
            if (msgQueue.size() > 0) {
                String e = msgQueue.stream().collect(Collectors.joining("\r\n"));
                res.add(e);
                msgQueue.clear();
            }
            msgQueue.offer(msg);
        }
        if (!isNew && msgQueue.size() > 0) {
            msgQueue.offer(msg);
        }
        if (isNew && isError(msg)) {
            if (error.size() > 0) {
                String e = error.stream().collect(Collectors.joining("\r\n"));
                error.clear();
                res.add(e);
            }
            error.offer(msg);
        }
        if (!isNew && error.size() > 0) {
            error.offer(msg);
        }
        return res;
    }


    private boolean isError(String str) {
        if (str.length() > MAX_ERROR_LINE_MATCH_LENGTH) {
            String temp = str.substring(0, MAX_ERROR_LINE_MATCH_LENGTH);
            return temp.contains("ERROR") || ((temp.contains("WARN") || temp.contains("INFO")) && (temp.contains("Exception") || temp.contains("exception") || temp.contains("error") || temp.contains("Profiler")));
        } else {
            return str.contains("ERROR") || ((str.contains("WARN") || str.contains("INFO")) && (str.contains("Exception") || str.contains("exception") || str.contains("error") || str.contains("Profiler")));
        }
    }

    private boolean isNew(String str) {
        return NEW_LINE_PATTERN.matcher(str).find();
    }


}
