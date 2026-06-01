package com.projeto.tcc.letramento.persistence;

import com.projeto.tcc.letramento.enums.CidPillar;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
import com.projeto.tcc.letramento.repository.ScenarioRepository;
import com.projeto.tcc.letramento.repository.TrailRepository;
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
import tools.jackson.databind.node.ObjectNode;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class JsonbEdgeCasesTest {

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

    @Autowired private ScenarioRepository scenarioRepository;
    @Autowired private TrailRepository trailRepository;

    private final ObjectMapper mapper = new ObjectMapper(); // Instância Jackson 3

    @Test
    @DisplayName("PERSISTÊNCIA JSONB — Deve gravar e ler um cenário perfeitamente mesmo se o JSON do Raio-X estiver totalmente vazio '{}'")
    void saveScenario_WithEmptyJsonbNodes_ShouldPersistSuccessfully() {
        // Arrange
        Trail trail = new Trail();
        trail.setTitle("Trilha JSONB");
        trail.setDescription("Testes de persistência");
        trail = trailRepository.saveAndFlush(trail);

        Scenario scenario = new Scenario();
        scenario.setTitleScenarios("Desafio sem Metadados");
        scenario.setPillar(CidPillar.INTEGRIDADE);
        scenario.setTrail(trail);

        scenario.setXrayData(mapper.createObjectNode());
        scenario.setQuiz(mapper.createObjectNode());

        // Act
        Scenario saved = scenarioRepository.saveAndFlush(scenario);

        // Assert
        Optional<Scenario> fetched = scenarioRepository.findById(saved.getId());
        assertTrue(fetched.isPresent());
        assertEquals("{}", fetched.get().getXrayData().toString());
        assertEquals("{}", fetched.get().getQuiz().toString());
    }

    @Test
    @DisplayName("PERSISTÊNCIA JSONB — Deve suportar a gravação de objetos complexos e profundamente aninhados")
    void saveScenario_WithDeeplyNestedJsonb_ShouldMaintainStructure() {
        Trail trail = new Trail();
        trail.setTitle("Trilha Deep JSONB");
        trail.setDescription("Mapeamento complexo");
        trail = trailRepository.saveAndFlush(trail);

        ObjectNode rootNode = mapper.createObjectNode();
        ObjectNode levelsNode = mapper.createObjectNode();
        ObjectNode metadataNode = mapper.createObjectNode();

        metadataNode.put("flagged_vulnerability", "Signature Bypass via JWT header injection");
        levelsNode.set("details", metadataNode);
        rootNode.set("security_xray", levelsNode);

        Scenario scenario = new Scenario();
        scenario.setTitleScenarios("Desafio Avançado Hacking");
        scenario.setPillar(CidPillar.CONFIDENCIALIDADE);
        scenario.setTrail(trail);
        scenario.setXrayData(rootNode);
        scenario.setQuiz(mapper.createObjectNode());

        Scenario saved = scenarioRepository.saveAndFlush(scenario);

        // Act & Assert
        Scenario fetched = scenarioRepository.findById(saved.getId()).orElseThrow();
        String jsonString = fetched.getXrayData().toString();

        assertTrue(jsonString.contains("security_xray"));
        assertTrue(jsonString.contains("details"));
        assertTrue(jsonString.contains("Signature Bypass"));
    }
}
