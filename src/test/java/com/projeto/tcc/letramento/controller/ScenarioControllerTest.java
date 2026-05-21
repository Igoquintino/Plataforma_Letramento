package com.projeto.tcc.letramento.controller;

import com.projeto.tcc.letramento.service.ProgressService;
import com.projeto.tcc.letramento.service.ScenarioService;
import com.projeto.tcc.letramento.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertTrue;

// placeholder: remove framework-specific annotation to keep skeletons compiling
@WebMvcTest(ScenarioController.class)
class ScenarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScenarioService scenarioService;

    @MockitoBean
    private ProgressService progressService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser
    @DisplayName("Deve retornar status 200 e 'Correto!' para resposta certa")
    void testPostAnswer_Correct() throws Exception {
        // Arrange
        Long scenarioId = 1L;
        String jsonAnswer = "{\"scenarioId\": 1, \"answers\": {\"answer\": \"phishing\"}}";

        // Simulamos que a Service validou como true
        when(scenarioService.compareUserResponse(eq(scenarioId), any())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/scenarios/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAnswer)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Correto!"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar status 200 e mensagem de erro amigável para resposta incorreta")
    void testPostAnswer_Incorrect() throws Exception {
        // Arrange
        String jsonAnswer = "{\"scenarioId\": 1, \"answers\": {\"answer\": \"spam\"}}";

        // Simulamos que a Service validou como false
        when(scenarioService.compareUserResponse(eq(1L), any())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/scenarios/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAnswer)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Incorreto. Tente analisar o Raio-X novamente."));
    }

    @Test
    void placeholder_scenarioController() {
        // TODO: MockMvc tests for /api/scenarios endpoints
        assertTrue(true);
    }
}
