package com.xiaomi.hera.trace.etl.consumer;

import com.google.common.util.concurrent.Monitor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

/**
 * @author goodjava@qq.com
 * @date 2023/8/31 13:49
 */
@Service
public class EnterManager {

    @Getter
    private Monitor monitor = new Monitor();

    @Getter
    private Monitor processMonitor = new Monitor();

    @Getter
    private AtomicInteger processNum = new AtomicInteger();

    private Monitor.Guard guard = monitor.newGuard(new BooleanSupplier() {
        @Override
        public boolean getAsBoolean() {
            return false;
        }
    });


    public void enter() {
        monitor.enter();
        monitor.leave();
    }

    public void processEnter() {
        processMonitor.enter();
        processMonitor.leave();
    }



}
