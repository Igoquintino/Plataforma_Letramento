package com.projeto.tcc.letramento.controller;

import org.junit.jupiter.api.Test;
import com.projeto.tcc.letramento.model.Trail;
import com.projeto.tcc.letramento.service.ProgressService;
import com.projeto.tcc.letramento.service.ScenarioService;
import com.projeto.tcc.letramento.service.TrailService;
import com.projeto.tcc.letramento.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrailController.class)
class TrailControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private TrailService trailService;
    @MockitoBean private ProgressService progressService;
    @MockitoBean private ScenarioService scenarioService;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser
    @DisplayName("Deve retornar a lista de todas as trilhas cadastradas")
    void testGetAllTrails_Success() throws Exception {
        // Arrange
        Trail trail = new Trail();
        trail.setId(1L);
        trail.setTitle("Trilha de Phishing");
        trail.setDescription("Aprenda a detectar e-mails falsos");

        when(trailService.findAllActiveTrails()).thenReturn(List.of(trail));

        // Act & Assert
        mockMvc.perform(get("/api/trails")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Trilha de Phishing"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar a porcentagem de progresso da trilha para um aluno")
    void testGetTrailProgress_Success() throws Exception {
        // Arrange
        Long trailId = 1L;
        Long userId = 2L;
        Double expectedProgress = 75.0; // 75% concluído

        when(trailService.calculateTrailProgress(userId, trailId)).thenReturn(expectedProgress);

        // Act & Assert
        mockMvc.perform(get("/api/trails/" + trailId + "/progress/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("75.0")); // Como retorna um Double bruto, checamos o texto plano da resposta
    }
}
