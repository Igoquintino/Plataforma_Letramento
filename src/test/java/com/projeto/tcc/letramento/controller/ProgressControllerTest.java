package com.projeto.tcc.letramento.controller;

import com.projeto.tcc.letramento.enums.ProgressStatus;
import com.projeto.tcc.letramento.model.Progress;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertTrue;

// placeholder: remove framework-specific annotation to keep skeletons compiling
@WebMvcTest(ProgressController.class) // 1. Foca apenas no controller de progresso
class ProgressControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ProgressService progressService;

        // Mocks de segurança necessários para o Spring Boot carregar o contexto da Web sem estourar erros
        @MockitoBean
        private ScenarioService scenarioService;

        @MockitoBean
        private JwtTokenProvider jwtTokenProvider;

        @Test
        @WithMockUser // Simula o usuário autenticado para passar pelos filtros de segurança
        @DisplayName("Deve retornar a lista de progresso do dashboard do aluno com status 200")
        void testGetDashboard_Success() throws Exception {
            // Arrange (Preparação)
            Long userId = 1L;

            // Criamos um objeto de progresso falso para fingir que veio do banco
            Progress mockProgress = new Progress();
            mockProgress.setId(10L);
            mockProgress.setStatus(ProgressStatus.COMPLETED);
            mockProgress.setQuizScore(new BigDecimal("100.00"));
            mockProgress.setTimeSpent(45L); // 45 segundos
            mockProgress.setUserFeedback("Ótimo simulador Raio-X");
            mockProgress.setCompletedAt(LocalDateTime.now());

            // Enfeitiçamos o serviço: "Quando perguntarem pelo progresso do usuário 1, retorne a lista com nosso mock"
            when(progressService.getStudentDashboard(userId)).thenReturn(List.of(mockProgress));

            // Act & Assert (Ação e Validação)
            // Como a rota mapeia "/api/progress/dashboard/{userId}", passamos o ID na URL
            mockMvc.perform(get("/api/progress/dashboard/" + userId)
                            .contentType(MediaType.APPLICATION_JSON).with(csrf()))
                    .andExpect(status().isOk()) // Confirma que retornou HTTP 200

                    // 💡 NOVIDADE DE Q.A: jsonPath ajuda a navegar dentro do JSON retornado para inspecionar os campos
                    // Como retorna uma lista [], $[0] significa "o primeiro item da lista"
                    .andExpect(jsonPath("$[0].id").value(10))
                    .andExpect(jsonPath("$[0].status").value("COMPLETED"))
                    .andExpect(jsonPath("$[0].quizScore").value(100.00))
                    .andExpect(jsonPath("$[0].TimeSpent").value(45L))
                    .andExpect(jsonPath("$[0].userFeedback").value("Ótimo simulador Raio-X"));
        }

    @Test
    void placeholder_progressController() {
        // TODO: MockMvc tests for progress dashboard endpoints
        assertTrue(true);
    }
}
