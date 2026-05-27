package com.projeto.tcc.letramento.controller;

import com.projeto.tcc.letramento.enums.CidPillar;
import com.projeto.tcc.letramento.enums.ProgressStatus;
import com.projeto.tcc.letramento.enums.UserRole;
import com.projeto.tcc.letramento.model.*;
import com.projeto.tcc.letramento.repository.*;
import org.junit.jupiter.api.BeforeEach;
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
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.google.client-id=mock-id",
        "spring.security.oauth2.client.registration.google.client-secret=mock-secret"
})
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class ScenarioAnswerIntegrationTest {
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

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private TrailRepository trailRepository;
    @Autowired private ScenarioRepository scenarioRepository;
    @Autowired private ProgressRepository progressRepository;

    private Scenario scenario;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // 🛠️ ATENÇÃO EXCLUSIVA DE Q.A: Criamos o usuário com ID 1L para bater com o ID fixo do Controller!
        User user = new User();
        user.setId(1L);
        user.setName("Igo Quintino");
        user.setEmail("igo@ufopa.edu.br");
        user.setGoogleId("google-id-fixo-1");
        user.setRole(UserRole.ALUNO);
        userRepository.saveAndFlush(user);

        Trail trail = new Trail();
        trail.setTitle("Trilha de JWT");
        trail.setDescription("Letramento de Vulnerabilidades");
        trail = trailRepository.saveAndFlush(trail);

        ObjectNode quizNode = mapper.createObjectNode();
        quizNode.put("correct_answer", "option_A");

        scenario = new Scenario();
        scenario.setTitleScenarios("Desafio Token Assinado");
        scenario.setXrayData(quizNode);
        scenario.setQuiz(quizNode);
        scenario.setPillar(CidPillar.CONFIDENCIALIDADE);
        scenario.setTrail(trail);
        scenario = scenarioRepository.saveAndFlush(scenario);

        // Inicializa o progresso como IN_PROGRESS para o completeScenario funcionar
        Progress progress = new Progress();
        progress.setUser(user);
        progress.setScenario(scenario);
        progress.setStatus(ProgressStatus.IN_PROGRESS);
        progressRepository.saveAndFlush(progress);
    }

    @Test
    @DisplayName("Deve computar resposta certa, retornar status 200 e gravar status COMPLETED no banco Docker")
    void postAnswer_CorrectFlow_Success() throws Exception {
        // payload correspondente ao AnswerDTO usando Jackson 3 no corpo do request HTTP
        String dtoString = "{\"scenarioId\":" + scenario.getId() + ",\"answers\":{\"answer\":\"option_A\"}}";

        mockMvc.perform(post("/api/scenarios/answer")
                        .with(oauth2Login()) // Simula a sessão ativa do usuário
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoString))
                .andExpect(status().isOk())
                .andExpect(content().string("Correto!"));

        // Validação física: Garante que o banco do container foi atualizado pelo fluxo completo
        Progress finalProgress = progressRepository.findByUserIdAndScenarioId(1L, scenario.getId()).orElseThrow();
        assertEquals(ProgressStatus.COMPLETED, finalProgress.getStatus());
        assertEquals(0, finalProgress.getQuizScore().compareTo(new java.math.BigDecimal("100.00")));
    }
}
