package com.service;

import com.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserDto> getUser(String id);
    Flux<UserDto> getAllUser();
    Mono<UserDto> saveUser(UserDto user);
}
