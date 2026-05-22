package com.projeto.tcc.letramento.controller;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;           // ✅ Jackson 3
import com.projeto.tcc.letramento.dto.ScenarioRequestDTO;
import com.projeto.tcc.letramento.enums.CidPillar;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
import com.projeto.tcc.letramento.dto.TrailRequestDTO;
import com.projeto.tcc.letramento.service.AdminService;
import com.projeto.tcc.letramento.service.ProgressService;
import com.projeto.tcc.letramento.service.ScenarioService;
import com.projeto.tcc.letramento.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // ✅ Um único ObjectMapper Jackson 3 para toda a classe
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private ProgressService progressService;

    @MockitoBean
    private ScenarioService scenarioService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    // =========================================================
    // POST /api/admin/trails
    // =========================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve criar uma nova trilha através do painel admin com status 201 Created")
    void testPostTrail_Success() throws Exception {
        // Arrange
        TrailRequestDTO requestData = new TrailRequestDTO("Engenharia Reversa", "Desmistificando binários");

        Trail createdTrail = new Trail();
        createdTrail.setId(5L);
        createdTrail.setTitle("Engenharia Reversa");
        createdTrail.setDescription("Desmistificando binários");

        when(adminService.createTrail(any(TrailRequestDTO.class))).thenReturn(createdTrail);

        // Act & Assert
        mockMvc.perform(post("/api/admin/trails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestData)) // ✅ usa o único objectMapper
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("Engenharia Reversa"));
    }

    // =========================================================
    // POST /api/admin/scenarios
    // =========================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve criar um novo cenário através do painel admin com status 201 Created")
    void testPostScenario_Success() throws Exception {

        // 1. ARRANGE
        // ✅ createObjectNode() é o mesmo da API do Jackson 2 — funciona igual no Jackson 3
        JsonNode xrayNode = objectMapper.createObjectNode().put("key", "value");
        JsonNode quizNode = objectMapper.createObjectNode().put("question", "Qual é a flag?");

        ScenarioRequestDTO requestData = new ScenarioRequestDTO(
                "Desafio de Engenharia Reversa",
                xrayNode,
                quizNode,
                CidPillar.CONFIDENCIALIDADE,
                5L
        );

        when(adminService.createScenario(any(ScenarioRequestDTO.class)))
                .thenAnswer(invocation -> {
                    ScenarioRequestDTO data = invocation.getArgument(0);

                    System.out.println("\n==================================================");
                    System.out.println("[MOCK SERVICE] Capturando dados enviados pelo Controller:");
                    System.out.println("Título do Cenário: " + data.titleScenario());
                    System.out.println("Pilar de Segurança: " + data.pillar());
                    System.out.println("==================================================");

                    Scenario scenario = new Scenario();
                    scenario.setId(3L);
                    scenario.setTitleScenarios(data.titleScenario());
                    scenario.setXrayData(data.xrayData());
                    scenario.setQuiz(data.quiz());
                    scenario.setPillar(data.pillar());
                    return scenario;
                });

        // 2. ACT
        var resultActions = mockMvc.perform(post("/api/admin/scenarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestData)) // ✅ usa o único objectMapper
                .with(csrf()));

        // 3. ASSERT
        String jsonRespostaBruta = resultActions.andReturn().getResponse().getContentAsString();

        System.out.println("\n==================================================");
        System.out.println("[HTTP RESPONSE] JSON final devolvido pelo AdminController:");
        System.out.println(jsonRespostaBruta);
        System.out.println("==================================================\n");

        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.titleScenarios").value("Desafio de Engenharia Reversa"));
    }

    // =========================================================
    // DELETE /api/admin/trails/{id}
    // =========================================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve deletar uma trilha existente através do ID com status 204 No Content")
    void testDeleteTrail_Success() throws Exception {
        // Arrange
        Long trailId = 10L;
        doNothing().when(adminService).deleteTrail(trailId);

        // Act & Assert
        mockMvc.perform(delete("/api/admin/trails/" + trailId)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        System.out.println("Teste de exclusão de trilha com ID " + trailId + " passou com sucesso.");
    }

    // =========================================================
    // Placeholder
    // =========================================================

    @Test
    void placeholder_adminController() {
        // TODO: adicionar testes de autorização por papel (ADMIN vs USER)
        assertTrue(true);
    }
}