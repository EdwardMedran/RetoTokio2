package com.service.impl;

import com.dto.UserDto;
import com.repository.UserRepository;
import com.service.UserService;
import com.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

// Suponiendo que tienes algo como:
// entity: id, name, age, email
// dto: id, nombre, edad, correo
// y que UserMapper.entityToDto / dtoToEntity hacen ese mapeo.

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    // Como el service solo depende del repository, podemos inyectarlo así.
    @InjectMocks
    private UserServiceImpl userService;

    // Helpers de datos
    private com.entity.UserEntity entity1;
    private com.entity.UserEntity entity2;
    private UserDto dto1;
    private UserDto dtoToSave;

    @BeforeEach
    void setUp() {
        entity1 = new com.entity.UserEntity();
        entity1.setId("u1");
        entity1.setName("Alice");
        entity1.setAge(25);
        entity1.setEmail("alice@mail.com");

        entity2 = new com.entity.UserEntity();
        entity2.setId("u2");
        entity2.setName("Bob");
        entity2.setAge(30);
        entity2.setEmail("bob@mail.com");

        dto1 = UserMapper.entityToDto(entity1);

        dtoToSave = new UserDto();
        dtoToSave.setIdentificador(null); // insert suele generar ID
        dtoToSave.setNombre("Charlie");
        dtoToSave.setEdad(22);
        dtoToSave.setCorreo("charlie@mail.com");
    }

    @Test
    void getUser_found_returnsDto() {
        when(userRepository.findById("u1")).thenReturn(Mono.just(entity1));

        Mono<UserDto> result = userService.getUser("u1");

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getIdentificador().equals("u1")
                        && dto.getNombre().equals("Alice")
                        && dto.getEdad() == 25
                        && dto.getCorreo().equals("alice@mail.com"))
                .verifyComplete();

        verify(userRepository).findById("u1");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUser_notFound_emitsCompleteWithoutValue() {
        when(userRepository.findById("nope")).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUser("nope"))
                .verifyComplete();

        verify(userRepository).findById("nope");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAllUser_returnsMappedDtos() {
        when(userRepository.findAll()).thenReturn(Flux.just(entity1, entity2));

        StepVerifier.create(userService.getAllUser())
                .expectNextMatches(dto -> dto.getIdentificador().equals("u1") && dto.getNombre().equals("Alice"))
                .expectNextMatches(dto -> dto.getIdentificador().equals("u2") && dto.getNombre().equals("Bob"))
                .verifyComplete();

        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void saveUser_insertsAndReturnsDto() {
        // Capturamos el entity que el service inserta (mapeado desde dto)
        ArgumentCaptor<com.entity.UserEntity> captor = ArgumentCaptor.forClass(com.entity.UserEntity.class);

        // Simulamos que la BD devuelve el entity ya con ID luego de insert
        com.entity.UserEntity inserted = new com.entity.UserEntity();
        inserted.setId("generated-id-123");
        inserted.setName("Charlie");
        inserted.setAge(22);
        inserted.setEmail("charlie@mail.com");

        when(userRepository.insert(any(com.entity.UserEntity.class))).thenReturn(Mono.just(inserted));

        StepVerifier.create(userService.saveUser(dtoToSave))
                .assertNext(dto -> {
                    assertThat(dto.getIdentificador()).isEqualTo("generated-id-123");
                    assertThat(dto.getNombre()).isEqualTo("Charlie");
                    assertThat(dto.getEdad()).isEqualTo(22);
                    assertThat(dto.getCorreo()).isEqualTo("charlie@mail.com");
                })
                .verifyComplete();

        verify(userRepository).insert(captor.capture());
        com.entity.UserEntity toInsert = captor.getValue();
        assertThat(toInsert.getId()).isNull(); // típico en insert
        assertThat(toInsert.getName()).isEqualTo("Charlie");
        assertThat(toInsert.getAge()).isEqualTo(22);
        assertThat(toInsert.getEmail()).isEqualTo("charlie@mail.com");

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser_completes() {
        when(userRepository.deleteById("u1")).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUser("u1"))
                .verifyComplete();

        verify(userRepository).deleteById("u1");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_found_updatesAndReturnsDto() {
        // find existente
        when(userRepository.findById("u1")).thenReturn(Mono.just(entity1));

        // simula save devolviendo entity actualizado
        com.entity.UserEntity updated = new com.entity.UserEntity();
        updated.setId("u1");
        updated.setName("Alice Updated");
        updated.setAge(26);
        updated.setEmail("alice.updated@mail.com");

        when(userRepository.save(any(com.entity.UserEntity.class))).thenReturn(Mono.just(updated));

        UserDto patch = new UserDto();
        patch.setNombre("Alice Updated");
        patch.setEdad(26);
        patch.setCorreo("alice.updated@mail.com");

        StepVerifier.create(userService.updateUser("u1", patch))
                .expectNextMatches(dto -> dto.getIdentificador().equals("u1")
                        && dto.getNombre().equals("Alice Updated")
                        && dto.getEdad() == 26
                        && dto.getCorreo().equals("alice.updated@mail.com"))
                .verifyComplete();

        verify(userRepository).findById("u1");
        verify(userRepository).save(any(com.entity.UserEntity.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser_notFound_completesEmpty() {
        when(userRepository.findById("nope")).thenReturn(Mono.empty());

        UserDto patch = new UserDto();
        patch.setNombre("X");
        patch.setEdad(1);
        patch.setCorreo("x@mail.com");

        StepVerifier.create(userService.updateUser("nope", patch))
                .verifyComplete(); // Mono.empty()

        verify(userRepository).findById("nope");
        verifyNoMoreInteractions(userRepository);
    }
}
