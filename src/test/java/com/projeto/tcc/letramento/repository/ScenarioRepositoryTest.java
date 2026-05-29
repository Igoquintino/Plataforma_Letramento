package com.projeto.tcc.letramento.repository;

import com.projeto.tcc.letramento.enums.CidPillar;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ScenarioRepositoryTest {

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

    @Autowired private TrailRepository trailRepository;
    @Autowired private ScenarioRepository scenarioRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Trail mainTrail;

    @BeforeEach
    void setUp() {
        mainTrail = new Trail();
        mainTrail.setTitle("Trilha de Autenticação Segura");
        mainTrail.setDescription("Conceitos base de JWT e OAuth");
        mainTrail = trailRepository.save(mainTrail);
    }

    @Test
    @DisplayName("Deve retornar uma lista de cenários pertencentes a uma trilha específica")
    void findByTrailId_Success() {
        // Arrange
        ObjectNode emptyJson = objectMapper.createObjectNode();

        Scenario scenario1 = new Scenario();
        scenario1.setTitleScenarios("Cenário de Injeção de SQL");
        scenario1.setXrayData(emptyJson);
        scenario1.setQuiz(emptyJson);
        scenario1.setPillar(CidPillar.INTEGRIDADE);
        scenario1.setTrail(mainTrail);

        Scenario scenario2 = new Scenario();
        scenario2.setTitleScenarios("Cenário de Quebra de JWT");
        scenario2.setXrayData(emptyJson);
        scenario2.setQuiz(emptyJson);
        scenario2.setPillar(CidPillar.CONFIDENCIALIDADE);
        scenario2.setTrail(mainTrail);

        scenarioRepository.saveAll(List.of(scenario1, scenario2));

        // Act
        List<Scenario> result = scenarioRepository.findByTrailId(mainTrail.getId());

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(s -> s.getTitleScenarios().equals("Cenário de Injeção de SQL")));
        assertTrue(result.stream().anyMatch(s -> s.getTitleScenarios().equals("Cenário de Quebra de JWT")));
    }

    @Test
    @DisplayName("Deve filtrar corretamente os cenários retornando apenas os da trilha pesquisada")
    void findByTrailId_IsolationFilter() {

        Trail alternativeTrail = new Trail();
            alternativeTrail.setTitle("Trilha de Inteligência Artificial");
            alternativeTrail.setDescription("Estudo de Engenharia de Prompt");
        alternativeTrail = trailRepository.save(alternativeTrail);

        ObjectNode emptyJson = objectMapper.createObjectNode();

        Scenario mainScenario = new Scenario();
            mainScenario.setTitleScenarios("Ataque de Phishing");
            mainScenario.setXrayData(emptyJson);
            mainScenario.setQuiz(emptyJson);
            mainScenario.setTrail(mainTrail);
            scenarioRepository.save(mainScenario);

        Scenario altScenario = new Scenario();
            altScenario.setTitleScenarios("Envenenamento de Dados na IA");
            altScenario.setXrayData(emptyJson);
            altScenario.setQuiz(emptyJson);
            altScenario.setTrail(alternativeTrail);
            scenarioRepository.save(altScenario);

        // Act
        List<Scenario> resultMain = scenarioRepository.findByTrailId(mainTrail.getId());
        List<Scenario> resultAlt = scenarioRepository.findByTrailId(alternativeTrail.getId());

        // Assert
        assertEquals(1, resultMain.size());
        assertEquals("Ataque de Phishing", resultMain.get(0).getTitleScenarios());

        assertEquals(1, resultAlt.size());
        assertEquals("Envenenamento de Dados na IA", resultAlt.get(0).getTitleScenarios());
    }

    @Test
    @DisplayName("Deve salvar e ler com sucesso uma estrutura complexa de JSONB usando Jackson 3")
    void saveAndReadJsonbFields_Success() {

        ObjectNode complexQuiz = objectMapper.createObjectNode();
        complexQuiz.put("question_id", 101);
        complexQuiz.put("correct_answer", "option_B");

        ArrayNode options = objectMapper.createArrayNode();
        options.add("Opção A: Token Simétrico");
        options.add("Opção B: Token Assimétrico RS256");
        complexQuiz.set("options", options);

        ObjectNode dummyXray = objectMapper.createObjectNode();
        dummyXray.put("status", "analisado");

        Scenario complexScenario = new Scenario();
        complexScenario.setTitleScenarios("Desafio Avançado de Assinatura JWT");
        complexScenario.setXrayData(dummyXray);
        complexScenario.setQuiz(complexQuiz);
        complexScenario.setPillar(CidPillar.CONFIDENCIALIDADE);
        complexScenario.setTrail(mainTrail);

        Scenario savedScenario = scenarioRepository.save(complexScenario);

        // Act
        Scenario retrievedScenario = scenarioRepository.findById(savedScenario.getId()).orElse(null);

        // Assert
        assertNotNull(retrievedScenario);
        assertEquals("Desafio Avançado de Assinatura JWT", retrievedScenario.getTitleScenarios());

        // Validando se o motor de persistência recuperou as chaves internas do JSONB perfeitamente
        assertNotNull(retrievedScenario.getQuiz());
        assertEquals(101, retrievedScenario.getQuiz().get("question_id").asInt());
        assertEquals("option_B", retrievedScenario.getQuiz().get("correct_answer").asString());

        // Validando o array aninhado dentro da coluna JSONB
        assertTrue(retrievedScenario.getQuiz().has("options"));
        assertEquals(2, retrievedScenario.getQuiz().get("options").size());
        assertEquals("Opção B: Token Assimétrico RS256", retrievedScenario.getQuiz().get("options").get(1).asString());
    }
}
