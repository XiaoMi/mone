package com.xiaomi.mone.monitor.service.kubernetes;

import com.xiaomi.mone.monitor.service.http.MoneSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author gaoxihui
 * @date 2022/6/7 9:51 上午
 */
@Slf4j
@Service
public class CapacityAdjustMessageService {

    private LinkedBlockingDeque<MoneSpec> queue = new LinkedBlockingDeque<>();

    public void product(MoneSpec moneSpec){
        try {
            queue.putLast(moneSpec);
        } catch (InterruptedException e) {
            log.error("CapacityAdjustMessageService.product error:{}",e.getMessage(),e);
        }
    }

    public MoneSpec consume(){
        try {
            return queue.pollFirst();
        } catch (Exception e) {
            log.error("CapacityAdjustMessageService.consume error:{}",e.getMessage(),e);
            return null;
        }
    }

    public int queueSize(){
        return queue.size();
    }

}
