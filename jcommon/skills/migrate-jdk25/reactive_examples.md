# Reactive Programming Examples

Code templates and patterns for converting blocking I/O to reactive programming with Spring WebFlux, R2DBC, and Reactor.

## Table of Contents

1. [Basic Mono & Flux Operations](#basic-mono--flux-operations)
2. [Converting Blocking to Reactive](#converting-blocking-to-reactive)
3. [Database Access (R2DBC)](#database-access-r2dbc)
4. [Redis Reactive Operations](#redis-reactive-operations)
5. [Dubbo Reactive RPC](#dubbo-reactive-rpc)
6. [WebFlux Controllers](#webflux-controllers)
7. [Error Handling](#error-handling)
8. [Advanced Patterns](#advanced-patterns)

---

## Basic Mono & Flux Operations

### Creating Streams

```java
// Mono - single value or empty
Mono<String> mono = Mono.just("Hello");
Mono<String> empty = Mono.empty();
Mono<String> error = Mono.error(new RuntimeException("Error"));

// Flux - multiple values
Flux<String> flux = Flux.just("A", "B", "C");
Flux<Integer> range = Flux.range(1, 5);
Flux<String> fromList = Flux.fromIterable(Arrays.asList("X", "Y", "Z"));
```

### Transforming Data

```java
// map - synchronous transformation
Mono<String> upper = mono.map(String::toUpperCase);

// flatMap - asynchronous transformation
Mono<UserDTO> user = mono.flatMap(id -> userService.findById(id));

// flatMapMany - Mono to Flux
Flux<Order> orders = userMono.flatMapMany(user -> orderService.findByUserId(user.getId()));
```

### Filtering

```java
Flux<Integer> filtered = flux
    .filter(n -> n > 10)
    .distinct()
    .take(5);
```

---

## Converting Blocking to Reactive

### Example 1: Simple Blocking RPC Call

**Before (Blocking):**

```java
public MaterialSearchResponse search(MaterialSearchReqDTO searchDTO) {
    try {
        // 1. DTO to Request
        MaterialSearchParam req = new MaterialSearchParam();
        BeanUtils.copyProperties(searchDTO, req);

        // 2. Execute search
        Result<MaterialSearchResponse> res = materialProvider.search(req);

        if (res == null) {
            throw new RuntimeException("res is null");
        }
        if (res.getCode() != GeneralCodes.OK.getCode()) {
            throw new RuntimeException(res.getMessage());
        }

        return res.getData();
    } catch (Exception e) {
        log.error("SearchClient->search error", e);
        return null;
    }
}
```

**After (Reactive):**

```java
public Mono<MaterialSearchResponse> search(MaterialSearchReqDTO searchDTO) {
    // DTO to Request
    MaterialSearchParam req = new MaterialSearchParam();
    BeanUtils.copyProperties(searchDTO, req);

    // Async execution
    return Mono.fromCallable(() -> materialProvider.search(req))
        .flatMap(res -> {
            // Check null result
            if (res == null) {
                return Mono.error(new RuntimeException("Query result is null"));
            }
            // Check response code
            if (res.getCode() != GeneralCodes.OK.getCode()) {
                return Mono.error(new RuntimeException(
                    String.format("Service error: %s (code=%d)", res.getMessage(), res.getCode())
                ));
            }
            // Return success result
            return Mono.just(res.getData());
        })
        .subscribeOn(Schedulers.boundedElastic()) // For blocking I/O
        .doOnSuccess(res -> log.info("Query success: {}", res))
        .doOnError(e -> log.error("Query failed", e));
}
```

### Example 2: Data Transformation with Multiple Steps

**Before (Blocking):**

```java
public List<MaterialInfoResDTO> search(MaterialSearchReqDTO searchDTO) {
    log.info("search request: {}", JSON.toJSONString(searchDTO));

    // Get store info
    StoreData storeData = storeClient.getStoreInfo(searchDTO.getOrgCode());
    StoreTypeEnum storeType = StoreTypeEnum.getByStoreInfo(storeData);
    searchDTO.setStoreType(storeType.getCode());

    // Execute search
    MaterialSearchResponse res = searchClient.search(searchDTO);
    if (res == null) {
        log.error("search response is null");
        return null;
    }

    // Transform results
    List<MaterialInfoResDTO> list = new ArrayList<>();
    for (SearchMaterialDTO dto : res.getMaterialDTOS()) {
        MaterialInfoResDTO data = new MaterialInfoResDTO();
        BeanUtils.copyProperties(dto, data);
        list.add(data);
    }

    return list;
}
```

**After (Reactive):**

```java
public Flux<MaterialInfoResDTO> search(MaterialSearchReqDTO searchDTO) {
    log.info("search request: {}", JSON.toJSONString(searchDTO));

    return storeClient.getStoreInfo(searchDTO.getOrgCode())
        .map(storeData -> {
            // Set store type
            StoreTypeEnum storeType = StoreTypeEnum.getByStoreInfo(storeData);
            searchDTO.setStoreType(storeType.getCode());
            return searchDTO;
        })
        .flatMap(searchClient::search)
        .flatMapIterable(MaterialSearchResponse::getMaterialDTOS)
        .map(searchMaterialDTO -> {
            // Transform each item
            MaterialInfoResDTO data = new MaterialInfoResDTO();
            BeanUtils.copyProperties(searchMaterialDTO, data);
            return data;
        })
        .doOnComplete(() -> log.info("search success"))
        .doOnError(e -> log.error("search failed", e));
}
```

### Example 3: Reactive Controller

**Before (Blocking):**

```java
@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @PostMapping("/search")
    public Result<MaterialClerkSearchResp> search(@RequestBody MaterialSearchReq req) {
        try {
            List<MaterialInfoResDTO> list = materialService.search(
                MaterialClerkDtoConverter.req2MaterialSearchReqDTO(req)
            );

            MaterialClerkSearchResp resp = MaterialClerkDtoConverter.dto2MaterialSearchResp(list);
            return Result.success(resp);
        } catch (Exception e) {
            log.error("search failed", e);
            return Result.fromException(e);
        }
    }
}
```

**After (Reactive):**

```java
@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    private MaterialClerkService materialClerkService;

    @PostMapping("/search")
    public Mono<Result<MaterialClerkSearchResp>> search(@RequestBody MaterialSearchReq req) {
        return materialClerkService.search(
                MaterialClerkDtoConverter.req2MaterialSearchReqDTO(req)
            )
            .collectList()
            .map(MaterialClerkDtoConverter::dto2MaterialSearchResp)
            .map(Result::success)
            .doOnSuccess(resp -> log.info("search success: {}", JSON.toJSONString(resp)))
            .doOnError(e -> log.error("search failed", e))
            .onErrorResume(e -> Mono.just(Result.fromException(e)));
    }
}
```

---

## Database Access (R2DBC)

### Configuration

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-r2dbc</artifactId>
</dependency>

<dependency>
    <groupId>io.asyncer</groupId>
    <artifactId>r2dbc-mysql</artifactId>
    <version>1.0.5</version>
</dependency>
```

```properties
# application.properties
spring.r2dbc.url=r2dbcs:mysql://127.0.0.1:3306/test?useSSL=false
spring.r2dbc.username=root
spring.r2dbc.password=password
```

### Repository Interface

```java
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    // Simple queries
    Mono<User> findByUsername(String username);
    Flux<User> findByAge(Integer age);

    // Complex query with @Query
    @Query("""
        SELECT * FROM users
        WHERE (:username IS NULL OR username LIKE CONCAT('%', :username, '%'))
          AND (:email IS NULL OR email = :email)
        ORDER BY id ASC
    """)
    Flux<User> findUsersByComplexConditions(
        @Param("username") String username,
        @Param("email") String email,
        Pageable pageable
    );

    // Custom update query
    @Modifying
    @Query("UPDATE users SET status = :status WHERE id = :id")
    Mono<Integer> updateStatus(@Param("id") Long id, @Param("status") String status);
}
```

### Service Layer

```java
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<User> createUser(User user) {
        return userRepository.save(user)
            .doOnSuccess(u -> log.info("User created: {}", u.getId()))
            .doOnError(e -> log.error("Failed to create user", e));
    }

    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found")));
    }

    public Flux<User> searchUsers(String username, String email) {
        return userRepository.findUsersByComplexConditions(
            username,
            email,
            PageRequest.of(0, 100)
        );
    }

    public Mono<Void> deleteUser(Long id) {
        return userRepository.deleteById(id)
            .doOnSuccess(v -> log.info("User deleted: {}", id));
    }
}
```

---

## Redis Reactive Operations

### Configuration

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis-reactive</artifactId>
</dependency>
```

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisSerializationContext redisSerializationContext() {
        RedisSerializationContext.RedisSerializationContextBuilder builder =
            RedisSerializationContext.newSerializationContext();

        builder.key(StringRedisSerializer.UTF_8);
        builder.value(RedisSerializer.json());
        builder.hashKey(StringRedisSerializer.UTF_8);
        builder.hashValue(StringRedisSerializer.UTF_8);

        return builder.build();
    }

    @Bean
    public ReactiveRedisTemplate reactiveRedisTemplate(
        ReactiveRedisConnectionFactory connectionFactory
    ) {
        RedisSerializationContext serializationContext = redisSerializationContext();
        return new ReactiveRedisTemplate(connectionFactory, serializationContext);
    }
}
```

### Usage

```java
@Service
@RequiredArgsConstructor
public class RedisService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    // Cache user
    public Mono<Boolean> cacheUser(UserDTO user) {
        return redisTemplate.opsForValue()
            .set("user:" + user.getId(), user, Duration.ofMinutes(30));
    }

    // Get cached user
    public Mono<UserDTO> getCachedUser(Long id) {
        return redisTemplate.opsForValue()
            .get("user:" + id)
            .cast(UserDTO.class);
    }

    // Get or fetch pattern
    public Mono<UserDTO> getUserWithCache(Long id) {
        return getCachedUser(id)
            .switchIfEmpty(
                userService.findById(id)
                    .flatMap(user -> cacheUser(user).thenReturn(user))
            );
    }

    // Hash operations
    public Mono<Boolean> hashSet(String key, String field, Object value) {
        return redisTemplate.opsForHash()
            .put(key, field, value);
    }

    public Mono<Object> hashGet(String key, String field) {
        return redisTemplate.opsForHash()
            .get(key, field);
    }
}
```

---

## Dubbo Reactive RPC

### Configuration

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-spring-boot-starter</artifactId>
    <version>3.3.4</version>
</dependency>

<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-reactive</artifactId>
    <version>3.3.4</version>
</dependency>
```

```properties
# application.properties
dubbo.protocol.name=tri
```

### Provider Interface

```java
public interface UserProvider {
    // Reactive method returning Mono
    Mono<UserDTO> getUser(Long id);

    // Reactive method returning Flux
    Flux<UserDTO> getUserList(List<Long> ids);
}
```

### Provider Implementation

```java
@DubboService
public class UserProviderImpl implements UserProvider {

    @Autowired
    private UserService userService;

    @Override
    public Mono<UserDTO> getUser(Long id) {
        return userService.findById(id)
            .map(this::convertToDTO);
    }

    @Override
    public Flux<UserDTO> getUserList(List<Long> ids) {
        return Flux.fromIterable(ids)
            .flatMap(userService::findById)
            .map(this::convertToDTO);
    }
}
```

### Consumer Usage

```java
@Service
public class UserConsumer {

    @DubboReference
    private UserProvider userProvider;

    public Mono<UserDTO> fetchUser(Long id) {
        return userProvider.getUser(id)
            .doOnSuccess(user -> log.info("Fetched user: {}", user))
            .doOnError(e -> log.error("Failed to fetch user", e));
    }
}
```

---

## WebFlux Controllers

### Basic Controller

```java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public Mono<UserDTO> getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @GetMapping
    public Flux<UserDTO> listUsers() {
        return userService.findAll();
    }

    @PostMapping
    public Mono<UserDTO> createUser(@RequestBody @Valid UserDTO userDTO) {
        return userService.create(userDTO);
    }

    @PutMapping("/{id}")
    public Mono<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return userService.update(id, userDTO);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteUser(@PathVariable Long id) {
        return userService.delete(id);
    }
}
```

---

## Error Handling

### Basic Error Handling

```java
public Mono<User> getUser(Long id) {
    return userRepository.findById(id)
        // Return default value on error
        .onErrorReturn(new User())

        // Switch to fallback stream
        .onErrorResume(ex -> getFallbackUser())

        // Transform error
        .onErrorMap(ex -> new CustomException("User not found", ex))

        // Continue on error (skip)
        .onErrorContinue((ex, obj) -> log.error("Error processing: {}", obj, ex));
}
```

### Retry Logic

```java
public Mono<User> getUserWithRetry(Long id) {
    return userRepository.findById(id)
        // Simple retry
        .retry(3)

        // Retry with backoff
        .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
            .maxBackoff(Duration.ofSeconds(2))
            .filter(ex -> ex instanceof TimeoutException)
        );
}
```

### Global Error Handler

```java
@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(EntityNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(404, ex.getMessage());
        return Mono.just(ResponseEntity.status(404).body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse(500, "Internal server error");
        return Mono.just(ResponseEntity.status(500).body(error));
    }
}
```

---

## Advanced Patterns

### Combining Multiple Mono

```java
public Mono<OrderDetailsDTO> getOrderDetails(Long orderId) {
    Mono<Order> orderMono = orderService.findById(orderId);
    Mono<User> userMono = orderMono.flatMap(order -> userService.findById(order.getUserId()));
    Mono<List<Product>> productsMono = orderMono.flatMapMany(order ->
        productService.findByIds(order.getProductIds())
    ).collectList();

    return Mono.zip(orderMono, userMono, productsMono)
        .map(tuple -> {
            Order order = tuple.getT1();
            User user = tuple.getT2();
            List<Product> products = tuple.getT3();

            return new OrderDetailsDTO(order, user, products);
        });
}
```

### Parallel Processing

```java
public Flux<ProcessedData> processInParallel(List<String> ids) {
    return Flux.fromIterable(ids)
        .parallel()
        .runOn(Schedulers.parallel())
        .flatMap(this::processItem)
        .sequential();
}
```

### Timeout Handling

```java
public Mono<User> getUserWithTimeout(Long id) {
    return userService.findById(id)
        .timeout(Duration.ofSeconds(5))
        .onErrorResume(TimeoutException.class,
            ex -> Mono.just(getDefaultUser())
        );
}
```

### Batching

```java
public Flux<List<User>> batchProcessUsers() {
    return userRepository.findAll()
        .buffer(100)  // Process in batches of 100
        .flatMap(batch -> processBatch(batch));
}
```

---

## Performance Tips

1. **Use `subscribeOn()` for blocking I/O:**
   ```java
   Mono.fromCallable(() -> blockingOperation())
       .subscribeOn(Schedulers.boundedElastic())
   ```

2. **Use `publishOn()` for downstream operations:**
   ```java
   flux.publishOn(Schedulers.parallel())
       .map(this::cpuIntensiveOperation)
   ```

3. **Avoid blocking in reactive chains:**
   ```java
   // ❌ Bad
   mono.map(data -> blockingOperation()).subscribe();

   // ✅ Good
   mono.flatMap(data -> Mono.fromCallable(() -> blockingOperation())
       .subscribeOn(Schedulers.boundedElastic())
   ).subscribe();
   ```

4. **Use flatMap for async operations, map for sync:**
   ```java
   // Sync transformation
   mono.map(String::toUpperCase)

   // Async operation
   mono.flatMap(id -> repository.findById(id))
   ```

5. **Enable SQL logging for debugging:**
   ```properties
   logging.level.org.springframework.data.r2dbc=DEBUG
   logging.level.io.r2dbc=DEBUG
   ```

---

## Migration Checklist

- [ ] Replace blocking repositories with `ReactiveCrudRepository`
- [ ] Convert service methods to return `Mono<T>` or `Flux<T>`
- [ ] Update controllers to use reactive types
- [ ] Configure R2DBC connection
- [ ] Set up reactive Redis template
- [ ] Update Dubbo interfaces to reactive
- [ ] Add proper error handling
- [ ] Implement retry logic where needed
- [ ] Add logging for debugging
- [ ] Test with realistic load

---

For more information, refer to:
- [Project Reactor Documentation](https://projectreactor.io/docs/core/release/reference/)
- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [R2DBC Documentation](https://r2dbc.io/)
