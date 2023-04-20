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

package com.xiaomi.mone.log.common;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author shanwb
 * @date 2021-10-20
 */
public class PathUtilsTest {

    @Test
    public void test0() {
        String logPattern = "/home/work/log/(aa|bb)/server.log";
        List<String> watches = PathUtils.parseWatchDirectory(logPattern);
        System.out.println(new Gson().toJson(watches));

        Assert.assertEquals(2, watches.size());
        Assert.assertEquals("/home/work/log/aa", watches.get(0));
        Assert.assertEquals("/home/work/log/bb", watches.get(1));
    }

    @Test
    public void test00() {
        String logPattern = "/home/work/logs/neo-logs/(a|b)/applogs/mi_com_event/mi_com_event.log";
        List<String> watches = PathUtils.parseWatchDirectory(logPattern);
        System.out.println(new Gson().toJson(watches));

        Assert.assertEquals(2, watches.size());
        Assert.assertEquals("/home/work/logs/neo-logs/a/applogs", watches.get(0));
        Assert.assertEquals("/home/work/logs/neo-logs/b/applogs", watches.get(1));
    }

    @Test
    public void test000() {
        String logPattern = "/home/work/logs/neo-logs/(a|b)/mi_com_event/mi_com_event.log";
        List<String> watches = PathUtils.parseWatchDirectory(logPattern);
        System.out.println(new Gson().toJson(watches));

        Assert.assertEquals(2, watches.size());
        Assert.assertEquals("/home/work/logs/neo-logs/a/mi_com_event", watches.get(0));
        Assert.assertEquals("/home/work/logs/neo-logs/b/mi_com_event", watches.get(1));
    }

    @Test
    public void test001() {
        String logPattern = "/home/work/logs/neo-logs/(a|b)/mi_com_event.log";
        List<String> watches = PathUtils.parseWatchDirectory(logPattern);
        System.out.println(new Gson().toJson(watches));

        Assert.assertEquals(2, watches.size());
        Assert.assertEquals("/home/work/logs/neo-logs/a", watches.get(0));
        Assert.assertEquals("/home/work/logs/neo-logs/b", watches.get(1));
    }

    @Test
    public void test01() {
        String logPattern = "/home/work/log/test1/server.log,/home/work/log/test/error.log";
        List<String> watches = PathUtils.parseWatchDirectory(logPattern);
        System.out.println(new Gson().toJson(watches));

        Assert.assertEquals(2, watches.size());
        Assert.assertEquals("/home/work/log/test1", watches.get(0));
        Assert.assertEquals("/home/work/log/test", watches.get(1));
    }

    @Test
    public void test011() {
        String logPattern = "/home/work/log/test/server.log,/home/work/log/test/error.log";
        List<String> watches = PathUtils.parseWatchDirectory(logPattern);
        System.out.println(new Gson().toJson(watches));

        Assert.assertEquals(1, watches.size());
        Assert.assertEquals("/home/work/log/test", watches.get(0));
    }

    @Test
    public void test012() {
        String logPattern = "/home/work/log/test/server.log|debug.log";
        List<String> watches = PathUtils.parseWatchDirectory(logPattern);
        System.out.println(new Gson().toJson(watches));

        Assert.assertEquals(1, watches.size());
        Assert.assertEquals("/home/work/log/test", watches.get(0));
    }

    @Test
    public void test1() {
        String logPattern = "/home/work/log/trace/*/server.log";
        List<String> watches = PathUtils.parseWatchDirectory(logPattern);
        System.out.println(new Gson().toJson(watches));

        Assert.assertEquals(1, watches.size());
        Assert.assertEquals("/home/work/log/trace", watches.get(0));
    }

    @Test
    public void test2() {
        String logPattern = "/home/work/log//trace//*//server.log";
        List<String> watches = PathUtils.parseWatchDirectory(logPattern);
        System.out.println(new Gson().toJson(watches));

        Assert.assertEquals(1, watches.size());
        Assert.assertEquals("/home/work/log/trace", watches.get(0));
    }

    @Test
    public void test71() {
        String logPattern = "/home/work/logs/applogs/mars/server.log";
        List<String> pathes = PathUtils.parseLevel5Directory(logPattern);
        System.out.println(new Gson().toJson(pathes));
    }

    @Test
    public void test72() {
        String logPattern = "/home/work/log/(aa|bb)/server.log";
        List<String> pathes = PathUtils.parseLevel5Directory(logPattern);
        System.out.println(new Gson().toJson(pathes));

        Assert.assertEquals(2, pathes.size());
        Assert.assertEquals("/home/work/log/aa/server.log", pathes.get(0));
        Assert.assertEquals("/home/work/log/bb/server.log", pathes.get(1));
    }

