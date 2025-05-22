package run.mone.agentx.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import run.mone.agentx.entity.InvokeHistory;

public interface InvokeHistoryRepository extends ReactiveCrudRepository<InvokeHistory, Long> {
    Flux<InvokeHistory> findByType(Integer type);
    Flux<InvokeHistory> findByRelateId(Long relateId);
    Flux<InvokeHistory> findByInvokeUserName(String invokeUserName);
} 