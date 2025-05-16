package run.mone.agentx.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.InvokeHistory;
import run.mone.agentx.repository.InvokeHistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvokeHistoryService {
    private final InvokeHistoryRepository invokeHistoryRepository;

    public Mono<InvokeHistory> createInvokeHistory(InvokeHistory invokeHistory) {
        invokeHistory.setInvokeTime(System.currentTimeMillis());
        invokeHistory.setCtime(System.currentTimeMillis());
        invokeHistory.setUtime(System.currentTimeMillis());
        invokeHistory.setState(1);
        return invokeHistoryRepository.save(invokeHistory);
    }

    public Flux<InvokeHistory> findByType(Integer type) {
        return invokeHistoryRepository.findByType(type);
    }

    public Flux<InvokeHistory> findByRelateId(Long relateId) {
        return invokeHistoryRepository.findByRelateId(relateId);
    }

    public Flux<InvokeHistory> findByInvokeUserName(String invokeUserName) {
        return invokeHistoryRepository.findByInvokeUserName(invokeUserName);
    }

    public Mono<InvokeHistory> findById(Long id) {
        return invokeHistoryRepository.findById(id);
    }

    public Mono<InvokeHistory> updateInvokeHistory(InvokeHistory invokeHistory) {
        return invokeHistoryRepository.findById(invokeHistory.getId())
                .flatMap(existingHistory -> {
                    existingHistory.setType(invokeHistory.getType());
                    existingHistory.setRelateId(invokeHistory.getRelateId());
                    existingHistory.setInputs(invokeHistory.getInputs());
                    existingHistory.setOutputs(invokeHistory.getOutputs());
                    existingHistory.setInvokeTime(invokeHistory.getInvokeTime());
                    existingHistory.setInvokeWay(invokeHistory.getInvokeWay());
                    existingHistory.setInvokeUserName(invokeHistory.getInvokeUserName());
                    existingHistory.setUtime(System.currentTimeMillis());
                    return invokeHistoryRepository.save(existingHistory);
                });
    }

    public Mono<Void> deleteInvokeHistory(Long id) {
        return invokeHistoryRepository.findById(id)
                .flatMap(history -> {
                    history.setState(0);
                    history.setUtime(System.currentTimeMillis());
                    return invokeHistoryRepository.save(history);
                })
                .then();
    }
} 