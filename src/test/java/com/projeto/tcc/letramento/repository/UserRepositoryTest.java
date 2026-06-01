package com.projeto.tcc.letramento.repository;

import com.projeto.tcc.letramento.enums.UserRole;
import com.projeto.tcc.letramento.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("letramento_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Mantido globalmente para evitar falhas na carga de entidades do Hibernate
        registry.add("spring.jpa.properties.hibernate.type.json_format_mapper",
                () -> "com.projeto.tcc.letramento.config.Jackson3FormatMapper");
    }

    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("Deve buscar e retornar um usuário com sucesso pelo e-mail cadastrado")
    void findByEmail_Success() {
        // Arrange
        User user = new User();
        user.setName("Igo Castro");
        user.setEmail("igo.castro@ufopa.edu.br");
        user.setGoogleId("google-oauth-unique-id-1");
        user.setRole(UserRole.ALUNO);
        userRepository.saveAndFlush(user);

        // Act
        Optional<User> found = userRepository.findByEmail("igo.castro@ufopa.edu.br");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Igo Castro", found.get().getName());
        assertEquals("google-oauth-unique-id-1", found.get().getGoogleId());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao buscar por um e-mail que não existe no banco")
    void findByEmail_NotFound() {
        // Act
        Optional<User> found = userRepository.findByEmail("inexistente@email.com");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve buscar e retornar um usuário com sucesso através do Google ID")
    void findByGoogleId_Success() {
        // Arrange
        User user = new User();
        user.setName("Adriele Santos");
        user.setEmail("adriele.santos@ufopa.edu.br");
        user.setGoogleId("google-oauth-unique-id-2");
        user.setRole(UserRole.ALUNO);
        userRepository.saveAndFlush(user);

        // Act
        Optional<User> found = userRepository.findByGoogleId("google-oauth-unique-id-2");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("Adriele Santos", found.get().getName());
        assertEquals("adriele.santos@ufopa.edu.br", found.get().getEmail());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao buscar por um Google ID não cadastrado")
    void findByGoogleId_NotFound() {
        // Act
        Optional<User> found = userRepository.findByGoogleId("google-id-falso-999");

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Deve lançar DataIntegrityViolationException ao tentar cadastrar dois usuários com o mesmo e-mail")
    void save_ShouldThrowException_WhenEmailIsDuplicated() {
        // Arrange - Primeiro usuário persistido normalmente
        User user1 = new User();
        user1.setName("Usuario Um");
        user1.setEmail("duplicado@email.com");
        user1.setGoogleId("google-id-1");
        userRepository.saveAndFlush(user1);

        // Segundo usuário tenta usar o mesmo e-mail
        User user2 = new User();
        user2.setName("Usuario Dois");
        user2.setEmail("duplicado@email.com");
        user2.setGoogleId("google-id-2");

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }

    @Test
    @DisplayName("Deve lançar DataIntegrityViolationException ao tentar cadastrar dois usuários com o mesmo Google ID")
    void save_ShouldThrowException_WhenGoogleIdIsDuplicated() {
        // Arrange - Primeiro usuário persistido normalmente
        User user1 = new User();
        user1.setName("Usuario Alpha");
        user1.setEmail("alpha@email.com");
        user1.setGoogleId("google-id-repetido");
        userRepository.saveAndFlush(user1);

        // Segundo usuário tenta usar o mesmo Google ID
        User user2 = new User();
        user2.setName("Usuario Beta");
        user2.setEmail("beta@email.com");
        user2.setGoogleId("google-id-repetido");

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }
}
