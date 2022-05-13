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

package com.xiaomi.data.push.antlr.sql.util;

import com.xiaomi.data.push.antlr.sql.constants.Constants;

public class StringPairUtils {


    public static Pair<String, String> getPointPair(String content) {
        return getPair(Constants.POINT, content, false);
    }

    public static Pair<String, String> getLastPointPair(String content) {
        return getPair(Constants.POINT, content, true);
    }

    private static Pair<String, String> getPair(String split, String content, boolean dir) {
        int index;
        if (dir) {
            index = content.lastIndexOf(Constants.POINT);
        } else {
            index = content.indexOf(Constants.POINT);
        }
        if (index == -1) {
            throw new RuntimeException("not contain . character:" + content);
        }
        String left = content.substring(0, index);
        String right = content.substring(index + 1);
        return Pair.of(left, right);
    }

    public static String getLastPoint(String split, String content) {
        int index = content.lastIndexOf(split);
        if (index < 0) {
            return content;
        } else {
            return content.substring(index + 1);
        }
    }

    public static boolean isNotBlank(String str) {
        if (null == str) {
            return false;
        }

        return str.trim().length() > 0;
    }
}
