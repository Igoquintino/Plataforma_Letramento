package com.projeto.tcc.letramento.controller;

import com.projeto.tcc.letramento.enums.CidPillar;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.User;
import com.projeto.tcc.letramento.service.UserService;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScenarioController.class)
class ScenarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private ScenarioService scenarioService;
    @MockitoBean private ProgressService progressService;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;
    @MockitoBean private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String MOCK_SUB_ID = "google-oauth-sub-12345";

    @Test
    @WithMockUser
    @DisplayName("Deve retornar status 200 e 'Correto!' para resposta certa")
    void testPostAnswer_Correct() throws Exception {
        // ARRANGE
        Long scenarioId = 1L;
        JsonNode answersNode = objectMapper.createObjectNode().put("answer", "phishing");

        User dummyUser = new User();
        dummyUser.setId(55L);
        when(userService.findByGoogleId(anyString())).thenReturn(dummyUser);

        when(scenarioService.compareUserResponse(eq(scenarioId), any(JsonNode.class))).thenReturn(true);

        String jsonPayload = "{\"scenarioId\":" + scenarioId + ",\"answers\":" + objectMapper.writeValueAsString(answersNode) + "}";

        // ACT & ASSERT
        mockMvc.perform(post("/api/scenarios/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload)
                        .with(oauth2Login().attributes(attrs -> attrs.put("sub", MOCK_SUB_ID)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Correto!"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar status 200 e mensagem de erro amigável para resposta incorreta")
    void testPostAnswer_Incorrect() throws Exception {
        // ARRANGE
        Long scenarioId = 1L;

        User dummyUser = new User();
        dummyUser.setId(55L);
        when(userService.findByGoogleId(anyString())).thenReturn(dummyUser);
        when(scenarioService.compareUserResponse(eq(scenarioId), any(JsonNode.class))).thenReturn(false);

        String jsonAnswer = "{\"scenarioId\": 1, \"answers\": {\"answer\": \"spam\"}}";

        // ACT & ASSERT
        mockMvc.perform(post("/api/scenarios/answer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAnswer)
                        .with(oauth2Login().attributes(attrs -> attrs.put("sub", MOCK_SUB_ID)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Incorreto. Tente analisar o Raio-X novamente."));
    }

    @Test
    @DisplayName("Deve retornar 200 OK e o DTO estruturado do cenário ao buscar pelo modo Raio-X")
    void testGetXRay_Success() throws Exception {
        Long scenarioId = 10L;
        Scenario mockScenario = new Scenario();
        mockScenario.setId(scenarioId);
        mockScenario.setTitleScenarios("Ataque de Phishing por DNS");
        mockScenario.setPillar(CidPillar.INTEGRIDADE);
        mockScenario.setXrayData(objectMapper.createObjectNode().put("domain", "fake-ufopa.edu.br"));

        when(scenarioService.getScenarioWithXray(scenarioId)).thenReturn(mockScenario);

        mockMvc.perform(get("/api/scenarios/" + scenarioId + "/xray")
                        .with(oauth2Login())
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(scenarioId))
                .andExpect(jsonPath("$.title").value("Ataque de Phishing por DNS"))
                .andExpect(jsonPath("$.pillar").value("INTEGRIDADE"));
    }
}