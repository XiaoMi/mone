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

package com.xiaomi.youpin.gwdash.service;

import com.alibaba.nacos.client.utils.StringUtils;
import com.google.gson.Gson;
import com.xiaomi.youpin.gwdash.bo.DataMessage;
import com.xiaomi.youpin.gwdash.bo.VulcanusData;
import com.xiaomi.youpin.gwdash.common.LogFile;
import com.xiaomi.youpin.gwdash.rocketmq.CodeCheckerHandler;
import com.xiaomi.youpin.gwdash.rocketmq.CompileHandler;
import com.xiaomi.youpin.gwdash.rocketmq.RocketMQConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class LogService {

    public static final String ProjectCodeCheck = "ProjectCodeCheck";
    public static final String ProjectCompilation = "ProjectCompilation";
    public static final String ProjectDockerBuild = "ProjectDockerBuild";
    public static final String ProjectDeployment = "ProjectDeployment";

    private static ConcurrentHashMap<String, LogFile> logCache = new ConcurrentHashMap<>();
    private final String LogPath = "/tmp/gwdash/mione/";
    private final Long SECONDS_TO_SCHEDULED_SAVE_LOG = 30 * 1000L;

    @Value("${cicd.log.group}")
    private String logGroup;

    @PostConstruct
    public void init() {
        File file = new File(LogPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 获取日志
     *
     * @param id   日志id
     * @param type 日志类型
     */
    public String getLog(final String type, final long id) {
        if (StringUtils.isEmpty(type)) {
            return "";
        }
        final String uniKey = generateUniKey(type, id);
        StringBuffer sb = new StringBuffer();
        sb.append(loadLogFromFile(uniKey));
        sb.append(loadLogFromCache(uniKey));
        return sb.toString();
    }

    /**
     *
     * 存储日志
     *
     * @param type
     * @param id
     * @param msg
     */
    public void saveLog(final String type, final long id, final String msg) {
        if (StringUtils.isEmpty(type) || StringUtils.isEmpty(msg)) {
            return;
        }
        final String uniKey = generateUniKey(type, id);
        LogFile logFile = logCache.get(uniKey);
        if (null == logFile) {
            logCache.put(uniKey, new LogFile());
        }
        logCache.get(uniKey).write(msg);
        pushLog(type, id, msg);
    }

    private void pushLog (final String type, final long id, final String msg) {
        Gson gson = new Gson();
        DataMessage msgLog = new DataMessage();
        if (ProjectCodeCheck.equals(type)) {
            msgLog.setMsgType(CodeCheckerHandler.CODE_CHECK_LOGS);
            msgLog.setData(gson.toJson(VulcanusData.builder().id(id).message(msg).build()));
            CodeCheckerService.pushMsg(id, gson.toJson(msgLog));
        } else if (ProjectCompilation.equals(type)) {
            msgLog.setMsgType(CompileHandler.COMPILE_LOGS);
            msgLog.setData(gson.toJson(VulcanusData.builder().id(id).message(msg).build()));
            ProjectCompilationService.pushMsg(id, new Gson().toJson(msgLog));
        } else if (ProjectDockerBuild.equals(type)) {
            msgLog.setMsgType(CompileHandler.DOCKER_BUILD_LOGS);
            msgLog.setData(gson.toJson(VulcanusData.builder().id(id).message(msg).build()));
            ProjectCompilationService.pushMsg(id, new Gson().toJson(msgLog));
        }
    }

    private String loadLogFromCache(final String uniKey) {
        LogFile logFile = logCache.get(uniKey);
        return null == logFile ? "" : logFile.read();
    }

    private String loadLogFromFile(final String uniKey) {
        final String fileName = findFileByKey(uniKey);
        if (fileName == null) {
            return "";
        }
        try {
            return FileUtils.readFileToString(new File(LogPath + fileName));
        } catch (IOException e) {
            log.error("error occurs when read from " + LogPath + fileName);
        }
        return "";
    }

    private String findFileByKey(String key) {
        File file = new File(LogPath);
        if (!file.exists() || !file.isDirectory()) {
            return null;
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        for (File e : files) {
            if (e.getName().endsWith(key)) {
                return e.getName();
            }
        }
        return null;
    }

    private String generateUniKey(String type, Long id) {
        return logGroup + "-" + type + "-" + id;
    }

    private String generateLogName(String key) {
        LocalDate now = LocalDate.now();
        return now.getYear() + "-" + String.format("%02d", now.getMonthValue()) + "-" + String.format("%02d", now.getDayOfMonth()) + "-" + key;
    }

    /**
     * 每分钟检查是否有日志应该存档
     * 存档条件： 距离上次更新时间超过1分钟
     */
    @Scheduled(cron = "* 0/1 * * * ?")
    public void scheduledSave() {
        if (logCache.size() > 0) {
            logCache.forEach((k, v) -> {
                if (System.currentTimeMillis() - v.getTime() > SECONDS_TO_SCHEDULED_SAVE_LOG) {
                    logCache.remove(k);
                    saveLogToFile(k, v.read().toString());
                }
            });
        }
    }

    private void saveLogToFile(String key, String msg) {
        String fileName = generateLogName(key);
        File file = new File(LogPath + fileName);
        boolean append = false;
        if (file.exists()) {
            append = true;
        }
        try {
            FileWriter writer = new FileWriter(file, append);
            writer.write(msg);
            writer.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 每天1点删除7天前的文件
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void scheduledDelete() {
        File file = new File(LogPath);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (null != files && files.length > 0) {
                LocalDate dateBefore7Days = LocalDate.now().plusDays(-6);
                String toDeleteKey = dateBefore7Days.getYear() + "-" + String.format("%02d",dateBefore7Days.getMonthValue()) + "-" + String.format("%02d",dateBefore7Days.getDayOfMonth());
                for (File e : files) {
                    if (e.getName().compareTo(toDeleteKey) <= 0) {
                        try {
                            FileUtils.forceDelete(e);
                        } catch (IOException ex) {
                            log.error(ex.getMessage());
                        }
                    }
                }
            }
        }
    }
}
