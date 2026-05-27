package com.projeto.tcc.letramento.e2e;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.google.client-id=mock-id",
        "spring.security.oauth2.client.registration.google.client-secret=mock-secret"
})
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class ApplicationSmokeTest {

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
    private MockMvc mockMvc;

    @Test
    @DisplayName("E2E SMOKE TEST — O sistema deve inicializar o contexto completo e responder à rota pública de trilhas")
    void application_ShouldBootAndRespondToPublicEndpoint_Success() throws Exception {

        mockMvc.perform(get("/api/trails")
                        .accept(MediaType.APPLICATION_JSON))
                // Se o banco estiver ativo, o Hibernate mapeado e os filtros de pé, deve retornar 200 OK
                .andExpect(status().isOk());
    }
}