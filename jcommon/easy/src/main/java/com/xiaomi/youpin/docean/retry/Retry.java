package com.xiaomi.youpin.docean.retry;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * @author goodjava@qq.com
 * @date 2023/2/19 13:26
 */
@Slf4j
public class Retry<T> {

    @Setter
    private int num = 3;

    @Setter
    private Predicate<T> checkResultPredicate = (res) -> null != res;


    public Retry(int num, Predicate<T> checkResultPredicate) {
        this.num = num;
        this.checkResultPredicate = checkResultPredicate;
    }

    public Retry() {
    }

    public interface ExCallable<T> {
        T call() throws Throwable;
    }


    public Optional<T> execute(ExCallable<T> callable) {
        for (int i = 0; i < num; i++) {
            try {
                T res = callable.call();
                if (!checkResultPredicate.test(res)) {
                    log.error("{} check result error:{}", i + 1, res);
                    continue;
                }
                return Optional.of(res);
            } catch (Throwable ex) {
                log.error("{} execute error:{}", i + 1, ex.getMessage());
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return Optional.empty();

    }

}
