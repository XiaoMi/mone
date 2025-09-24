package run.mone.agentx.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import run.mone.agentx.dto.ConfigRequest;
import run.mone.agentx.dto.common.ApiResponse;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentConfig;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.AgentConfigService;
import run.mone.agentx.service.AgentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agents/{agentId}/config")
@RequiredArgsConstructor
public class AgentConfigController {
    private final AgentConfigService agentConfigService;
    private final AgentService agentService;

    private Mono<Boolean> checkAgentAccess(Long agentId, User user) {
        return agentService.findById(agentId)
                .map(agent -> agent.getCreatedBy().equals(user.getId()) || (agent.getIsPublic() != null && agent.getIsPublic()))
                .defaultIfEmpty(false);
    }

    @GetMapping
    public Mono<ApiResponse<List<AgentConfig>>> getAllConfigs(
            @AuthenticationPrincipal User user,
            @PathVariable Long agentId) {
        return checkAgentAccess(agentId, user)
                .flatMap(hasAccess -> {
                    if (hasAccess) {
                        return agentConfigService.getUserConfigsByAgentId(agentId, user.getId())
                                .collectList()
                                .map(ApiResponse::success);
                    }
                    return Mono.just(ApiResponse.<List<AgentConfig>>error(403, "No access to this agent"));
                });
    }

    @GetMapping("/all")
    public Mono<ApiResponse<List<AgentConfig>>> getAllAgentConfigs(
            @AuthenticationPrincipal User user,
            @PathVariable Long agentId) {
        return checkAgentAccess(agentId, user)
                .flatMap(hasAccess -> {
                    if (hasAccess) {
                        return agentConfigService.getAllConfigsByAgentId(agentId)
                                .collectList()
                                .map(ApiResponse::success);
                    }
                    return Mono.just(ApiResponse.<List<AgentConfig>>error(403, "No access to this agent"));
                });
    }

    @GetMapping("/{key}")
    public Mono<ApiResponse<AgentConfig>> getConfig(
            @AuthenticationPrincipal User user,
            @PathVariable Long agentId,
            @PathVariable String key) {
        return checkAgentAccess(agentId, user)
                .flatMap(hasAccess -> {
                    if (hasAccess) {
                        return agentConfigService.getConfigByKey(agentId, user.getId(), key)
                                .map(ApiResponse::success)
                                .defaultIfEmpty(ApiResponse.error(404, "Config not found"));
                    }
                    return Mono.just(ApiResponse.<AgentConfig>error(403, "No access to this agent"));
                });
    }

    @PostMapping("/{key}")
    public Mono<ApiResponse<AgentConfig>> setConfig(
            @AuthenticationPrincipal User user,
            @PathVariable Long agentId,
            @PathVariable String key,
            @RequestBody ConfigRequest request) {
        return checkAgentAccess(agentId, user)
                .flatMap(hasAccess -> {
                    if (hasAccess) {
                        return agentConfigService.setConfig(agentId, user.getId(), key, request.getValue())
                                .map(ApiResponse::success);
                    }
                    return Mono.just(ApiResponse.<AgentConfig>error(403, "No access to this agent"));
                });
    }

    @PostMapping("/batch")
    public Mono<ApiResponse<Void>> setBatchConfig(
            @AuthenticationPrincipal User user,
            @PathVariable Long agentId,
            @RequestBody ConfigRequest request) {
        return checkAgentAccess(agentId, user)
                .flatMap(hasAccess -> {
                    if (hasAccess) {
                        return agentConfigService.setBatchConfig(agentId, user.getId(), request.getConfigs())
                                .thenReturn(ApiResponse.<Void>success(null));
                    }
                    return Mono.just(ApiResponse.<Void>error(403, "No access to this agent"));
                });
    }

    @DeleteMapping("/{key}")
    public Mono<ApiResponse<Void>> deleteConfig(
            @AuthenticationPrincipal User user,
            @PathVariable Long agentId,
            @PathVariable String key) {
        return checkAgentAccess(agentId, user)
                .flatMap(hasAccess -> {
                    if (hasAccess) {
                        return agentConfigService.deleteConfig(agentId, user.getId(), key)
                                .thenReturn(ApiResponse.<Void>success(null));
                    }
                    return Mono.just(ApiResponse.<Void>error(403, "No access to this agent"));
                });
    }
} 