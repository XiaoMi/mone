package run.mone.agentx.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import run.mone.agentx.common.ApiResponse;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public Mono<ApiResponse<User>> register(@RequestBody User user) {
        return userService.createUser(user).map(ApiResponse::success);
    }
}