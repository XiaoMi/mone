package run.mone.agentx.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.common.ApiResponse;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentAccess;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.AgentAccessService;
import run.mone.agentx.service.AgentService;

@RestController
@RequestMapping("/api/agent/access")
@RequiredArgsConstructor
public class AgentAccessController {
    private final AgentService agentService;
    private final AgentAccessService agentAccessService;

    @GetMapping("/list/{agentId}")
    public Mono<ApiResponse<List<AgentAccess>>> getAccessList(@PathVariable Long agentId) {
        return agentAccessService.getAgentAccessList(agentId)
                .collectList()
                .map(ApiResponse::success);
    }

    @PostMapping("/create")
    public Mono<ApiResponse<AgentAccess>> createAccess(@RequestBody AgentAccess agentAccess) {
        return agentAccessService.createAgentAccess(agentAccess)
                .map(ApiResponse::success);
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> deleteAccess(@PathVariable Long id) {
        return agentAccessService.deleteAgentAccess(id)
                .thenReturn(ApiResponse.success(null));
    }

    @PutMapping("/{id}/state")
    public Mono<ApiResponse<Void>> updateState(@PathVariable Long id, @RequestParam Integer state) {
        return agentAccessService.updateAgentAccessStatus(id, state)
                .thenReturn(ApiResponse.success(null));
    }

    @GetMapping("/validate")
    public Mono<ApiResponse<Boolean>> validateAccess(@RequestParam Long agentId, 
                                                   @RequestParam String accessApp, 
                                                   @RequestParam String accessKey) {
        return agentAccessService.validateAccess(agentId, accessApp, accessKey)
                .map(ApiResponse::success);
    }

    @GetMapping("/agents")
    public Mono<ApiResponse<List<Agent>>> getAccessibleAgents(@AuthenticationPrincipal User user) {
        return agentService.findAccessibleAgents(user.getId())
                .collectList()
                .map(ApiResponse::success);
    }

    @GetMapping("/check/{agentId}")
    public Mono<ApiResponse<Boolean>> checkAccess(
            @AuthenticationPrincipal User user,
            @PathVariable Long agentId,
            @RequestParam String accessApp,
            @RequestParam String accessKey) {
        return agentService.hasAccess(agentId, accessApp, accessKey)
                .map(ApiResponse::success);
    }

    @PostMapping("/{agentId}/users/{userId}")
    public Mono<ApiResponse<Void>> grantAccess(
            @AuthenticationPrincipal User user,
            @PathVariable Long agentId,
            @PathVariable Long userId) {
        // Only agent owner can grant access
        return agentService.findById(agentId)
                .filter(agent -> agent.getCreatedBy().equals(user.getId()))
                .flatMap(agent -> agentService.grantAccess(agentId, userId))
                .thenReturn(ApiResponse.<Void>success(null))
                .defaultIfEmpty(ApiResponse.<Void>error(403, "Unauthorized or agent not found"));
    }

    @DeleteMapping("/{agentId}/users/{userId}")
    public Mono<ApiResponse<Void>> revokeAccess(
            @AuthenticationPrincipal User user,
            @PathVariable Long agentId,
            @PathVariable Long userId) {
        // Only agent owner can revoke access
        return agentService.findById(agentId)
                .filter(agent -> agent.getCreatedBy().equals(user.getId()))
                .flatMap(agent -> agentService.revokeAccess(agentId, userId))
                .thenReturn(ApiResponse.<Void>success(null))
                .defaultIfEmpty(ApiResponse.<Void>error(403, "Unauthorized or agent not found"));
    }

    @GetMapping("/{agentId}/users")
    public Mono<ApiResponse<List<String>>> getAuthorizedUsers(
            @AuthenticationPrincipal User user,
            @PathVariable Long agentId) {
        // Only agent owner can see authorized users
        return agentService.findById(agentId)
                .filter(agent -> agent.getCreatedBy().equals(user.getId()))
                .flatMapMany(agent -> agentService.getAuthorizedUserIds(agentId))
                .collectList()
                .map(ApiResponse::success)
                .defaultIfEmpty(ApiResponse.<List<String>>error(403, "Unauthorized or agent not found"));
    }

    @PutMapping("/{agentId}/public/{isPublic}")
    public Mono<ApiResponse<Agent>> setPublicAccess(
            @AuthenticationPrincipal User user,
            @PathVariable Long agentId,
            @PathVariable Boolean isPublic) {
        // Only agent owner can change public access setting
        return agentService.findById(agentId)
                .filter(agent -> agent.getCreatedBy().equals(user.getId()))
                .flatMap(agent -> {
                    agent.setIsPublic(isPublic);
                    return agentService.updateAgent(agent);
                })
                .map(ApiResponse::success)
                .defaultIfEmpty(ApiResponse.<Agent>error(403, "Unauthorized or agent not found"));
    }
} 