package com.service.impl;

import com.dto.UserDto;
import com.repository.UserRepository;
import com.service.UserService;
import com.util.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDto> getUser(String id) {
        return this.userRepository.findById(id)
                .map(UserMapper::entityToDto);
//                .map(userEntity -> UserMapper.entityToDto(userEntity));
    }

    @Override
    public Flux<UserDto> getAllUser() {
        return this.userRepository.findAll()
                .map(UserMapper::entityToDto);
              //.map(userEntity -> UserMapper.entityToDto(userEntity));
    }

    @Override
    public Mono<UserDto> saveUser(UserDto user) {
        var entity = UserMapper.dtoToEntity(user);
        return this.userRepository.insert(entity)
                .map(UserMapper::entityToDto);
//              .map(userDto -> UserMapper.entityToDto(userDto));
    }
}
