package com.projeto.tcc.letramento.repository;

import com.projeto.tcc.letramento.enums.CidPillar;
import com.projeto.tcc.letramento.enums.ProgressStatus;
import com.projeto.tcc.letramento.enums.UserRole;
import com.projeto.tcc.letramento.model.Progress;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
import com.projeto.tcc.letramento.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;           // ✅ Boot 4
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase; // ✅ Boot 4
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ProgressRepositoryTest {

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

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TrailRepository trailRepository;

    @Autowired
    private ScenarioRepository scenarioRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user;
    private Scenario scenario;

    @BeforeEach
    void setUp() {
        // 1. Usuário
        user = new User();
        user.setName("Igo Quintino Castro Prata");
        user.setEmail("igo.teste@ufopa.edu.br");
        user.setGoogleId("google-test-123");
        user.setCourse("Sistemas de Informação");
        user.setAcademicLevel("Graduação");
        user.setRole(UserRole.ALUNO);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        // 2. Trilha
        Trail trail = new Trail();
        trail.setTitle("Trilha de Autenticação Segura");
        trail.setDescription("Conceitos base de JWT e OAuth");
        trail = trailRepository.save(trail);

        // 3. Cenário
        ObjectNode jsonNode = objectMapper.createObjectNode();
        jsonNode.put("exemplo_chave", "valor_teste");

        scenario = new Scenario();
        scenario.setTitleScenarios("Cenário de Análise de JWT");
        scenario.setXrayData(jsonNode);
        scenario.setQuiz(jsonNode);
        scenario.setPillar(CidPillar.CONFIDENCIALIDADE);
        scenario.setTrail(trail);
        scenario = scenarioRepository.save(scenario);
    }

    @Test
    @DisplayName("Deve retornar uma lista populada de Progress buscando pelo ID do usuário")
    void findByUserId_Success() {
        // Arrange
        Progress progress = new Progress();
        progress.setUser(user);
        progress.setScenario(scenario);
        progress.setStatus(ProgressStatus.IN_PROGRESS);
        progressRepository.save(progress);

        // Act
        List<Progress> results = progressRepository.findByUserId(user.getId());

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(user.getId(), results.get(0).getUser().getId());
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando o usuário não tiver nenhum progresso salvo")
    void findByUserId_Empty() {
        // Act
        List<Progress> results = progressRepository.findByUserId(user.getId());

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar o progresso exato ao buscar pelo ID do usuário e do cenário")
    void findByUserIdAndScenarioId_Success() {
        // Arrange
        Progress progress = new Progress();
        progress.setUser(user);
        progress.setScenario(scenario);
        progress.setStatus(ProgressStatus.COMPLETED);
        progress.setQuizScore(new BigDecimal("100.00"));
        progressRepository.save(progress);

        // Act
        Optional<Progress> result = progressRepository.findByUserIdAndScenarioId(user.getId(), scenario.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(ProgressStatus.COMPLETED, result.get().getStatus());
        assertEquals(new BigDecimal("100.00"), result.get().getQuizScore());
        assertEquals(scenario.getId(), result.get().getScenario().getId());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao buscar Progresso usando um ID de cenário não iniciado")
    void findByUserIdAndScenarioId_NotFound() {
        // Arrange
        Progress progress = new Progress();
        progress.setUser(user);
        progress.setScenario(scenario);
        progress.setStatus(ProgressStatus.IN_PROGRESS);
        progressRepository.save(progress);

        Long nonExistentScenarioId = 9999L;

        // Act
        Optional<Progress> result = progressRepository.findByUserIdAndScenarioId(user.getId(), nonExistentScenarioId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void placeholder_findByUserId() {
        // TODO: tests for findByUserId and concurrency behaviors
        assertTrue(true);
    }
}