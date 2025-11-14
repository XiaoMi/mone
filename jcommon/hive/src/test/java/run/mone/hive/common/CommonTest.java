package run.mone.hive.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2025/6/3 22:37
 */
public class CommonTest {

    class A {

        public void a() {
            System.out.println("a");
        }

        public void hi() {
            System.out.println("--");
            this.a();
        }
    }


    class AA extends A {
        @Override
        public void a() {
            System.out.println("aa");
        }

        @Override
        public void hi() {
            super.hi();
        }
    }

    @Test
    public void testDate() {
//        Nov 7, 2025, 10:43:40 PM
        System.out.println(new Gson().toJson(new Date()));
//        Nov 7, 2025, 10:43:40 PM
        System.out.println(new GsonBuilder().setDateFormat("MMM d, yyyy, hh:mm:ss a").create().toJson(new Date()));
    }

    @Test
    public void testMap() {
        ConcurrentHashMap<String,String> m = new ConcurrentHashMap<>();
        m.put("a","a");
        m.compute("a",(k,v)->{
            if(v.equals("a1")) {
                return null;
            }
            return v;
        });
        System.out.println(m);
    }


}
