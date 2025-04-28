package run.mone.agentx.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import run.mone.agentx.dto.common.ApiResponse;
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
    private static final String TOKEN_COOKIE_NAME = "auth_token";
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

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
    public Mono<ResponseEntity<ApiResponse<LoginResponse>>> login(@RequestBody LoginRequest loginRequest) {
        return userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    LoginResponse loginResponse = LoginResponse.fromUser(user, token);
                    
                    // 创建 http-only cookie，过期时间与 JWT token 一致
                    // 由于 JWT token 使用 UTC 时间，而 cookie 使用本地时间，需要调整时区
                    ResponseCookie cookie = ResponseCookie.from(TOKEN_COOKIE_NAME, token)
                            .httpOnly(true)
                            .secure(true)  // 只在 HTTPS 下发送
                            .path("/")
                            .maxAge((jwtExpiration + 8 * 60 * 60 * 1000) / 1000)  // 将毫秒转换为秒，并加上 8 小时的时区差
                            .build();
                    
                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .body(ApiResponse.success(loginResponse));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.ok()
                        .body(ApiResponse.error(401, "Invalid username or password"))))
                .onErrorResume(e -> Mono.just(ResponseEntity.ok()
                        .body(ApiResponse.error(500, e.getMessage()))));
    }
}