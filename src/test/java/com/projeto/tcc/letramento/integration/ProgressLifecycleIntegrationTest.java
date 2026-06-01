package com.projeto.tcc.letramento.integration;

import com.projeto.tcc.letramento.enums.CidPillar;
import com.projeto.tcc.letramento.enums.ProgressStatus;
import com.projeto.tcc.letramento.model.Progress;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
import com.projeto.tcc.letramento.model.User;
import com.projeto.tcc.letramento.enums.UserRole;
import com.projeto.tcc.letramento.repository.ProgressRepository;
import com.projeto.tcc.letramento.repository.ScenarioRepository;
import com.projeto.tcc.letramento.repository.TrailRepository;
import com.projeto.tcc.letramento.repository.UserRepository;
import com.projeto.tcc.letramento.service.ProgressService;
import com.projeto.tcc.letramento.service.TrailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ProgressLifecycleIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("letramento_integration")
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

    @Autowired private ProgressService progressService;
    @Autowired private TrailService trailService;
    @Autowired private UserRepository userRepository;
    @Autowired private TrailRepository trailRepository;
    @Autowired private ScenarioRepository scenarioRepository;
    @Autowired private ProgressRepository progressRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("INTEGRAÇÃO — Deve rodar o ciclo de vida completo do progresso de um aluno com sucesso")
    void fullProgressLifecycle_IntegrationFlow() {
        // 1. CADASTRAR INFRAESTRUTURA BASE NO BANCO REAL
        User student = new User();
        student.setName("Aluno Teste Integração");
        student.setEmail("aluno.tcc@ufopa.edu.br");
        student.setGoogleId("google-id-lifecycle-999");
        student.setRole(UserRole.ALUNO);
        student.setCreatedAt(LocalDateTime.now());
        student = userRepository.saveAndFlush(student);

        Trail trail = new Trail();
        trail.setTitle("Trilha de Teste de Ciclo de Vida");
        trail.setDescription("Validando o fluxo de ponta a ponta");
        trail = trailRepository.saveAndFlush(trail);

        Scenario scenario = new Scenario();
        scenario.setTitleScenarios("Cenário Phishing Bancário");
        scenario.setPillar(CidPillar.CONFIDENCIALIDADE);
        scenario.setTrail(trail);
        scenario.setXrayData(mapper.createObjectNode().put("target", "Simulação de Login"));
        scenario.setQuiz(mapper.createObjectNode().put("question", "É um golpe?"));
        scenario = scenarioRepository.saveAndFlush(scenario);

        // 2. PASSO 1: O Aluno inicia o cenário
        progressService.startUserProgress(student.getId(), scenario.getId());

        List<Progress> dashboardAntes = progressRepository.findByUserId(student.getId());
        assertEquals(1, dashboardAntes.size());
        assertEquals(ProgressStatus.IN_PROGRESS, dashboardAntes.get(0).getStatus());

        // 3. PASSO 2: O Aluno conclui o cenário acertando o desafio
        progressService.completeScenario(
                student.getId(),
                scenario.getId(),
                BigDecimal.valueOf(10.0),
                "Excelente análise do Raio-X!",
                120L);

        // 4. PASSO 3: Validar os impactos e as métricas geradas no banco de dados real
        List<Progress> dashboardDepois = progressRepository.findByUserId(student.getId());
        assertEquals(1, dashboardDepois.size());
        assertEquals(ProgressStatus.COMPLETED, dashboardDepois.get(0).getStatus());
        assertEquals(120L, dashboardDepois.get(0).getTimeSpent());
        assertEquals("Excelente análise do Raio-X!", dashboardDepois.get(0).getUserFeedback());

        // 5. PASSO 4: Validar se o cálculo estatístico da trilha computou os 100% corretamente
        Double progressoTrilha = trailService.calculateTrailProgress(student.getId(), trail.getId());
        assertEquals(100.0, progressoTrilha, 0.001);
    }
}
