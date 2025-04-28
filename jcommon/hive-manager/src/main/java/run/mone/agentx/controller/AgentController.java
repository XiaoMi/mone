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
import reactor.core.publisher.Mono;
import run.mone.agentx.dto.common.ApiResponse;
import run.mone.agentx.dto.AgentWithInstancesDTO;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentInstance;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.AgentService;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfoDto;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {
    private final AgentService agentService;

    @PostMapping("/create")
    public Mono<ApiResponse<Agent>> createAgent(@AuthenticationPrincipal User user, @RequestBody Agent agent) {
        agent.setCreatedBy(user.getId());
        return agentService.createAgent(agent).map(ApiResponse::success);
    }

    @GetMapping("/list")
    public Mono<ApiResponse<List<AgentWithInstancesDTO>>> getAgents(@AuthenticationPrincipal User user) {
        return agentService.findAccessibleAgentsWithInstances(user.getId()).collectList().map(ApiResponse::success);
    }

    @GetMapping("/access/{id}")
    public Mono<ApiResponse<AgentWithInstancesDTO>> getAccessAgent(
            @PathVariable Long id,
            @RequestParam String accessApp,
            @RequestParam String accessKey) {
        return agentService.hasAccess(id, accessApp, accessKey)
                .flatMap(hasAccess -> {
                    if (hasAccess) {
                        return agentService.findAgentWithInstances(id)
                                .map(ApiResponse::success);
                    }
                    return Mono.just(ApiResponse.<AgentWithInstancesDTO>error(403, "Access denied"));
                });
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<AgentWithInstancesDTO>> getAgent(@PathVariable Long id) {

        return agentService.findAgentWithInstances(id)
                .map(ApiResponse::success)
                .defaultIfEmpty(ApiResponse.error(404, "Agent not found"));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<Agent>> updateAgent(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody Agent agent) {
        return agentService.findById(id)
                .filter(existingAgent -> existingAgent.getCreatedBy().equals(user.getId()))
                .flatMap(existingAgent -> {
                    agent.setId(id);
                    agent.setCreatedBy(user.getId());
                    return agentService.updateAgent(agent);
                })
                .map(ApiResponse::success)
                .defaultIfEmpty(ApiResponse.<Agent>error(403, "Unauthorized or agent not found"));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> deleteAgent(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return agentService.findById(id)
                .filter(existingAgent -> existingAgent.getCreatedBy().equals(user.getId()))
                .flatMap(existingAgent -> agentService.deleteAgent(id))
                .thenReturn(ApiResponse.<Void>success(null))
                .defaultIfEmpty(ApiResponse.<Void>error(403, "Unauthorized or agent not found"));
    }

    @PostMapping("/register")
    public Mono<ApiResponse<AgentInstance>> register(@RequestBody RegInfoDto regInfoDto) {
        return agentService.register(regInfoDto).map(ApiResponse::success);
    }

    @PostMapping("/unregister")
    public Mono<ApiResponse<Void>> unregister(@RequestBody RegInfoDto regInfoDto) {
        return agentService.unregister(regInfoDto).thenReturn(ApiResponse.success(null));
    }

    @PostMapping("/health")
    public Mono<ApiResponse<Void>> heartbeat(@RequestBody HealthInfo healthInfo) {
        return agentService.heartbeat(healthInfo).thenReturn(ApiResponse.success(null));
    }

    @GetMapping("/{id}/check")
    public Mono<ApiResponse<Boolean>> checkAccess(
            @PathVariable Long id,
            @RequestParam String accessApp,
            @RequestParam String accessKey) {
        return agentService.hasAccess(id, accessApp, accessKey)
                .map(ApiResponse::success);
    }
}