package run.mone.mfa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MFA认证控制器
 */
@RestController
@RequestMapping("/api/mfa")
@RequiredArgsConstructor
@Slf4j
public class MfaController {

    private final UserService userService;
    private final UserRepository userRepository;
    /**
     * 获取当前用户的MFA状态
     * 如果未启用MFA，会返回二维码URL
     */
    @GetMapping("/status")
    @UserModel
    public Result<Map<String, Object>> getMfaStatus(User currentUser) {
        if (currentUser == null) {
            return Result.error(401, "未授权");
        }
        
        log.info("获取用户MFA状态: {}", currentUser.getUsername());
        Map<String, Object> status = userService.getMfaStatus(currentUser.getId());
        return Result.success(status);
    }

    /**
     * 验证MFA验证码
     */
    @PostMapping("/verify")
    public Result<Boolean> verifyMfaCode(@RequestBody MfaVerifyRequest request, HttpServletResponse response) {
        if (request == null) {
            return Result.error(400, "请求参数错误");
        }

        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User user = userOpt.get();
        // 使用 PwdUtils 验证密码
        if (!PwdUtils.verifyPassword(request.getPassword(), user.getPasswordMd5())) {
            throw new RuntimeException("密码错误");
        }
        if (!user.getEnabled()) {
            throw new RuntimeException("用户已被禁用");
        }
        
        log.info("验证用户MFA验证码: {}", request.getUsername());
        boolean isValid = userService.verifyMfaCode(user.getId(), request.getMfaCode());
        
        if (isValid) {
            // 设置MFA验证通过的Cookie
            ResponseCookie mfaCookie = ResponseCookie.from("mfaVerified", "true")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(24 * 60 * 60) // 24小时有效期
                .build();
            
            response.addHeader("Set-Cookie", mfaCookie.toString());
        }
        
        return Result.success(isValid);
    }

    /**
     * 启用/禁用MFA
     */
    @PostMapping("/toggle")
    @UserModel
    public Result<Map<String, Object>> toggleMfa(User currentUser, @RequestBody MfaStatusRequest request) {
        if (currentUser == null) {
            return Result.error(401, "未授权");
        }
        
        log.info("{}用户MFA: {}", request.isEnabled() ? "启用" : "禁用", currentUser.getUsername());
        User updatedUser = userService.updateMfaStatus(currentUser.getId(), request.isEnabled(), request.getCode());
        
        Map<String, Object> result = Map.of(
            "username", updatedUser.getUsername(),
            "mfaEnabled", updatedUser.getMfaEnabled()
        );
        
        return Result.success(result);
    }
} 