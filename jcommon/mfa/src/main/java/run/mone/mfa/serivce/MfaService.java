package run.mone.mfa.serivce;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.KeyRepresentation;

import run.mone.mfa.model.User;

public class MfaService {
    
    @Value("${mfa.issuer:mone}")
    private String mfaIssuer;
    
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator(
            new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder()
                    .setTimeStepSizeInMillis(30000) // 30秒
                    .setWindowSize(3) // 允许前后1个时间窗口的验证码
                    .setCodeDigits(6) // 6位验证码
                    .setKeyRepresentation(KeyRepresentation.BASE32)
                    .build()
    );

    protected User findById(Long userId) {
        // TODO
        return null;
    }
    
    public String generateMfaSecret(Long userId) {
        Optional<User> userOpt = findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        
        // 生成新的密钥
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secretKey = key.getKey();
        
        // 保存密钥，但不启用MFA
        user.setMfaSecret(secretKey);
        user.setMfaEnabled(false);
        userRepository.save(user);
        
        return secretKey;
    }
    
    public Map<String, Object> getMfaStatus(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        Map<String, Object> result = new HashMap<>();
        
        result.put("enabled", user.getMfaEnabled());
        
        // 如果MFA未启用，且没有密钥，生成新的密钥
        if (!user.getMfaEnabled()) {
            String secretKey = StringUtils.isBlank(user.getMfaSecret()) ? generateMfaSecret(userId) : user.getMfaSecret();
            String username = user.getUsername();
            
            // 生成otpauth链接和二维码URL
            String otpauthUrl = GoogleAuthenticatorQRGenerator
                .getOtpAuthTotpURL(mfaIssuer, username, new GoogleAuthenticatorKey.Builder(secretKey).build());
            
            result.put("secret", secretKey);
            result.put("otpauthUrl", otpauthUrl);
        }
        
        return result;
    }
    
    public boolean verifyMfaCode(Long userId, String code) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // 如果用户未启用MFA或密钥为空，直接返回失败
        if (user.getMfaSecret() == null || user.getMfaSecret().isEmpty()) {
            return false;
        }
        
        try {
            int verificationCode = Integer.parseInt(code);
            return gAuth.authorize(user.getMfaSecret(), verificationCode);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    @Override
    public User updateMfaStatus(Long userId, boolean enabled, String code) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        
        User user = userOpt.get();
        
        // 要启用MFA
        if (enabled) {
            // 验证提供的验证码
            if (code == null || code.isEmpty()) {
                throw new RuntimeException("启用MFA需要验证码");
            }
            
            if (user.getMfaSecret() == null || user.getMfaSecret().isEmpty()) {
                throw new RuntimeException("MFA密钥未生成，请先获取MFA密钥");
            }
            
            // 验证验证码
            boolean isValid = verifyMfaCode(userId, code);
            if (!isValid) {
                throw new RuntimeException("验证码错误，无法启用MFA");
            }
            
            user.setMfaEnabled(true);
        } else {
            // 禁用MFA
            user.setMfaEnabled(false);
            // 可以选择是否清除密钥
            // user.setMfaSecret(null);
        }
        
        return userRepository.save(user);
    }
}