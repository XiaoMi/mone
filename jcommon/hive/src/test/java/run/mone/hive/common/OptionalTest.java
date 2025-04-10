package run.mone.hive.common;

import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * @author goodjava@qq.com
 * @date 2025/4/10 14:27
 */
public class OptionalTest {

    @Test
    public void test1() {
        Optional.ofNullable("abc").ifPresent(it->{
            System.out.println(it);
        });
    }
}