    @Test
    public void test721() {
        String logPattern = "/home/work/log/(aa|bb)/logs/server.log";
        List<String> pathes = PathUtils.parseLevel5Directory(logPattern);
        System.out.println(new Gson().toJson(pathes));

        Assert.assertEquals(2, pathes.size());
        Assert.assertEquals("/home/work/log/aa/logs/server.log", pathes.get(0));
        Assert.assertEquals("/home/work/log/bb/logs/server.log", pathes.get(1));
    }

    @Test
    public void test723() {
        String logPattern = "/home/work/log/(aa|bb)/logs/log1/server.log";
        List<String> pathes = PathUtils.parseLevel5Directory(logPattern);
        System.out.println(new Gson().toJson(pathes));

        Assert.assertEquals(2, pathes.size());
        Assert.assertEquals("/home/work/log/aa/logs/log1/server.log", pathes.get(0));
        Assert.assertEquals("/home/work/log/bb/logs/log1/server.log", pathes.get(1));
    }

    @Test
    public void test73() {
        String logPattern = "/home/work/log/aa/server.log";
        List<String> pathes = PathUtils.parseLevel5Directory(logPattern);
        System.out.println(new Gson().toJson(pathes));

        Assert.assertEquals(1, pathes.size());
        Assert.assertEquals("/home/work/log/aa/server.log", pathes.get(0));
    }

    @Test
    public void test722() {
        String logPattern = "/home/work/log/(aa|bb)/logs/log1/(server.log|error.log)";
        List<String> pathes = PathUtils.parseLevel5Directory(logPattern);
        System.out.println(new Gson().toJson(pathes));

        Assert.assertEquals(4, pathes.size());
        Assert.assertEquals("/home/work/log/aa/logs/log1/server.log", pathes.get(0));
        Assert.assertEquals("/home/work/log/aa/logs/log1/error.log", pathes.get(1));
        Assert.assertEquals("/home/work/log/bb/logs/log1/server.log", pathes.get(2));
        Assert.assertEquals("/home/work/log/bb/logs/log1/error.log", pathes.get(3));
    }

    @Test
    public void test74() {
        String logPattern = "/home/work/log/test/server.log|error.log";
        List<String> pathes = PathUtils.parseLevel5Directory(logPattern);
        System.out.println(new Gson().toJson(pathes));

        Assert.assertEquals(2, pathes.size());
        Assert.assertEquals("/home/work/log/test/server.log", pathes.get(0));
        Assert.assertEquals("/home/work/log/test/error.log", pathes.get(1));
    }

    @Test
    public void test741() {
        String logPattern = "/home/work/log/(test1|test2)/server.log|error.log";
        List<String> pathes = PathUtils.parseLevel5Directory(logPattern);
        System.out.println(new Gson().toJson(pathes));

        Assert.assertEquals(4, pathes.size());
        Assert.assertEquals("/home/work/log/test1/server.log", pathes.get(0));
        Assert.assertEquals("/home/work/log/test1/error.log", pathes.get(1));
        Assert.assertEquals("/home/work/log/test2/server.log", pathes.get(2));
        Assert.assertEquals("/home/work/log/test2/error.log", pathes.get(3));
    }


    @Test
    public void test81() {
        String logPattern = "/home/work/log error-2022-08-04_05_1.log";
        Pattern pattern = Pattern.compile(logPattern);

        Assert.assertEquals(true, pattern.matcher("/home/work/log/aa/server.log").matches());
        Assert.assertEquals(true, pattern.matcher("/home/work/log/bb/server.log").matches());

        Assert.assertEquals(false, pattern.matcher("/home/work/log/aa/bb/server.log").matches());
        Assert.assertEquals(false, pattern.matcher("/home/work/log/bb/cc/server.log").matches());

        Assert.assertEquals(false, pattern.matcher("/home/work/log/aaa/server.log").matches());
        Assert.assertEquals(false, pattern.matcher("/home/work/log/bbb/server.log").matches());

        Assert.assertEquals(false, pattern.matcher("/home/work/log/aa/server1.log").matches());
        Assert.assertEquals(false, pattern.matcher("/home/work/log/bb/app.log").matches());
    }

    @Test
    public void test82() {
        String logPattern = "/home/work/log/.*/server.log";
        Pattern pattern = Pattern.compile(logPattern);

        Assert.assertEquals(true, pattern.matcher("/home/work/log/aa/server.log").matches());
        Assert.assertEquals(false, pattern.matcher("/home/work/log/aa/server.log1").matches());
        Assert.assertEquals(true, pattern.matcher("/x/home/work/log/aa/server.log1").find());

        Assert.assertEquals(true, pattern.matcher("/home/work/log/aa/bb/server.log").matches());
        Assert.assertEquals(false, pattern.matcher("/home/work/log/server.log").matches());
    }

