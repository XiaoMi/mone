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
package com.xiaomi.mone.log.common;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_MULTI;

/**
 * @author milog
 */
@Slf4j
public class PathUtils {

    /**
     * 最小目录层级数
     */
    private static final int MINIMUM_LEVELS = 3;
    /**
     * 目录通配符
     */
    public static final String PATH_WILDCARD = "*";

    public static final String MULTI_FILE_PREFIX = "(";
    public static final String MULTI_FILE_SUFFIX = ")";
    private static final String NEO_FILE_PREFIX = "/home/work/logs/neo-logs/(";
    public static final String SPLIT_VERTICAL_LINE = "\\|";

//    public static final String SEPARATOR = FileSystems.getDefault().getSeparator();

    public static final String SEPARATOR = "/";

    /**
     * 用于解析倒数第二级目录通配符 example:/home/work/log/xapp/ * /server.log
     *
     * @param origPath
     * @return
     */
    public static List<String> parseLevel5Directory(String origPath) {
        ArrayList<String> pathList = Lists.newArrayList();
        if (StringUtils.isEmpty(origPath)) {
            return pathList;
        }
        String[] pathArray = origPath.split(",");
        for (String path : pathArray) {
            path = pathTrim(path);
            String basePath = path.substring(0, path.lastIndexOf(SEPARATOR));
            String fileName = path.substring(path.lastIndexOf(SEPARATOR) + 1);
            String[] fileArray = path.split(SEPARATOR);
            if (fileArray.length < MINIMUM_LEVELS) {
                //todo 是否抛异常
                pathList.add(path);
            } else {
                String fixedBasePath = basePath.substring(0, basePath.lastIndexOf(SEPARATOR));
                String regexPattern = basePath.substring(basePath.lastIndexOf(SEPARATOR) + 1);
                if (StringUtils.startsWith(fixedBasePath, NEO_FILE_PREFIX)) {
                    regexPattern = MULTI_FILE_PREFIX + StringUtils.substringBetween(fixedBasePath, MULTI_FILE_PREFIX, MULTI_FILE_SUFFIX) + MULTI_FILE_SUFFIX;
                    fixedBasePath = NEO_FILE_PREFIX.substring(0, NEO_FILE_PREFIX.length() - 2);
                }
                // *通配 读取多级目录
                if (regexPattern.trim().equals(PATH_WILDCARD)) {
                    try {
                        readFile(fixedBasePath, fileName, pathList);
                    } catch (FileNotFoundException e) {
                        log.error("[PathUtils.ParseLevel5Directory] file[{}] not found err:", basePath + fileName, e);
                    } catch (Exception e) {
                        log.error("[PathUtils.ParseLevel5Directory] path:[{}],err:", basePath + fileName, e);
                    }
                } else if (basePath.contains(MULTI_FILE_PREFIX) && basePath.contains(MULTI_FILE_SUFFIX) && !fileName.contains(PATH_WILDCARD)) {
                    String multiDirectories = StringUtils.substringBetween(basePath, MULTI_FILE_PREFIX, MULTI_FILE_SUFFIX);
                    String directoryPrefix = StringUtils.substringBefore(basePath, MULTI_FILE_PREFIX);
                    String directorySuffix = StringUtils.substringAfter(basePath, MULTI_FILE_SUFFIX);
                    handleMultiDirectories(multiDirectories, fileName, directoryPrefix, directorySuffix, SEPARATOR, pathList);
                } else if (fileName.contains(PATH_WILDCARD)) {
                    //匹配很多个文件
                    handleMultipleDirectoryFile(basePath, fileName, pathList);
                } else {
                    if (origPath.contains(PATH_WILDCARD)) {
                        basePath = StringUtils.substringBefore(origPath, PATH_WILDCARD);
                        String fileSuffix = StringUtils.substringAfter(origPath, PATH_WILDCARD);
                        try {
                            readFile(basePath, fileSuffix, pathList);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                    for (String serverName : fileName.split(SPLIT_VERTICAL_LINE)) {
                        pathList.add(basePath + SEPARATOR + serverName);
                    }
                }
            }
        }
        return pathList;
    }

    public static List<String> findDirectoryWithMultiple(String patternStr, Boolean collectOnce) {
        List<String> directories = Lists.newArrayList();
        if (patternStr.contains(MULTI_FILE_PREFIX) && patternStr.contains(MULTI_FILE_SUFFIX)) {
            String multiDirectories = StringUtils.substringBetween(patternStr, MULTI_FILE_PREFIX, MULTI_FILE_SUFFIX);
            String directoryPrefix = StringUtils.substringBefore(patternStr, MULTI_FILE_PREFIX);
            for (String directory : multiDirectories.split(SPLIT_VERTICAL_LINE)) {
                String basePathSingle = directoryPrefix + directory;
                directories.add(basePathSingle);
            }
        } else {
            directories.add(StringUtils.substringBeforeLast(patternStr, SEPARATOR));
        }
        return directories;
    }

    private static void handleMultipleDirectoryFile(String basePath, String fileNamePattern, List<String> pathList) {
        if (basePath.contains(MULTI_FILE_PREFIX) && basePath.contains(MULTI_FILE_SUFFIX)) {
            String multiDirectories = StringUtils.substringBetween(basePath, MULTI_FILE_PREFIX, MULTI_FILE_SUFFIX);
            String directoryPrefix = StringUtils.substringBefore(basePath, MULTI_FILE_PREFIX);
            String directorySuffix = StringUtils.substringAfter(basePath, MULTI_FILE_SUFFIX);
            for (String directory : multiDirectories.split(SPLIT_VERTICAL_LINE)) {
                String basePathSingle = directoryPrefix + directory + directorySuffix;
                pathList.addAll(findRulePatternFiles(basePathSingle, fileNamePattern));
            }
        } else {
            pathList.addAll(findRulePatternFiles(basePath, fileNamePattern));
        }

    }

    private static void handleMultiDirectories(String multiDirectories, String fileName, String directoryPrefix,
                                               String directorySuffix, String separator, List<String> pathList) {
        for (String directory : multiDirectories.split(SPLIT_VERTICAL_LINE)) {
            if (fileName.contains(MULTI_FILE_PREFIX) || fileName.contains(MULTI_FILE_SUFFIX)) {
                for (String singleFileName : StringUtils.substringBetween(fileName, MULTI_FILE_PREFIX, MULTI_FILE_SUFFIX).split(SPLIT_VERTICAL_LINE)) {
                    pathList.add(directoryPrefix + directory + directorySuffix + separator + singleFileName);
                }
            } else {
                for (String singleFileName : fileName.split(SPLIT_VERTICAL_LINE)) {
                    pathList.add(directoryPrefix + directory + directorySuffix + separator + singleFileName);
                }
            }
        }
    }

    /**
     * 返回监控文件夹列表
     *
     * @param origPath
     * @return`
     */
    public static List<String> parseWatchDirectory(String origPath) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isEmpty(origPath)) {
            return result;
        }
        String[] pathArray = origPath.split(",");
        for (String path : pathArray) {
            path = pathTrim(path);
            String basePath = path.substring(0, path.lastIndexOf(SEPARATOR));

            String fixedBasePath = basePath.substring(0, basePath.lastIndexOf(SEPARATOR));
            String regexPattern = basePath.substring(basePath.lastIndexOf(SEPARATOR) + 1);

            if (StringUtils.startsWith(fixedBasePath, NEO_FILE_PREFIX)) {
                regexPattern = MULTI_FILE_PREFIX + StringUtils.substringBetween(fixedBasePath, MULTI_FILE_PREFIX, MULTI_FILE_SUFFIX) + MULTI_FILE_SUFFIX;
                fixedBasePath = NEO_FILE_PREFIX.substring(0, NEO_FILE_PREFIX.length() - 2);
            }

            if (regexPattern.trim().equals(PATH_WILDCARD)) {
                result.add(fixedBasePath);
            } else if (regexPattern.startsWith(MULTI_FILE_PREFIX) && regexPattern.endsWith(MULTI_FILE_SUFFIX)) {
                String patterns = regexPattern.substring(1, regexPattern.length() - 1);
                String[] patternArr = patterns.split(SPLIT_VERTICAL_LINE);
                String originFilePrefix = fixedBasePath + SEPARATOR + regexPattern;
                //适配正则右侧剩余目录 /home/work/logs/neo-logs/(xxx|yy)/zz/server.log => 加 /zz
                String regexRightPath = path.substring(originFilePrefix.length() + 1);
                String rightDir = "";
                if (regexRightPath.split(SEPARATOR).length > 1) {
                    rightDir = regexRightPath.substring(0, regexRightPath.lastIndexOf(SEPARATOR));
                    rightDir = SEPARATOR + Arrays.stream(rightDir.split(SEPARATOR)).findFirst().get();
                }
                for (String p : patternArr) {
                    String watchPath = fixedBasePath + SEPARATOR + p + rightDir;
                    result.add(watchPath);
                }
            } else if (basePath.contains(MULTI_FILE_PREFIX) && basePath.contains(MULTI_FILE_SUFFIX)) {
                String multiDirectories = StringUtils.substringBetween(basePath, MULTI_FILE_PREFIX, MULTI_FILE_SUFFIX);
                String directoryPrefix = StringUtils.substringBefore(basePath, MULTI_FILE_PREFIX);
                String directorySuffix = StringUtils.substringAfter(basePath, MULTI_FILE_SUFFIX);
                for (String directory : multiDirectories.split(SPLIT_VERTICAL_LINE)) {
                    result.add(directoryPrefix + directory + directorySuffix);
                }
            } else {
                result.add(basePath);
            }
        }
        return result.stream().distinct().collect(Collectors.toList());
    }

