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

package com.xiaomi.youpin.codecheck;


import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocCheck implements Serializable {

    public Map<String, String> getDoc(String path) {
        Map<String, String> res = new HashMap<>();

        if (path == null || path.equals("")) {
            return res;
        }

        //xxx.java校验
        List<File> files = CommonUtils.searchFiles(new File(path), ".java");
        files.stream().forEach(it -> {
//            String javaDocRes = JavaDocReader.getDoc(it.getPath());
//            if (!javaDocRes.equals("")) {
//                res.put(it.getPath(), javaDocRes);
//            }
        });

        return res;
    }
}
