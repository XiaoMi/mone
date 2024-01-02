package run.mone.mimeter.engine.service.test;

import com.google.common.base.Stopwatch;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static common.Util.*;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/8/28
 */
@Ignore
public class ConcurrencyTest {


    private String m() {
        try {

        } finally {
            if (1 == 1) {
                return "def";
            }
        }
        System.out.println("return");
        return "abc";
    }


    @Test
    public void testFinally() {
        System.out.println(m());
    }

    @Test
    public void testLatch() {
        CountDownLatch l = new CountDownLatch(10);
        IntStream.range(0,22).forEach(i->{
            l.countDown();
        });
        System.out.println(l.getCount());
    }


    @Test
    public void testMap() {
        ConcurrentHashMap<String, Object> m = new ConcurrentHashMap<>();
        m.put("abc", "abc");
        Object v = m.remove("def");
        System.out.println(v);
        System.out.println(m);
    }

    @Test
    public void atomicTest() {
        AtomicLong al = new AtomicLong(0L);
        modifyAL(al, 1L);
        System.out.println(al.get());
    }

    private void modifyAL(AtomicLong al, long newVal) {
        al.set(newVal);
    }

//    @Test
//    public void nullCastTest() {
//        Integer val = null;
//        System.out.println((long) val);
//    }

    @Test
    public void sampleRateToCntTest() {
        System.out.println(sampleRateToCnt(1, 5));
    }

    @Test
    public void samplingTest() {
        int[][] lists = new int[][]{{10, 10}, {1000, 100}, {100000, 5000}, {100000, 20000}, {100000, 50000}, {100000, 80000}};

        for (int[] arr : lists) {
            Stopwatch sw = Stopwatch.createStarted();
            sampleIndices(arr[0], arr[1]);
            System.out.println("sampling indices takes:" + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    @Test
    public void sampleByTimeAndQpsTest() {
        int[][] lists = new int[][]{{8, 1, 10}, {1000, 100, 5}};

        for (int[] arr : lists) {
            int duration = arr[0];
            int qps = arr[1];
            Set<Integer> logIndices = new HashSet<>(sampleByTimeAndQps(duration, qps, arr[2]));
            System.out.println("sampling indices:" + logIndices);

            int hits = 0;

            for (int j = 1; j <= duration; j++) {
                for (int i = 0; i < qps; i++) {
                    if (logIndices.contains(((j - 1) % 10) * qps + i)) {
                        hits++;
                    }
                }
            }
            int total = duration * qps;
            System.out.println("hits:" + hits + ", total:" + total + ", rate:" + ((100.0d * hits) / total));
        }
    }
}