    private static String pathTrim(String path) {
        path = path.replaceAll("//", "/");
        return path;
    }

    private static void readFile(String filepath, String fileName, List<String> list) throws FileNotFoundException, IOException {
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                return;
            } else if (file.isDirectory()) {
                String[] fileList = file.list();
                for (int i = 0; i < fileList.length; i++) {
                    String subPath;
                    if (filepath.endsWith("/")) {
                        subPath = filepath + fileList[i];
                    } else {
                        subPath = filepath + "/" + fileList[i];
                    }

                    File subFile = new File(subPath);
                    if (!subFile.isDirectory() && StringUtils.equals(subFile.getName(), fileName)) {
                        list.add(subFile.getPath());
                    } else if (subFile.isDirectory()) {
                        readFile(subPath, fileName, list);
                    }
                }

            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        return;
    }

    private static void readFile(String filepath, String fileName, String dictionaries, List<String> list) throws FileNotFoundException, IOException {
        try {
            File file = new File(filepath);
            if (!file.isDirectory()) {
                return;
            } else if (file.isDirectory()) {
                String[] fileList = file.list();
                for (int i = 0; i < fileList.length; i++) {
                    String subPath;
                    if (filepath.endsWith("/")) {
                        subPath = filepath + fileList[i];
                    } else {
                        subPath = filepath + "/" + fileList[i];
                    }

                    File subFile = new File(subPath);
                    if (!subFile.isDirectory() && StringUtils.equals(subFile.getName(), fileName)) {
                        list.add(subFile.getPath());
                    } else if (subFile.isDirectory()) {
                        readFile(subPath, fileName, list);
                    }
                }

            }
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        return;
    }


    public static List<String> findRulePatternFiles(String directory, String patternStr) {
        File file = new File(directory);
        if (!patternStr.contains(SYMBOL_MULTI)) {
            patternStr = patternStr.replaceAll("\\*", SYMBOL_MULTI);
        }
        if (patternStr.startsWith(PATH_WILDCARD)) {
            patternStr = patternStr.replaceFirst("\\*", SYMBOL_MULTI);
        }
        Pattern compile = Pattern.compile(patternStr);
        if (file.isDirectory()) {
            return Arrays.stream(file.list())
                    .filter(name -> compile.matcher(name).matches())
                    .map(s -> String.format("%s%s%s", directory, SEPARATOR, s))
                    .collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * /home/work/log/log-agent/server.log.*
     * /logSplitExpress:/home/work/log/log-agent/(server.log.*|error.log.*)
     * /logSplitExpress:/home/work/log/(log-agent|log-stream)/(a|b)/server.log.*
     * 清晰带符号的路径
     *
     * @param originStr
     * @return
     */
    public static void dismantlingStrWithSymbol(String originStr, List<String> cleanedPathList) {
        if (StringUtils.isBlank(originStr)) {
            return;
        }
        String pathPrefix = StringUtils.substringBefore(originStr, MULTI_FILE_PREFIX);
        String betweenStr = StringUtils.substringBetween(originStr, MULTI_FILE_PREFIX, MULTI_FILE_SUFFIX);
        if (StringUtils.isBlank(betweenStr)) {
            cleanedPathList.add(originStr);
            return;
        }
        String pathSuffix = StringUtils.substringAfter(originStr, MULTI_FILE_SUFFIX);

        if (StringUtils.isNotBlank(betweenStr)) {
            String[] directories = StringUtils.split(betweenStr, SPLIT_VERTICAL_LINE);
            for (String perDirectory : directories) {
                String realPath = String.format("%s%s%s", pathPrefix, perDirectory, pathSuffix);
                dismantlingStrWithSymbol(realPath, cleanedPathList);
            }
        }
    }
}
