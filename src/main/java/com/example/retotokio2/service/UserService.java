package com.example.retotokio2.service;

import com.example.retotokio2.dto.UserDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserDto> getUser(String id);
    Flux<UserDto> getAllUser();
    Mono<UserDto> saveUser(UserDto user);
}
