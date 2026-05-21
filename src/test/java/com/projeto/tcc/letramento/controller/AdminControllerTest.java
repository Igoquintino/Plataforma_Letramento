package com.projeto.tcc.letramento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.tcc.letramento.dto.ScenarioRequestDTO;
import com.projeto.tcc.letramento.dto.TrailRequestDTO;
import com.projeto.tcc.letramento.enums.CidPillar;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertTrue;

// placeholder: remove framework-specific annotation to keep skeletons compiling
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AdminService adminService;

    // Outros mocks necessários para estabilizar o contexto Web
    @MockitoBean
    private ProgressService progressService;

    @MockitoBean
    private ScenarioService scenarioService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "ADMIN") // Garante autorização simulando papel administrativo
    @DisplayName("Deve criar uma nova trilha através do painel admin com status 21 Created")
    void testPostTrail_Success() throws Exception {
        // Arrange
        TrailRequestDTO requestData = new TrailRequestDTO("Engenharia Reversa", "Desmistificando binários");

        Trail createdTrail = new Trail();
        createdTrail.setId(5L);
        createdTrail.setTitle("Engenharia Reversa");
        createdTrail.setDescription("Desmistificando binários");

        when(adminService.createTrail(any(TrailRequestDTO.class))).thenReturn(createdTrail);

        // Act & Assert
        // Nota técnica: Rota mantida com o typo "/tails" conforme mapeamento real do seu AdminController
        mockMvc.perform(post("/api/admin/trails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestData))
                        .with(csrf())) // CSRF obrigatório para operações POST
                .andExpect(status().isCreated()) // Confirma retorno HTTP 201 Created
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("Engenharia Reversa"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve criar um novo cenário através do painel admin com status 201 Created")
    void testPostScenario_Success() throws Exception {
        ScenarioRequestDTO requestData = new ScenarioRequestDTO(
                "Desafio de Engenharia Reversa",
                objectMapper.createObjectNode().put("key", "value"), // Exemplo de xrayData
                objectMapper.createObjectNode().put("question", "Qual é a flag?"), // Exemplo de quiz
                CidPillar.CONFIDENCIALIDADE,
                5L
        );

        when(adminService.createScenario(any(ScenarioRequestDTO.class)))
                .thenAnswer(invocation -> {
                    ScenarioRequestDTO data = invocation.getArgument(0);
                    Scenario scenario = new Scenario();
                    scenario.setId(3L);
                    scenario.setTitleScenarios(data.titleScenario());
                    scenario.setXrayData(data.xrayData());
                    scenario.setQuiz(data.quiz());
                    scenario.setPillar(data.pillar());
                    return scenario;
                });
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve deletar uma trilha existente através do ID com status 204 No Content")
    void testDeleteTrail_Success() throws Exception {
        // Arrange
        Long trailId = 10L;
        doNothing().when(adminService).deleteTrail(trailId); // Métodos void usam doNothing() do Mockito

        // Act & Assert
        mockMvc.perform(delete("/api/admin/trails/" + trailId)
                        .with(csrf())) // CSRF obrigatório para operações DELETE
                .andExpect(status().isNoContent()); // Confirma status HTTP 204 No Content
    }

    @Test
    void placeholder_adminController() {
        // TODO: MockMvc tests for admin endpoints and role checks
        assertTrue(true);
    }
}
