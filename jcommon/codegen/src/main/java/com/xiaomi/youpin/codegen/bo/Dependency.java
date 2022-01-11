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

package com.xiaomi.youpin.codegen.bo;

import java.util.HashMap;

public class Dependency {
    private final HashMap<String, String> dep;

    public Dependency(HashMap<String, String> dep) {
        this.dep = dep;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("        <dependency>\n");
        for (String k : this.dep.keySet()) {
            String v = dep.get(k);
            sb.append(buildXMLLine(k, v));
        }
        sb.append("        </dependency>\n");
        return sb.toString();
    }

    private String buildXMLLine(String tag, String data) {
        return "            <" + tag + ">" + data + "</" + tag + ">\n";
    }
}