    @Test
    public void test91() {
        String logPattern = "/home/work/log/aa/server.log";
        System.out.println(logPattern.contains("*"));
        System.out.println(logPattern.replaceAll("\\*", ".*"));

        String logPattern2 = "/home/work/log/*/server.log";
        System.out.println(logPattern2.contains("*"));
        System.out.println(logPattern2.replaceAll("\\*", ".*"));

        String logPattern3 = "/home/work/log/*/server.log";
        System.out.println(logPattern3.contains(".*"));
    }

    @Test
    public void test92() {
        String logPattern = "/home/work/log/aa/server.log";
        System.out.println(logPattern.substring(0, logPattern.lastIndexOf("/")));

    }


    @Test
    public void test93() {
        String logPattern = "/home/work/log/test/server.log,/home/work/log/test/error.log";
        List<String> pathes = PathUtils.parseLevel5Directory(logPattern);
        System.out.println(new Gson().toJson(pathes));

        Assert.assertEquals(2, pathes.size());
        Assert.assertEquals("/home/work/log/test/server.log", pathes.get(0));
        Assert.assertEquals("/home/work/log/test/error.log", pathes.get(1));
    }

    @Test
    public void test94() {
        String logPattern = "/home/work/log/test/server.log|error.log";
        List<String> pathes = PathUtils.parseLevel5Directory(logPattern);
        System.out.println(new Gson().toJson(pathes));

        Assert.assertEquals(2, pathes.size());
        Assert.assertEquals("/home/work/log/test/server.log", pathes.get(0));
        Assert.assertEquals("/home/work/log/test/error.log", pathes.get(1));
    }

    @Test
    public void testLogPattern() {
        String logSplitExpress = "/home/work/log/promotion-admin/info-.*.log";
        Pattern pattern = Pattern.compile(logSplitExpress);
        Assert.assertEquals(true, pattern.matcher("/home/work/log/promotion-admin/info-2022-06-21_09_1.log").matches());
        Assert.assertEquals(true, pattern.matcher("/home/work/log/promotion-admin/info-2022-06-21_20_1.log").matches());
    }

    @Test
    public void testMultipleFile() {
        String logPattern = "/home/work/logs/applogs/graces.log|applog.log.wf";
        List<String> pathes = PathUtils.parseLevel5Directory(logPattern);
        System.out.println(new Gson().toJson(pathes));

        Assert.assertEquals(2, pathes.size());
        Assert.assertEquals("/home/work/logs/applogs/graces.log", pathes.get(0));
        Assert.assertEquals("/home/work/logs/applogs/applog.log.wf", pathes.get(1));
    }

    @Test
    public void testLogPattern1() {
        String logSplitExpress = "/home/work/logs/neo-logs/(eventapi-stable-66fd975598-z8fd9|eventapi-stable-66fd975598-zfhwq|eventapi-ams-runscript-stable-5c76f54c68-lrmcl|eventapi-stable-66fd975598-l7c4s|eventapi-stable-66fd975598-ckwwd)/applogs/mi_com_event.log-.*";
        Pattern pattern = Pattern.compile(logSplitExpress);
        Assert.assertEquals(true, pattern.matcher("/home/work/logs/neo-logs/eventapi-stable-66fd975598-z8fd9/applogs/mi_com_event.log-2022-01-11-16").matches());
        Assert.assertEquals(true, pattern.matcher("/home/work/logs/neo-logs/eventapi-ams-runscript-stable-5c76f54c68-lrmcl/applogs/mi_com_event.log-2022-01-11-16").matches());
    }

    @Test
    public void test() {
        Long str = 1648116658120L;
        String s1 = String.valueOf(1.648099046556E12);
        String s = String.valueOf(str);
        BigDecimal bd = new BigDecimal(str);
        String callBackScore = bd.toPlainString();
        System.out.println(new BigDecimal(Instant.now().toEpochMilli()).toPlainString());
        System.out.println(callBackScore);
        System.out.println(s);
        System.out.println(str.toString());
    }

    @Test
    public void testQueryRuleFiles() {
        String directory = "/home/work/log/log-manager/";
        String serverNamePattern = "server.log.*";
        System.out.println(PathUtils.findRulePatternFiles(directory, serverNamePattern));
    }

    @Test
    public void testDismantlingStrWithSymbol() {
//        String str = "/home/work/log/(a|b)/server.log.*";
//        String str = "/home/work/log/log-agent/server.log.*";
//        String str = "/home/work/log/log-agent/(server.log.*|error.log.*)";
        String str = "logSplitExpress:/home/work/log/(log-agent|log-stream)/(a|b)/server.log.*";
        List<String> cleanedPathList = Lists.newArrayList();
        PathUtils.dismantlingStrWithSymbol(str, cleanedPathList);
        cleanedPathList.stream().forEach(System.out::println);
    }
}
