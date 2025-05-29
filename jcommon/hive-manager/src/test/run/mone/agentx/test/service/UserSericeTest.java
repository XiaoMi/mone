package run.mone.agentx.test.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import reactor.test.StepVerifier;
import run.mone.agentx.dto.UserDTO;
import run.mone.agentx.entity.User;
import run.mone.agentx.repository.UserRepository;
import run.mone.agentx.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserService单元测试
 * @author HawickMason
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;


    @Test
    void testCreateUser_Success() {
        StepVerifier.create(userService.createUser(testUser))
                .assertNext(savedUser -> {
                    assertNotNull(savedUser.getId());
                    assertEquals(testUser.getUsername(), savedUser.getUsername());
                    assertEquals(testUser.getEmail(), savedUser.getEmail());
                    assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
                    assertEquals(1, savedUser.getState());
                    assertNotNull(savedUser.getCtime());
                    assertNotNull(savedUser.getUtime());
                })
                .verifyComplete();
    }

    @Test
    void testFindByUsername_Success() {
        User savedUser = userService.createUser(testUser).block();
        assertNotNull(savedUser);

        StepVerifier.create(userService.findByUsername(testUser.getUsername()))
                .assertNext(foundUser -> {
                    assertEquals(savedUser.getId(), foundUser.getId());
                    assertEquals(savedUser.getUsername(), foundUser.getUsername());
                    assertEquals(savedUser.getEmail(), foundUser.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void testFindByUsername_NotFound() {
        StepVerifier.create(userService.findByUsername("nonExistentUser"))
                .verifyComplete();
    }

    @Test
    void testAuthenticate_Success() {
        User savedUser = userService.createUser(testUser).block();
        assertNotNull(savedUser);

        StepVerifier.create(userService.authenticate(testUser.getUsername(), "password123"))
                .assertNext(authenticatedUser -> {
                    assertEquals(savedUser.getId(), authenticatedUser.getId());
                    assertEquals(savedUser.getUsername(), authenticatedUser.getUsername());
                })
                .verifyComplete();
    }

    @Test
    void testAuthenticate_WrongPassword() {
        User savedUser = userService.createUser(testUser).block();
        assertNotNull(savedUser);

        StepVerifier.create(userService.authenticate(testUser.getUsername(), "wrongPassword"))
                .verifyComplete();
    }

    @Test
    void testAuthenticate_UserNotFound() {
        StepVerifier.create(userService.authenticate("nonExistentUser", "password123"))
                .verifyComplete();
    }

    @Test
    void testFindAllUsers() {
        User savedUser = userService.createUser(testUser).block();
        assertNotNull(savedUser);

        // 创建第二个用户
        User secondUser = new User();
        secondUser.setUsername("testUser2");
        secondUser.setPassword("password456");
        secondUser.setEmail("test2@example.com");
        secondUser.setCtime(System.currentTimeMillis());
        secondUser.setUtime(System.currentTimeMillis());
        secondUser.setState(1);
        User savedSecondUser = userService.createUser(secondUser).block();
        assertNotNull(savedSecondUser);

        StepVerifier.create(userService.findAllUsers())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void testCreateToken_Success() {
        User savedUser = userService.createUser(testUser).block();
        assertNotNull(savedUser);

        StepVerifier.create(userService.createToken(savedUser))
                .assertNext(token -> {
                    assertNotNull(token);
                    assertFalse(token.isEmpty());
                    assertEquals(32, token.length()); // UUID without dashes
                })
                .verifyComplete();
    }

    @Test
    void testCreateToken_UserNotFound() {
        User nonExistentUser = new User();
        nonExistentUser.setUsername("nonExistentUser");

        StepVerifier.create(userService.createToken(nonExistentUser))
                .verifyComplete();
    }

    @Test
    void testGetCurrentUserToken_Success() {
        User savedUser = userService.createUser(testUser).block();
        assertNotNull(savedUser);

        // 先创建token
        String createdToken = userService.createToken(savedUser).block();
        assertNotNull(createdToken);

        StepVerifier.create(userService.getCurrentUserToken(savedUser))
                .assertNext(token -> {
                    assertEquals(createdToken, token);
                })
                .verifyComplete();
    }

    @Test
    void testGetCurrentUserToken_NoToken() {
        User savedUser = userService.createUser(testUser).block();
        assertNotNull(savedUser);

        StepVerifier.create(userService.getCurrentUserToken(savedUser))
                .assertNext(token -> {
                    assertEquals("", token);
                })
                .verifyComplete();
    }

    @Test
    void testVerifyToken_ValidToken() {
        User savedUser = userService.createUser(testUser).block();
        assertNotNull(savedUser);

        String createdToken = userService.createToken(savedUser).block();
        assertNotNull(createdToken);

        StepVerifier.create(userService.verifyToken(createdToken))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void testVerifyToken_InvalidToken() {
        StepVerifier.create(userService.verifyToken("invalid-token"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void testVerifyToken_NullToken() {
        StepVerifier.create(userService.verifyToken(null))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void testVerifyToken_EmptyToken() {
        StepVerifier.create(userService.verifyToken(""))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void testVerifyToken_InactiveUser() {
        User savedUser = userService.createUser(testUser).block();
        assertNotNull(savedUser);

        String createdToken = userService.createToken(savedUser).block();
        assertNotNull(createdToken);

        // 将用户状态设置为非活跃
        savedUser.setState(0);
        userRepository.save(savedUser).block();

        StepVerifier.create(userService.verifyToken(createdToken))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void testGetUserInfo_Success() {
        User savedUser = userService.createUser(testUser).block();
        assertNotNull(savedUser);

        StepVerifier.create(userService.getUserInfo(savedUser))
                .assertNext(userDTO -> {
                    assertEquals(savedUser.getId(), userDTO.getId());
                    assertEquals(savedUser.getUsername(), userDTO.getUsername());
                    assertEquals(savedUser.getEmail(), userDTO.getEmail());
                })
                .verifyComplete();
    }

    @Test
    void testGetUserInfo_UserNotFound() {
        User nonExistentUser = new User();
        nonExistentUser.setUsername("nonExistentUser");

        StepVerifier.create(userService.getUserInfo(nonExistentUser))
                .verifyComplete();
    }

    @Test
    void testBindInternalAccount_Success() {
        User savedUser = userService.createUser(testUser).block();
        assertNotNull(savedUser);

        String internalAccount = "internal123";
        StepVerifier.create(userService.bindInternalAccount(savedUser, internalAccount))
                .expectNext(internalAccount)
                .verifyComplete();

        // 验证绑定成功
        StepVerifier.create(userService.findByUsername(savedUser.getUsername()))
                .assertNext(updatedUser -> {
                    assertEquals(internalAccount, updatedUser.getInternalAccount());
                })
                .verifyComplete();
    }

    @Test
    void testBindInternalAccount_UserNotFound() {
        User nonExistentUser = new User();
        nonExistentUser.setUsername("nonExistentUser");

        StepVerifier.create(userService.bindInternalAccount(nonExistentUser, "internal123"))
                .verifyComplete();
    }

    @Test
    void testFindByToken_Success() {
        User savedUser = userService.createUser(testUser).block();
        assertNotNull(savedUser);

        String createdToken = userService.createToken(savedUser).block();
        assertNotNull(createdToken);

        StepVerifier.create(userService.findByToken(createdToken))
                .assertNext(foundUser -> {
                    assertEquals(savedUser.getId(), foundUser.getId());
                    assertEquals(savedUser.getUsername(), foundUser.getUsername());
                    assertEquals(createdToken, foundUser.getToken());
                })
                .verifyComplete();
    }

    @Test
    void testFindByToken_TokenNotFound() {
        StepVerifier.create(userService.findByToken("invalid-token"))
                .verifyComplete();
    }
}