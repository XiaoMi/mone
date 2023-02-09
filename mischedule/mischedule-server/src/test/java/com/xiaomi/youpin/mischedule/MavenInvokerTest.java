/*
 *  Copyright 2020 Xiaomi
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */


//package com.xiaomi.youpin.mischedule;
//
//import com.xiaomi.data.push.schedule.task.impl.shell.ProcessUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.tuple.Pair;
//import org.apache.maven.shared.invoker.*;
//import org.codehaus.plexus.classworlds.launcher.Launcher;
//import org.junit.Test;
//
//import java.io.*;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//import java.util.concurrent.*;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//import java.util.stream.Stream;
//
//
//@Slf4j
//public class MavenInvokerTest {
//
//
//    @Test
//    public void test2() {
//        Pair<Integer, List<String>> res = ProcessUtils.process("/tmp/build/tesla", "/Users/zhangzhiyong/programs/apache-maven-3.3.9/bin/mvn clean compile package -Dmaven.test.skip=true");
//
//        System.out.println(res.getRight().get(res.getRight().size() - 1));
//    }
//
//
//    @Test
//    public void test3() throws InterruptedException {
//        List<Integer> ls = IntStream.range(0, 1).mapToObj(it -> {
//            ProcessUtils.process(true, "/Users/zhangzhiyong/programs/apache-maven-3.3.9/bin/", (msg) -> {
//                if (msg.contains("BUILD SUCCESS")) {
//                    System.out.println("---------->");
//                }
//                System.out.println(msg);
//            }, 100, false, "/Users/zhangzhiyong/programs/apache-maven-3.3.9/bin/mvn2 clean compile package -Dmaven.test.skip=true -f /tmp/build/tesla/tesla-gateway/pom.xml");
//            return 0;
//        }).collect(Collectors.toList());
//
//        System.out.println(ls);
//
//        TimeUnit.HOURS.sleep(1);
//    }
//
//
//    @Test
//    public void test4() throws InterruptedException {
//
////        String cmd = "/Users/zhangzhiyong/programs/apache-maven-3.3.9/bin/mvn2 clean compile package -Dmaven.test.skip=true -f /tmp/build/tesla/tesla-gateway/pom.xml";
////        String cmd = "/Users/zhangzhiyong/programs/apache-maven-3.3.9/bin/mvn3";
//
//        String cmd = "/usr/bin/java";
//
//        Pair<Integer, List<String>> res = ProcessUtils.process(false, "/Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home/bin/", (msg) -> {
//            System.out.println(msg);
//        }, 0, false, cmd);
//
//        System.out.println(res);
//        System.out.println("-------------444");
//        TimeUnit.HOURS.sleep(1);
//
//
//    }
//
//
//    @Test
//    public void test5() throws IOException, InterruptedException {
//        ProcessBuilder builder = new ProcessBuilder("zzy");
//        Process process = builder.start();
//
//        InputStream is = process.getInputStream();
//        int b = 0;
//        while((b= is.read())!=-1) {
//            System.out.print((byte)b);
//        }
//
//        int code = process.waitFor();
//        System.out.println(code);
//    }
//
//
//    @Test
//    public void test() throws MavenInvocationException, InterruptedException {
//        List<Integer> res = IntStream.range(0, 1).parallel().mapToObj(it -> {
//
//            try {
//                Invoker invoker = new DefaultInvoker();
//                invoker.setLocalRepositoryDirectory(new File("/tmp/build/tesla"));
//                invoker.setMavenHome(new File("/Users/zhangzhiyong/programs/apache-maven-3.3.9"));
//
//                invoker.setOutputHandler(new InvocationOutputHandler() {
//                    @Override
//                    public void consumeLine(String line) throws IOException {
//                        System.out.println(line);
//                    }
//                });
//
//                StringBuilder buildInfo = new StringBuilder("---- build info ----" + System.lineSeparator());
//                InvocationRequest request = new DefaultInvocationRequest();
//                request.setPomFile(new File("/tmp/build/tesla/tesla-gateway/pom.xml"));
//
//                List commandList = Arrays.asList("-U", "clean", "package", "-Dmaven.test.skip=true", "-Dmaven.compile.fork=true", "-Djvm.options=\"-Xmx100m " +
//                        "-Xms100m -XX:MaxDirectMemorySize=100m\"");
//
//                request.setGoals(commandList);
//                request.setTimeoutInSeconds(30);
//
//                InvocationResult result = invoker.execute(request);
//                System.out.println("exit code:" + result.getExitCode());
//
//                return result.getExitCode();
//
//            } catch (Throwable ex) {
//                log.error(ex.getMessage(), ex);
//            }
//
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }).collect(Collectors.toList());
//
//
//        System.out.println(res);
//
//
//        TimeUnit.HOURS.sleep(1);
//
//    }
//}
