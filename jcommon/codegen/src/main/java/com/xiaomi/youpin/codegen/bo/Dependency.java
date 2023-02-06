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
