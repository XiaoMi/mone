package run.mone.agentx.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import run.mone.agentx.entity.User;
import run.mone.agentx.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> createUser(User user) {
        log.info("开始创建用户: {}", user.getUsername());
        
        // 直接保存用户，不做重复检查
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCtime(System.currentTimeMillis());
        user.setUtime(System.currentTimeMillis());
        user.setState(1);
        
        log.info("准备保存用户到数据库: {}", user);
        
        return Mono.just(user)
                .flatMap(userToSave -> {
                    log.info("执行保存操作");
                    return userRepository.save(userToSave);
                })
                .doOnSuccess(savedUser -> log.info("用户保存成功: {}", savedUser))
                .doOnError(error -> log.error("保存用户时发生错误: {}", error.getMessage(), error));
    }

    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Mono<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()));
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }
}