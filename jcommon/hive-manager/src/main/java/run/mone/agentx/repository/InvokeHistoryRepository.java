package run.mone.agentx.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
    
    // 简单分页查询
    @Query("SELECT * FROM t_invoke_history WHERE state = 1 ORDER BY ctime DESC LIMIT :limit OFFSET :offset")
    Flux<InvokeHistory> findAllWithPage(int limit, long offset);
    
    // 统计总数
    @Query("SELECT COUNT(*) FROM t_invoke_history WHERE state = 1")
    Mono<Long> countAllActive();
} 