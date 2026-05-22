package com.projeto.tcc.letramento.controller;

import tools.jackson.databind.JsonNode;              // ✅ Jackson 3
import tools.jackson.databind.ObjectMapper;          // ✅ Jackson 3
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    // ✅ Um único ObjectMapper Jackson 3 para toda a classe
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser
    @DisplayName("Deve retornar status 200 e 'Correto!' para resposta certa")
    void testPostAnswer_Correct() throws Exception {
        // ARRANGE
        Long scenarioId = 1L;
        JsonNode answersNode = objectMapper.createObjectNode().put("answer", "phishing");

        // ✅ any(JsonNode.class) agora referencia tools.jackson.databind.JsonNode — sem conflito de tipos
        when(scenarioService.compareUserResponse(eq(scenarioId), any(JsonNode.class))).thenReturn(true);

        String jsonPayload = "{\"scenarioId\":" + scenarioId + ",\"answers\":" + objectMapper.writeValueAsString(answersNode) + "}";

        // ACT & ASSERT
        mockMvc.perform(post("/api/scenarios/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Correto!"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar status 200 e mensagem de erro amigável para resposta incorreta")
    void testPostAnswer_Incorrect() throws Exception {
        // ARRANGE
        String jsonAnswer = "{\"scenarioId\": 1, \"answers\": {\"answer\": \"spam\"}}";

        when(scenarioService.compareUserResponse(eq(1L), any(JsonNode.class))).thenReturn(false);

        // ACT & ASSERT
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