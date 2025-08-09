package com.util;

import com.dto.UserDto;
import com.entity.UserEntity;

public class UserMapper {
    public static UserDto entityToDto(UserEntity user){
        final UserDto dto = new UserDto();
        dto.setIdentificador(user.getId());
        dto.setNombre(user.getName());
        dto.setEdad(user.getAge());
        dto.setCorreo(user.getEmail());
        return dto;
    }

    public static UserEntity dtoToEntity(UserDto dto){
        final UserEntity user = new UserEntity();
        user.setId(dto.getIdentificador());
        user.setName(dto.getNombre());
        user.setAge(dto.getEdad());
        user.setEmail(dto.getCorreo());
        return user;
    }
}
