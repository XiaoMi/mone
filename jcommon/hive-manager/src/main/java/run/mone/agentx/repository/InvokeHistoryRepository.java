package run.mone.agentx.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import run.mone.agentx.entity.InvokeHistory;

public interface InvokeHistoryRepository extends ReactiveCrudRepository<InvokeHistory, Long> {
    Flux<InvokeHistory> findByType(Integer type);
    Flux<InvokeHistory> findByRelateId(Long relateId);
    Flux<InvokeHistory> findByInvokeUserName(String invokeUserName);
    
    // 新增查询方法，支持调用次数上报功能
    Flux<InvokeHistory> findByAppName(String appName);
    Flux<InvokeHistory> findByBusinessName(String businessName);
    Flux<InvokeHistory> findByClassName(String className);
    Flux<InvokeHistory> findByAppNameAndBusinessName(String appName, String businessName);
} 