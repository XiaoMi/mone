package run.mone.mimeter.engine.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import run.mone.mimeter.engine.agent.bo.hosts.HostBo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class HostsService {

    /**
     * 获取host文件路径
     */
    public static String getHostFile() {
        String fileName;
        // 判断系统
        if ("linux".equalsIgnoreCase(System.getProperty("os.name")) || "Mac OS X".equalsIgnoreCase(System.getProperty("os.name"))) {
            fileName = "/etc/hosts";
//            fileName = "/Users/dongzhenxing/Desktop/hosts";
        } else {
            //不支持其他系统
            return "";
        }
        return fileName;
    }

    public static List<String> loadHostFile() {
        // Step1: 获取host文件
        String fileName = getHostFile();
        List<?> hostFileDataLines = new ArrayList<>();
        try {
            hostFileDataLines = FileUtils.readLines(new File(fileName));
        } catch (IOException e) {
            log.error("Reading host file occurs error: " + e.getMessage());
        }
        return (List<String>) hostFileDataLines;
    }

    /**
     * 根据输入Domain，批量删除host文件中的host配置
     */
    public synchronized static boolean deleteDomainsConfig(List<HostBo> confList) {
        if (confList == null || confList.size() == 0) {
            return true;
        }
        confList.forEach(domainMap -> {
            if (domainMap.getDomain() == null || domainMap.getDomain().trim().isEmpty()) {
                throw new IllegalArgumentException("ERROR： ip & domain must be specified");
            }
        });
        // Step1: 获取host文件
        String fileName = getHostFile();
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("ERROR：get host file path failed");
        }
        List<?> hostFileDataLines;
        try {
            hostFileDataLines = FileUtils.readLines(new File(fileName));
        } catch (IOException e) {
            log.error("Reading host file occurs error: " + e.getMessage());
            return false;
        }
        // Step2: 解析host文件，如果指定域名不存在，则Ignore，如果已经存在，则直接删除该行配置
        List<String> newLinesList = new ArrayList<>();
        // 标识本次文件是否有更新，比如如果指定的IP和域名已经在host文件中存在，则不用再写文件
        AtomicBoolean updateFlag = new AtomicBoolean(false);

        List<String> finalHostFile = (List<String>) hostFileDataLines;

        confList.forEach(domainMap -> {
            String domain = domainMap.getDomain();
            for (Object line : finalHostFile) {
                String strLine = line.toString();
                // host文件中的空行或无效行，直接跳过
                if (StringUtils.isEmpty(strLine) || strLine.trim().equals("#")) {
                    continue;
                }
                // 如果没有被注释掉，则
                if (!strLine.trim().startsWith("#")) {
                    int index = strLine.toLowerCase().indexOf(domain.toLowerCase());
                    // 如果行字符可以匹配上指定域名，则针对该行做操作
                    if (index != -1) {
                        // 匹配到相同的域名，直接将整行数据干掉
                        updateFlag.set(true);
                        continue;
                    }
                }
                // 如果没有匹配到，直接将当前行加入代码中
                newLinesList.add(strLine);
            }
            finalHostFile.clear();
            finalHostFile.addAll(newLinesList);
            newLinesList.clear();
        });
        //Step3: 将更新后的数据写入host文件中去
        if (updateFlag.get()) {
            try {
                FileUtils.writeLines(new File(fileName), finalHostFile);
            } catch (IOException e) {
                log.error("Updating host file occurs error: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * 根据输入IP和Domain，更新host文件中的某个host配置
     */
    public synchronized static boolean updateHostConfig(List<HostBo> confList) {
        if (confList == null || confList.size() == 0) {
            return true;
        }
        // Step1: 获取host文件
        String fileName = getHostFile();
        List<?> hostFileDataLines;
        List<String> finalHostFile;
        try {
            hostFileDataLines = FileUtils.readLines(new File(fileName));
        } catch (IOException e) {
            log.error("Reading host file occurs error: " + e.getMessage());
            return false;
        }
        finalHostFile = (List<String>) hostFileDataLines;
        //Step2: 解析host文件，如果指定域名不存在，则追加，如果已经存在，则修改IP进行保存
        List<String> newLinesList = new ArrayList<>();
        // 指定domain是否存在，如果存在，则不追加
        final boolean[] findFlag = {false};
        // 标识本次文件是否有更新，比如如果指定的IP和域名已经在host文件中存在，则不用再写文件
        final AtomicBoolean[] updateFlag = {new AtomicBoolean(false)};
        List<String> finalHostFile1 = finalHostFile;
        confList.forEach(domainMap -> {
            for (Object line : finalHostFile1) {
                String strLine = line.toString();
                // 将host文件中的空行或无效行，直接去掉
                if (StringUtils.isEmpty(strLine) || strLine.trim().equals("#")) {
                    continue;
                }
                if (!strLine.startsWith("#")) {
                    int index = strLine.toLowerCase().indexOf(domainMap.getDomain().toLowerCase());
                    // 如果行字符可以匹配上指定域名，则针对该行做操作
                    if (index != -1) {
                        // 如果之前已经找到过一条，则说明当前line的域名已重复，
                        // 故删除当前line, 不将该条数据放到newLinesList中去
                        if (findFlag[0]) {
                            updateFlag[0].set(true);
                            continue;
                        }
                        // 否则，继续寻找
                        String[] array = strLine.trim().split(" ");
                        //遍历域名
                        for (int i = 1; i < array.length; i++) {
                            if (domainMap.getDomain().equalsIgnoreCase(array[i])) {
                                findFlag[0] = true;
                                // IP相同，则不更新该条数据，直接将数据放到newLinesList中去
                                if (!array[0].equals(domainMap.getIp())) {
                                    // IP不同，将匹配上的domain的ip 更新成设定好的IP地址
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(domainMap.getIp());
                                    for (int j = 1; i < array.length; i++) {
                                        //array[j] 域名
                                        sb.append(" ").append(array[j]);
                                    }
                                    strLine = sb.toString();
                                    updateFlag[0].set(true);
                                }
                            }
                        }
                    }
                }
                // 如果有更新，则会直接更新到strLine中去
                // 故这里直接将strLine赋值给newLinesList
                newLinesList.add(strLine);
            }
            // Step3: 如果没有任何Host域名匹配上，则追加
            if (!findFlag[0]) {
                newLinesList.add(domainMap.getIp() + " " + domainMap.getDomain());
            }
            finalHostFile1.clear();
            finalHostFile1.addAll(newLinesList);
            newLinesList.clear();
            findFlag[0] = false;
        });

        //Step4: 写设定文件
        if (updateFlag[0].get() || !findFlag[0]) {
            try {
                FileUtils.writeLines(new File(fileName), finalHostFile1);
            } catch (IOException e) {
                log.error("Updating host file occurs error: " + e.getMessage());
                return false;
            }
        }
        return true;
    }
}
