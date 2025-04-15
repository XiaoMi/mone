package run.mone.agentx.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import run.mone.agentx.common.ApiResponse;
import run.mone.agentx.dto.LoginRequest;
import run.mone.agentx.dto.LoginResponse;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.JwtService;
import run.mone.agentx.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public Mono<ApiResponse<User>> register(@RequestBody User user) {
        log.info("收到注册请求: {}", user);
        
        return Mono.just(user)
                .flatMap(userToRegister -> {
                    log.info("开始处理注册请求");
                    return userService.createUser(userToRegister);
                })
                .map(savedUser -> {
                    log.info("注册成功，返回用户信息: {}", savedUser);
                    return ApiResponse.success(savedUser);
                })
                .doOnSuccess(response -> log.info("准备返回响应: {}", response))
                .doOnError(error -> log.error("注册过程中发生错误: {}", error.getMessage(), error))
                .onErrorResume(e -> {
                    log.error("捕获到错误，返回错误响应: {}", e.getMessage());
                    return Mono.just(ApiResponse.error(400, e.getMessage()));
                });
    }
    
    @PostMapping("/login")
    public Mono<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        return userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    return ApiResponse.success(LoginResponse.fromUser(user, token));
                })
                .switchIfEmpty(Mono.just(ApiResponse.error(401, "Invalid username or password")))
                .onErrorResume(e -> Mono.just(ApiResponse.error(500, e.getMessage())));
    }
}