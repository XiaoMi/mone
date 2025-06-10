package run.mone.agentx.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.dto.McpRequest;
import run.mone.agentx.dto.common.ApiResponse;
import run.mone.agentx.dto.AgentWithInstancesDTO;
import run.mone.agentx.dto.AgentQueryRequest;
import run.mone.agentx.entity.Agent;
import run.mone.agentx.entity.AgentInstance;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.AgentConfigService;
import run.mone.agentx.service.AgentService;
import run.mone.agentx.service.McpService;
import run.mone.agentx.utils.GsonUtils;
import run.mone.hive.bo.HealthInfo;
import run.mone.hive.bo.RegInfoDto;
import run.mone.hive.common.ToolDataInfo;
import run.mone.agentx.service.UserService;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
@Slf4j
public class AgentController {

    private final AgentService agentService;

    private final McpService mcpService;

    private final AgentConfigService agentConfigService;

    private final UserService userService;

    @PostMapping("/create")
    public Mono<ApiResponse<Agent>> createAgent(@AuthenticationPrincipal User user, @RequestBody Agent agent) {
        agent.setCreatedBy(user.getId());
        return agentService.createAgent(agent).map(ApiResponse::success);
    }

    @GetMapping("/list")
    public Mono<ApiResponse<List<AgentWithInstancesDTO>>> getAgents(
            @AuthenticationPrincipal User user,
            @ModelAttribute AgentQueryRequest query) {
        return agentService.findAccessibleAgentsWithInstances(user.getId(), query)
                .collectList()
                .map(list -> {
                    list.sort((a1, a2) -> a2.getAgent().getId().compareTo(a1.getAgent().getId()));
                    return ApiResponse.success(list);
                });
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
//                .filter(existingAgent -> existingAgent.getCreatedBy().equals(user.getId()))
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
                .flatMap(existingAgent -> agentService.deleteAgent(id))
                .thenReturn(ApiResponse.<Void>success(null))
                .defaultIfEmpty(ApiResponse.<Void>error(403, "Unauthorized or agent not found"));
    }

    @PostMapping("/register")
    public Mono<ApiResponse<AgentInstance>> register(@RequestBody RegInfoDto regInfoDto) {
        return userService.verifyToken(regInfoDto.getToken())
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.just(ApiResponse.<AgentInstance>error(401, "Invalid token"));
                    }
                    return agentService.register(regInfoDto).map(ApiResponse::success);
                });
    }

    //下线agent (需要调到远程)
    @PostMapping("/offline")
    public Mono<ApiResponse<String>> offline(@AuthenticationPrincipal User user, @RequestBody McpRequest request) {
        ToolDataInfo result = new ToolDataInfo("mcp_request", request.getMapData());
        result.setFrom("hive_manager");
        Flux.create(sink -> CompletableFuture.runAsync(() -> {
            //这里本质是当Agent调用的
            mcpService.callMcp(user.getUsername(), request.getAgentId(), request.getAgentInstance(), GsonUtils.gson.toJson(request), result, sink);
            sink.onDispose(() -> log.info("call mcp finish"));
            sink.complete();
        })).subscribe();
        return Mono.just(ApiResponse.success("ok"));
    }

    //清空agent历史记录 (需要调到远程)
    @PostMapping("/clearHistory")
    public Mono<ApiResponse<String>> clearHistory(@AuthenticationPrincipal User user, @RequestBody McpRequest request) {
        ToolDataInfo result = new ToolDataInfo("mcp_request", request.getMapData());
        result.setFrom("hive_manager");
        Flux.create(sink -> CompletableFuture.runAsync(() -> {
            //这里本质是当Agent调用的
            mcpService.callMcp(user.getUsername(), request.getAgentId(), request.getAgentInstance(), GsonUtils.gson.toJson(request), result, sink);
            sink.onDispose(() -> log.info("call mcp finish"));
            sink.complete();
        })).subscribe();
        return Mono.just(ApiResponse.success("ok"));
    }

    @PostMapping("/unregister")
    public Mono<ApiResponse<Void>> unregister(@RequestBody RegInfoDto regInfoDto) {
        return userService.verifyToken(regInfoDto.getToken())
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.just(ApiResponse.<Void>error(401, "Invalid token"));
                    }
                    return agentService.unregister(regInfoDto).thenReturn(ApiResponse.success(null));
                });
    }

    @PostMapping("/health")
    public Mono<ApiResponse<Void>> heartbeat(@RequestBody HealthInfo healthInfo) {
        return userService.verifyToken(healthInfo.getToken())
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.just(ApiResponse.<Void>error(401, "Invalid token"));
                    }
                    return agentService.heartbeat(healthInfo).thenReturn(ApiResponse.success(null));
                });
    }

    @GetMapping("/{id}/check")
    public Mono<ApiResponse<Boolean>> checkAccess(
            @PathVariable Long id,
            @RequestParam String accessApp,
            @RequestParam String accessKey) {
        return agentService.hasAccess(id, accessApp, accessKey)
                .map(ApiResponse::success);
    }

    @PostMapping("/config")
    public Mono<ApiResponse<Map<String, String>>> getAgentConfig(@RequestBody Map<String, Long> request) {
        Long agentId = request.get("agentId");
        Long userId = request.get("userId");

        if (agentId == null || userId == null) {
            return Mono.just(ApiResponse.error(400, "Missing required parameters: agentId and userId"));
        }

        return agentConfigService.getUserConfigsAsMap(agentId, userId)
                .map(ApiResponse::success)
                .defaultIfEmpty(ApiResponse.success(Map.of()));
    }

    @PostMapping("/instances/by-names")
    public Mono<ApiResponse<Map<String, List<AgentInstance>>>> getAgentInstancesByNames(@RequestBody Map<String, Object> request) {
        String token = (String) request.get("token");
        @SuppressWarnings("unchecked")
        List<String> agentNames = (List<String>) request.get("agentNames");

        if (token == null || agentNames == null || agentNames.isEmpty()) {
            return Mono.just(ApiResponse.error(400, "Missing required parameters: token and agentNames"));
        }

        return userService.verifyToken(token)
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.just(ApiResponse.<Map<String, List<AgentInstance>>>error(401, "Invalid token"));
                    }
                    return agentService.getAgentInstancesByNames(agentNames).map(ApiResponse::success);
                });
    }
}