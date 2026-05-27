package com.projeto.tcc.letramento.security;

import com.projeto.tcc.letramento.model.User;
import com.projeto.tcc.letramento.repository.UserRepository;
import com.projeto.tcc.letramento.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.google.client-id=mock-google-id",
        "spring.security.oauth2.client.registration.google.client-secret=mock-google-secret"
})
@Testcontainers
@ActiveProfiles("test")
class OAuth2PostLoginTest {

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
        registry.add("spring.jpa.properties.hibernate.type.json_format_mapper",
                () -> "com.projeto.tcc.letramento.config.Jackson3FormatMapper");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Deve cadastrar automaticamente o usuário no banco ao fazer o primeiro login via Google OAuth2")
    void processOAuthPostLogin_ShouldCreateUser_WhenFirstAccess() {
        String email = "aluno.novo@ufopa.edu.br";
        String name = "Novo Aluno Letramento";
        String googleId = "sub-google-id-12345";

        // Act - Executa o methodo chamado no sucesso do OAuth2
        User loggedUser = userService.processOAuthPostLogin(email, name, googleId);

        // Assert
        assertNotNull(loggedUser.getId());
        assertEquals(name, loggedUser.getName());

        // Confere se realmente persistiu fisicamente no Postgres do Testcontainers
        Optional<User> dbUser = userRepository.findByGoogleId(googleId);
        assertTrue(dbUser.isPresent());
        assertEquals(email, dbUser.get().getEmail());
    }

    @Test
    void placeholder_oauth2PostLogin() {
        // TODO: tests for processOAuthPostLogin behavior
        assertTrue(true);
    }
}
