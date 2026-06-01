package com.projeto.tcc.letramento.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired private MockMvc mockMvc;

    @Test
    @DisplayName("Deve barrar (401) a criação de uma trilha se o usuário for anônimo")
    void createTrail_AnonymousUser_ShouldBeDenied() throws Exception {
        String trailPayload = "{ \"title\": \"Trilha Hacker\", \"description\": \"Tentativa de Injeção\" }";

        mockMvc.perform(post("/api/admin/trails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trailPayload))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve barrar (403) a criação de uma trilha se o usuário estiver logado apenas como ALUNO")
    void createTrail_StudentUser_ShouldBeForbidden() throws Exception {
        String trailPayload = "{ \"title\": \"Trilha Hacker\", \"description\": \"Tentativa de Injeção\" }";

        mockMvc.perform(post("/api/admin/trails")
                        .with(oauth2Login().authorities(() -> "ROLE_ALUNO"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trailPayload))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve permitir o acesso se o usuário estiver autenticado como ADMIN")
    void createTrail_AdminUser_ShouldWithstand() throws Exception {
        String trailPayload = "{ \"title\": \"Trilha Redes Sociais\", \"description\": \"Validando Engenharia Social\" }";

        mockMvc.perform(post("/api/admin/trails")
                        .with(oauth2Login().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(trailPayload))
                .andExpect(status().isCreated());
    }
}