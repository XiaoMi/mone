package run.mone.hive.common;

import org.junit.jupiter.api.Test;

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
    public void testAA() {
        new AA().hi();
    }


}
