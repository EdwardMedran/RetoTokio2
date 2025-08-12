package com.util;

import com.dto.UserDto;
import com.entity.UserEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    @Test
    public void entityToDto_ok(){
        var entity =  new UserEntity("1", "Alice", 22, "a@a");
        var dto = UserMapper.entityToDto(entity);

        assertThat(dto.getIdentificador()).isEqualTo("1");
        assertThat(dto.getNombre()).isEqualTo("Alice");
        assertThat(dto.getEdad()).isEqualTo(22);
        assertThat(dto.getCorreo()).isEqualTo("a@a");
    }

    @Test
    public void dtoToEntity_ok(){
        var dto = new UserDto("2", "Bob", 30, "a@a");
        var entity = UserMapper.dtoToEntity(dto);

        assertThat(entity.getId()).isEqualTo("2");
        assertThat(entity.getName()).isEqualTo("Bob");
        assertThat(entity.getAge()).isEqualTo(30);
        assertThat(entity.getEmail()).isEqualTo("a@a");
    }
}
