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

package com.xiaomi.youpin.tesla.test;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.common.RequestContext;
import com.xiaomi.youpin.gateway.common.Utils;
import com.xiaomi.youpin.gateway.common.ZipUtils;
import com.xiaomi.youpin.gateway.ws.WsRequest;
import com.youpin.xiaomi.tesla.plugin.bo.Request;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Test;
import org.nutz.lang.stream.StringInputStream;

import javax.validation.ConstraintViolationException;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;


public class CommonTest {

    @Test
    public void testByteBufAllocator() {
        PooledByteBufAllocator allocator = new PooledByteBufAllocator();
        ByteBuf buffer = allocator.directBuffer(10);
        buffer.release();
        System.out.println(buffer.refCnt());
        System.out.println(buffer.isDirect());
    }


    @Test(expected = Throwable.class)
    public void testLock3() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        lock.readLock().unlock();
        lock.readLock().unlock();
        System.out.println("finish");
    }

    @Test
    public void testLock2() throws InterruptedException {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        new Thread(() -> {
            sleep(2);
            while(true) {
                boolean v = lock.writeLock().tryLock();
                int lockCount = lock.getReadLockCount();
                System.out.println(v+":"+lockCount);
                if (v) {
                    break;
                }
                sleep(1);
            }

        }).start();
        new Thread(() -> {
            lock.readLock().lock();
            sleep(10);
            lock.readLock().unlock();
        }).start();
        new Thread(() -> {
            lock.readLock().lock();
            sleep(5);
            lock.readLock().unlock();
        }).start();
        Thread.currentThread().join();
    }

    @SneakyThrows
    private void sleep(long t) {
        TimeUnit.SECONDS.sleep(t);
    }

    @Test
    public void testJar() throws IOException {
        URL jarUrl =
                new URL("jar:file:/tmp/filter/loginfilter!/");
        JarURLConnection connection =
                (JarURLConnection) jarUrl.openConnection();

        JarFile jar = connection.getJarFile();

        System.out.println(jar);

        jar.stream().forEach(it -> {
            if (it.getName().equals("FilterDef")) {
                System.out.println(it);
            }
        });

    }


    @Test
    public void testZip() throws IOException {
        String path = "/tmp/filter/loginfilter";
        String str = ZipUtils.readFile(path, "FilterDef");
        Properties p = new Properties();
        p.load(new StringInputStream(str));
        System.out.println(p.getProperty("groups"));
    }


    @Test
    public void testOptional() {
        String s = Optional.ofNullable("abc").map(it -> it.toString()).orElse("a");
        System.out.println(s);
    }


    @Test
    public void testJson77() {
        String json = " {\"orderId\":\"4200521602301602\",\"pid\":\"156438\",\"deliveryId\":\"420052160230160201\",\"expressSn\":\"JDV0888888\"} ";
        Map<String, Object> m = new Gson().fromJson(json, Map.class);
        System.out.println(m);
    }


    @Test
    public void testGetClassName() {
        System.out.println(ConstraintViolationException.class.getName());
    }

    @Test
    public void testStr2() {
        String name = "172.17.0.4";
        System.out.println(name.startsWith("172.17"));
    }


    @Test
    public void testSplite() {
        String cmd = "cd   /abc/def";
        String[] ss = cmd.split("\\s+");
        System.out.println(ss[1]);
    }


    @Test
    public void testConcurrentHashMap() {
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        map.put("1", "2");

        System.out.println("----->" + map.putIfAbsent("2", "3"));
        System.out.println(map);


        Object v = map.putIfAbsent("2", "3");
        System.out.println(v);

        Object v2 = map.putIfAbsent("1", "11");
        System.out.println(v2);

        System.out.println(map);
    }


    @Test
    public void testGson44() {
        String json = "[\n" +
                " [{\n" +
                "  \"freeTaxMark\": 1,\n" +
                "  \"name\": \"糖果2#\",\n" +
                "  \"preferentialMarkFlag\": 1,\n" +
                "  \"skuId\": 3048298,\n" +
                "  \"taxCode\": \"1002302010230200012\",\n" +
                "  \"taxPrice\": 99,\n" +
                "  \"taxRate\": 0,\n" +
                "  \"unit\": \"粒\",\n" +
                "  \"vatSpecialManagement\": \"op\"\n" +
                " }]\n" +
                "]";

        List list = new Gson().fromJson(json, List.class);
        System.out.println(list);

    }


    @Test
    public void testSplit3() {
        String str = ":::38403";
        System.out.println(str.split(":")[str.split(":").length - 1]);
    }


    @Test
    public void testRandom2() {
        Random r = new Random();
        IntStream.range(0, 100).forEach(it -> {
            System.out.println(r.nextInt(2));
        });
    }

    public void testLock() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//        lock.writeLock().lock();
        lock.writeLock().unlock();
        System.out.println("finish");
    }


    @Test
    public void testFind() {
        String url = "http://${zzyTest}$/abc?id=123";
        Pattern pattern = Pattern.compile("\\$\\{.*\\}\\$");
        Matcher matcher = pattern.matcher(url);
        while (matcher.find()) {
            System.out.println(matcher.start() + ":" + matcher.end());
            System.out.println(url.substring(matcher.start() + 2, matcher.end() - 2));
        }
    }

    public void testHeader() {
        List<String> abc = ServerCookieEncoder.LAX.encode(new DefaultCookie("a", "b"), new DefaultCookie("a", "1"));
        System.out.println(abc);

    }


    @Test
    public void testMatches() {
        boolean res = Pattern.matches(".*\\$\\{.*\\}\\$.*", "http://${zzyTest}$/abc?id=123");
        System.out.println(res);
    }


    @Test
    public void testReplace2() {
        String path = "http://${zzyTest}$/abc?id=123";
        String currentClient = path.replaceFirst("\\$\\{.*\\}\\$", "127.0.0.1");
        System.out.println(currentClient);
    }

    public void testSplit2() {
        String str = " detail-gateway-20191202182723889.jar";
        System.out.println(str.split("-20")[0]);
    }


    @Test
    public void testReplace() {
        String apiPackage = "a.b.c";
        apiPackage = apiPackage.replaceAll("\\.", "/");
        System.out.println(apiPackage);
    }


    @Test
    public void testByteBuf() {
        ByteBuf buf = Unpooled.buffer();
        System.out.println((int) '{' + "," + (int) '}');

        buf.writeBytes("{abc}".getBytes());

        System.out.println(buf.getByte(0));
        System.out.println(buf.getByte(buf.readableBytes() - 1));


        System.out.println(buf.readableBytes());
    }


    @Test
    public void testSplit() {
        String str = "ade|b";
        System.out.println(str.split("\\|")[0]);
    }


    @Test
    public void testRandom() {
        List<Pair<String, Integer>> list = Lists.newArrayList(Pair.of("a", 30), Pair.of("b", 30), Pair.of("c", 30));
        IntStream.range(0, 10).forEach(it -> {
            System.out.println(Utils.random(list));
        });
    }


    @Test
    public void testBuffer() {
        ByteBuf buf = Unpooled.wrappedBuffer("abc".getBytes());
        ByteBuf buf2 = buf.duplicate();
        CharSequence str = buf2.readCharSequence(buf2.readableBytes(), Charset.defaultCharset());
        System.out.println(str);
        System.out.println(buf.readCharSequence(buf.readableBytes(), Charset.defaultCharset()));
    }


    @Test
    public void testStream() {
        List<List<String>> list = new ArrayList<>();
        list.add(Lists.newArrayList("a", "b"));
        list.add(Lists.newArrayList("c", "d"));

        ArrayList<String> l2 = list.stream().collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
        System.out.println(l2);
    }


    @Test
    public void testJson22() {
        Request r = new Request();
        r.put("name", "z zy");
        r.put("cmd", "cmd");
        System.out.println(r.getCmd());
        System.out.println(new Gson().toJson(r));


        String str = "{\"name\":\"z zy\",\"cmd\":\"cmd\"}";

        Request r2 = new Gson().fromJson(str, Request.class);
        System.out.println(r2.get("name"));
    }


    @Test
    public void testCreateDirectory() throws IOException {
        Files.createDirectories(Paths.get("/tmp/f/e/d"));
    }


    public Long sum(List<Long> list) {
        return list.stream().reduce((a, b) -> a + b).get();
    }


    @Test
    public void testGson2() {
        WsRequest wr = new WsRequest();
        wr.setUri("/zzy/test");
        List<Object> data = new ArrayList<>();
        data.add(new RequestContext());
        data.add("123");
        System.out.println(new Gson().toJson(wr));
    }


    @Test
    public void testGson() throws NoSuchMethodException {


//        Class<?> clazz = ReflectUtils.forName("java.util.List<java.lang.Long>");
//        Class<?> clazz = ReflectUtils.forName("java.util.List");
//        Type e = TypeToken.get(clazz).getType();
//        System.out.println(e);


        Method method = CommonTest.class.getMethod("sum", List.class);

        Arrays.stream(method.getGenericParameterTypes()).forEach(it -> {
            Type type = TypeToken.get(it).getType();
            System.out.println("-------->" + type);
        });

        Arrays.stream(method.getParameterTypes()).forEach(it -> {
            System.out.println(it.getTypeName());
            Type type = TypeToken.get(it).getType();
            System.out.println("*********" + type);
        });

        String name = ReflectUtils.getName(List.class);
        System.out.println(name);

        name = "java.util.List<Long>";
        Gson gson = new Gson();
        if (name.equals("java.util.List<Long>")) {
            List<Long> list2 = gson.fromJson("[1,2,3,4]", new TypeToken<List<Long>>() {
            }.getType());
            System.out.println(list2);
            System.out.println(list2.get(0).getClass());
            return;
        }

        List<Long> list2 = (List<Long>) gson.fromJson("[1,2,3,4]", ReflectUtils.forName(name));
        System.out.println(list2.get(0).getClass());
        System.out.println(list2);
    }


    @Test
    public void testFile() {
        System.out.println(File.separator);
    }


    @Test
    public void testFuture() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        ExecutorService pool = Executors.newFixedThreadPool(10);
        ListeningExecutorService l = MoreExecutors.listeningDecorator(pool);
        ListenableFuture<String> l2 = l.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "abc";
            }
        });
        Futures.addCallback(l2, new FutureCallback<String>() {
            @Override
            public void onSuccess(@Nullable String result) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("success:" + result);
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println(t.getMessage());
            }
        }, executor);
        System.out.println("finish");
    }

    //15=24184 500=10295
    @Test
    public void testFuture2() throws InterruptedException {
        int size = 800;
        SimpleTimeLimiter simpleTimeLimiter = SimpleTimeLimiter.create(Executors.newFixedThreadPool(size));
        ThreadPoolExecutor pool = new ThreadPoolExecutor(size, size,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        ThreadPoolExecutor pool2 = new ThreadPoolExecutor(size, size,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());


        ListeningExecutorService li = MoreExecutors.listeningDecorator(pool);

        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            System.out.println(pool.getActiveCount() + "," + pool.getQueue().size());

        }, 0, 1, TimeUnit.SECONDS);

        int n = 10;
        int m = 1500;
        CountDownLatch countDownLatch = new CountDownLatch(n * m);

        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    TimeUnit.MILLISECONDS.sleep(200 + new Random().nextInt(2000));
                } finally {
                    countDownLatch.countDown();
                }
                return "abc";
            }
        };


        Stopwatch sw = Stopwatch.createStarted();
        for (int i = 0; i < n; i++) {
            List<String> l = IntStream.range(0, m).parallel().mapToObj(it -> {
                ListenableFuture<String> f = li.submit(() -> simpleTimeLimiter.callWithTimeout(callable, 2200, TimeUnit.MILLISECONDS));
                Futures.addCallback(f, new FutureCallback<String>() {
                    @Override
                    public void onSuccess(@Nullable String result) {
//                        System.out.println(result);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        System.out.println(t);
                    }
                }, pool2);
                return "ok";
            }).collect(Collectors.toList());

            System.out.println(l.size() + "," + i);
            TimeUnit.SECONDS.sleep(1);
        }
        countDownLatch.await();

        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
    }
}
