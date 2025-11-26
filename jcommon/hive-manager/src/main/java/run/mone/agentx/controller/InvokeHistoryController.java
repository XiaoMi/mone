package run.mone.agentx.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.dto.common.ApiResponse;
import run.mone.agentx.entity.InvokeHistory;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.InvokeHistoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/invoke/history")
@RequiredArgsConstructor
public class InvokeHistoryController {
    private final InvokeHistoryService invokeHistoryService;

    @PostMapping
    public Mono<ApiResponse<InvokeHistory>> createInvokeHistory(
            @AuthenticationPrincipal User user,
            @RequestBody InvokeHistory invokeHistory) {
        invokeHistory.setInvokeUserName(user.getUsername());
        return invokeHistoryService.createInvokeHistory(invokeHistory)
                .map(ApiResponse::success);
    }

    @GetMapping("/type/{type}")
    public Mono<ApiResponse<List<InvokeHistory>>> getByType(
            @AuthenticationPrincipal User user,
            @PathVariable Integer type) {
        return invokeHistoryService.findByType(type)
                .collectList()
                .map(ApiResponse::success);
    }

    @GetMapping("/relate/{relateId}")
    public Mono<ApiResponse<List<InvokeHistory>>> getByRelateId(
            @AuthenticationPrincipal User user,
            @PathVariable Long relateId) {
        return invokeHistoryService.findByRelateId(relateId)
                .collectList()
                .map(ApiResponse::success);
    }

    @GetMapping("/user/{userName}")
    public Mono<ApiResponse<List<InvokeHistory>>> getByUserName(
            @AuthenticationPrincipal User user,
            @PathVariable String userName) {
        return invokeHistoryService.findByInvokeUserName(userName)
                .collectList()
                .map(ApiResponse::success);
    }

    @GetMapping("/{id}")
    public Mono<ApiResponse<InvokeHistory>> getById(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return invokeHistoryService.findById(id)
                .map(ApiResponse::success);
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse<InvokeHistory>> updateInvokeHistory(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody InvokeHistory invokeHistory) {
        invokeHistory.setId(id);
        return invokeHistoryService.updateInvokeHistory(invokeHistory)
                .map(ApiResponse::success);
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse<Void>> deleteInvokeHistory(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        return invokeHistoryService.deleteInvokeHistory(id)
                .thenReturn(ApiResponse.success(null));
    }
} 