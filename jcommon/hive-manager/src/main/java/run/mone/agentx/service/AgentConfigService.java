package run.mone.agentx.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.entity.AgentConfig;
import run.mone.agentx.repository.AgentConfigRepository;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentConfigService {
    private final AgentConfigRepository agentConfigRepository;

    public Flux<AgentConfig> getAllConfigsByAgentId(Long agentId) {
        return agentConfigRepository.findByAgentId(agentId);
    }
    
    public Flux<AgentConfig> getUserConfigsByAgentId(Long agentId, Long userId) {
        return agentConfigRepository.findByAgentIdAndUserId(agentId, userId);
    }

    public Mono<AgentConfig> getConfigByKey(Long agentId, Long userId, String key) {
        return agentConfigRepository.findByAgentIdAndUserIdAndKey(agentId, userId, key);
    }

    public Mono<AgentConfig> setConfig(Long agentId, Long userId, String key, String value) {
        return agentConfigRepository.findByAgentIdAndUserIdAndKey(agentId, userId, key)
                .flatMap(config -> {
                    config.setValue(value);
                    config.setUtime(System.currentTimeMillis());
                    return agentConfigRepository.save(config);
                })
                .switchIfEmpty(
                    Mono.<AgentConfig>defer(() -> {
                        AgentConfig config = new AgentConfig();
                        config.setAgentId(agentId);
                        config.setUserId(userId);
                        config.setKey(key);
                        config.setValue(value);
                        config.setCtime(System.currentTimeMillis());
                        config.setUtime(System.currentTimeMillis());
                        config.setState(1);
                        return agentConfigRepository.save(config);
                    })
                );
    }

    public Mono<Void> deleteConfig(Long agentId, Long userId, String key) {
        return agentConfigRepository.deleteByAgentIdAndUserIdAndKey(agentId, userId, key);
    }

    public Mono<Void> setBatchConfig(Long agentId, Long userId, Map<String, String> configs) {
        return Flux.fromIterable(configs.entrySet())
                .flatMap(entry -> setConfig(agentId, userId, entry.getKey(), entry.getValue()))
                .then();
    }

    public Mono<Map<String, String>> getUserConfigsAsMap(Long agentId, Long userId) {
        return getUserConfigsByAgentId(agentId, userId)
                .collectMap(AgentConfig::getKey, AgentConfig::getValue);
    }
} 