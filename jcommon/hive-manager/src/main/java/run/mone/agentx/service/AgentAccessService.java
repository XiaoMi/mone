package run.mone.agentx.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.AgentAccess;
import run.mone.agentx.repository.AgentAccessRepository;

import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@Service
public class AgentAccessService {

    @Resource
    private AgentAccessRepository agentAccessRepository;

    public Flux<AgentAccess> getAgentAccessList(Long agentId) {
        return agentAccessRepository.findByAgentId(agentId);
    }

    public Mono<AgentAccess> createAgentAccess(AgentAccess agentAccess) {
        agentAccess.setAccessKey(UUID.randomUUID().toString().replace("-", ""));
        agentAccess.setState(1); // 默认启用
        agentAccess.setCtime(System.currentTimeMillis());
        agentAccess.setUtime(System.currentTimeMillis());
        return agentAccessRepository.save(agentAccess);
    }

    @CacheEvict(value = "agentAccess", key = "#id")
    public Mono<Void> deleteAgentAccess(Long id) {
        return agentAccessRepository.deleteById(id).then();
    }

    @CacheEvict(value = "agentAccess", key = "#id")
    public Mono<Void> updateAgentAccessStatus(Long id, Integer state) {
        return agentAccessRepository.findById(id)
                .flatMap(access -> {
                    access.setState(state);
                    return agentAccessRepository.save(access);
                })
                .then();
    }

    @Cacheable(value = "agentAccess", key = "#agentId + ':' + #accessApp + ':' + #accessKey", unless = "#result == false")
    public Mono<Boolean> validateAccess(Long agentId, String accessApp, String accessKey) {
        return agentAccessRepository.findByAgentIdAndAccessApp(agentId, accessApp)
                .map(access -> access.getAccessKey().equals(accessKey) && access.getState() == 1)
                .defaultIfEmpty(false);
    }

    @Cacheable(value = "agentAccess", key = "#agentId + ':' + #accessAppId", unless = "#result == false")
    public Mono<Boolean> validateAccess(Long agentId, String accessAppId) {
        return agentAccessRepository.findByAgentIdAndAccessAppId(agentId, accessAppId)
                .map(access -> true)
                .defaultIfEmpty(false);
    }
} 