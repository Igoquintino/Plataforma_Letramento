package com.projeto.tcc.letramento.repository;

import com.projeto.tcc.letramento.model.Trail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class TrailRepositoryTest {

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

        // Necessário porque o Hibernate mapeia o contexto global de entidades na inicialização do slice
        registry.add("spring.jpa.properties.hibernate.type.json_format_mapper",
                () -> "com.projeto.tcc.letramento.config.Jackson3FormatMapper");
    }

    @Autowired
    private TrailRepository trailRepository;

    @Test
    @DisplayName("Deve persistir e recuperar uma Trilha com sucesso quando os dados forem válidos")
    void saveAndFindById_Success() {
        // Arrange
        Trail trail = new Trail();
        trail.setTitle("Trilha de Engenharia Reversa");
        trail.setDescription("Exploração de binários e análise de vulnerabilidades.");

        // Act
        Trail savedTrail = trailRepository.save(trail);
        Optional<Trail> retrievedTrail = trailRepository.findById(savedTrail.getId());

        // Assert
        assertTrue(retrievedTrail.isPresent());
        assertEquals("Trilha de Engenharia Reversa", retrievedTrail.get().getTitle());
        assertEquals("Exploração de binários e análise de vulnerabilidades.", retrievedTrail.get().getDescription());
    }

    @Test
    @DisplayName("Deve lançar DataIntegrityViolationException ao tentar salvar uma Trilha com título nulo")
    void save_ShouldThrowException_WhenTitleIsNull() {
        // Arrange
        Trail trail = new Trail();
        trail.setTitle(null); // Violação da constraint de banco (nullable = false)
        trail.setDescription("Descrição válida da trilha.");

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            trailRepository.saveAndFlush(trail);
            // Nota de Q.A: Usamos saveAndFlush para forçar o JPA a disparar o SQL imediatamente
            // para o Postgres dentro do método, capturando o erro de constraint no momento exato.
        });
    }

    @Test
    @DisplayName("Deve lançar DataIntegrityViolationException ao tentar salvar uma Trilha com descrição nula")
    void save_ShouldThrowException_WhenDescriptionIsNull() {
        // Arrange
        Trail trail = new Trail();
        trail.setTitle("Trilha de Segurança de APIs");
        trail.setDescription(null); // Violação da constraint de banco (nullable = false)

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            trailRepository.saveAndFlush(trail);
        });
    }

    @Test
    @DisplayName("Deve atualizar os dados de uma Trilha existente com sucesso")
    void updateTrail_Success() {
        // Arrange
        Trail trail = new Trail();
        trail.setTitle("Trilha Original");
        trail.setDescription("Descrição Original");
        Trail savedTrail = trailRepository.saveAndFlush(trail);

        // Act
        savedTrail.setTitle("Trilha Atualizada");
        savedTrail.setDescription("Descrição Atualizada");
        Trail updatedTrail = trailRepository.saveAndFlush(savedTrail);

        // Assert
        Optional<Trail> found = trailRepository.findById(updatedTrail.getId());
        assertTrue(found.isPresent());
        assertEquals("Trilha Atualizada", found.get().getTitle());
        assertEquals("Descrição Atualizada", found.get().getDescription());
        assertEquals(savedTrail.getId(), found.get().getId()); // Garante que o ID permaneceu o mesmo
    }

    @Test
    void placeholder_trailRepository() {
        // TODO: basic CRUD tests for TrailRepository
        assertTrue(true);
    }
}
