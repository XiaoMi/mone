# Reactor Support for Docean MVC

This document explains how to use Project Reactor's `Mono` and `Flux` types with the Docean MVC framework.

## Overview

The Docean MVC framework now supports reactive return types from controller methods. This allows for non-blocking, reactive processing in your controllers.

## Dependencies

The following dependencies have been added to support reactive programming:

```xml
<dependency>
  <groupId>io.projectreactor</groupId>
  <artifactId>reactor-core</artifactId>
  <version>3.5.10</version>
</dependency>
<dependency>
  <groupId>org.reactivestreams</groupId>
  <artifactId>reactive-streams</artifactId>
  <version>1.0.4</version>
</dependency>
```

## Using Mono and Flux in Controllers

You can now return `Mono` or `Flux` from your controller methods:

```java
@RequestMapping(path = "/api/users")
public Mono<User> getUser(String id) {
    return userService.findUserById(id);
}

@RequestMapping(path = "/api/users/all")
public Flux<User> getAllUsers() {
    return userService.findAllUsers();
}
```

## How It Works

When a controller method returns a `Mono` or `Flux`:

1. For `Mono`: The framework subscribes to the Mono and processes the emitted item when it becomes available.
2. For `Flux`: The framework collects all items into a list using `collectList()` and processes them when all are available.

## Error Handling

Errors in reactive streams are automatically caught and transformed into appropriate HTTP responses with error codes and messages.

## Example Controller

```java
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/api/users/find")
    public Mono<MvcResult<User>> findUser(String username) {
        return userService.findByUsername(username)
                .map(user -> {
                    MvcResult<User> result = new MvcResult<>();
                    result.setCode(200);
                    result.setMessage("Success");
                    result.setData(user);
                    return result;
                })
                .switchIfEmpty(Mono.defer(() -> {
                    MvcResult<User> result = new MvcResult<>();
                    result.setCode(404);
                    result.setMessage("User not found");
                    return Mono.just(result);
                }))
                .onErrorResume(e -> {
                    MvcResult<User> result = new MvcResult<>();
                    result.setCode(500);
                    result.setMessage(e.getMessage());
                    return Mono.just(result);
                });
    }

    @RequestMapping(path = "/api/users/all")
    public Flux<User> getAllUsers() {
        return userService.findAllUsers();
    }
}
```

## Performance Considerations

Using reactive types can improve the performance of your application, especially for I/O-bound operations. However, it's important to ensure that your entire stack supports reactive programming to reap the full benefits. 