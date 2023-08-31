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

import com.xiaomi.youpin.codecheck.po.CheckResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) throws Exception {
        if (version1 == null || version2 == null) {
            throw new Exception("compareVersion error:illegal params.");
        }
        //注意此处为正则匹配，不能用"."；
        String[] versionArray1 = version1.split("\\.");
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        int minLength = Math.min(versionArray1.length, versionArray2.length);//取最小长度值
        int diff = 0;
        while (idx < minLength
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }

    public static CheckResult checkLog4jVersion(String curVersion, String targetVersion) throws Exception {
        if (curVersion == null || targetVersion == null) {
            throw new Exception("checkVersion error:illegal params.");
        }
        if (!curVersion.contains("rc")) {
            int diff = compareVersion(curVersion, targetVersion);
            if (diff >= 0) {
                return CheckResult.getInfoRes("log4j", "", "");
            }
        }
        String[] curVersionArrayByDot = curVersion.split("\\.");
        //2.x.x版本必须是2.15.0-rc2以上
        if ("2".equals(curVersionArrayByDot[0])) {
            if (curVersion.contains("-")) {
                String[] curVersionArrayByRod = curVersion.split("-");
                if (curVersionArrayByRod.length != 2) {
                    return CheckResult.getErrorRes("log4j", "unknown version of log4j,please update your log4j version", "无法解析的log4j版本");
                }
                //类似2.15.1-rc1
                int diff = compareVersion(curVersionArrayByRod[0],targetVersion);
                if (diff >=0){
                    return CheckResult.getInfoRes("log4j", "", "");
                }
                int version = Integer.parseInt(curVersionArrayByRod[1].substring(2));
                if (version >= 2) {
                    return CheckResult.getInfoRes("log4j", "", "");
                }
            }
            return CheckResult.getErrorRes("log4j", "warn version of log4j,please update your log4j version", "log4j版本安全漏洞，请立即更新至2.15.0-rc2以上");
        }
        return CheckResult.getErrorRes("log4j", "unknown version of log4j,please update your log4j version", "无法解析的log4j版本");
    }

    public static List<File> searchFiles(File folder, final String keyword) {
        List<File> result = new ArrayList<File>();
        if (folder.isFile()) {
            result.add(folder);
        }
        File[] subFolders = folder.listFiles(
                (File file) -> {
                    if (file.isDirectory()) {
                        return true;
                    }
                    if (file.getName().toLowerCase().endsWith(keyword)) {
                        return true;
                    }
                    return false;
                }
        );
        if (subFolders != null) {
            for (File file : subFolders) {
                if (file.isFile()) {
                    // 如果是文件则将文件添加到结果列表中
                    result.add(file);
                } else {
                    // 如果是文件夹，则递归调用本方法，然后把所有的文件加到结果列表中
                    result.addAll(searchFiles(file, keyword));
                }
            }
        }
        return result;
    }

    public static List<File> searchFiles(File folder, final List<String> keywords) {
        List<File> result = new ArrayList<>();
        if (folder.isFile()) {
            result.add(folder);
        }
        File[] subFolders = folder.listFiles(
                (File file) -> {
                    if (file.isDirectory()) {
                        return true;
                    }
                    return anyEndWith(file.getName().toLowerCase(), keywords);
                }
        );
        if (subFolders != null) {
            for (File file : subFolders) {
                if (file.isFile()) {
                    // 如果是文件则将文件添加到结果列表中
                    result.add(file);
                } else {
                    // 如果是文件夹，则递归调用本方法，然后把所有的文件加到结果列表中
                    result.addAll(searchFiles(file, keywords));
                }
            }
        }
        return result;
    }

    public static List<File> searchNoFiles(File folder, final List<String> keywords) {
        List<File> result = new ArrayList<>();
        if (folder.isFile()) {
            result.add(folder);
        }
        File[] subFolders = folder.listFiles(
                (File file) -> {
                    if (file.isDirectory()) {
                        return true;
                    }
                    return noneEndWith(file.getName().toLowerCase(), keywords);
                }
        );
        if (subFolders != null) {
            for (File file : subFolders) {
                if (file.isFile()) {
                    // 如果是文件则将文件添加到结果列表中
                    result.add(file);
                } else {
                    // 如果是文件夹，则递归调用本方法，然后把所有的文件加到结果列表中
                    result.addAll(searchNoFiles(file, keywords));
                }
            }
        }
        return result;
    }

    private static boolean anyEndWith(String fileName, List<String> keywords){
        if(fileName == null || keywords == null || keywords.isEmpty()){
            return false;
        }
        return keywords.stream().anyMatch(fileName::endsWith);
    }

    private static boolean noneEndWith(String fileName, List<String> keywords){
        if(fileName == null || keywords == null || keywords.isEmpty()){
            return false;
        }
        return keywords.stream().noneMatch(fileName::endsWith);
    }

    /**
     * 判断内容是否含有IPv4
     * @param content
     * @return
     */
    public static boolean hasIPv4(String content){
        String pattern = "\\d+[\\.]\\d+[\\.]\\d+[\\.]\\d+";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(content);
        return m.find();
    }
}
