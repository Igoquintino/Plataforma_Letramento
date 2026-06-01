package com.projeto.tcc.letramento.service;

import com.projeto.tcc.letramento.dto.ScenarioRequestDTO;
import com.projeto.tcc.letramento.dto.TrailRequestDTO;
import com.projeto.tcc.letramento.enums.CidPillar;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
import com.projeto.tcc.letramento.repository.ScenarioRepository;
import com.projeto.tcc.letramento.repository.TrailRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private TrailRepository trailRepository;
    @Mock private ScenarioRepository scenarioRepository;
    @InjectMocks private AdminService adminService;

    @Test
    @DisplayName("Deve criar uma Trilha com sucesso")
    void createTrail_Success() {
        // Arrange
        TrailRequestDTO requestDTO = new TrailRequestDTO("Trilha Redes Sociais", "Descrição da trilha");
        Trail savedTrail = new Trail(1L, "Trilha Redes Sociais", "Descrição da trilha");

        when(trailRepository.save(any(Trail.class))).thenReturn(savedTrail);

        // Act
        Trail result = adminService.createTrail(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Trilha Redes Sociais", result.getTitle());
        verify(trailRepository, times(1)).save(any(Trail.class));
    }

    @Test
    @DisplayName("Deve criar um Cenário com sucesso quando a Trilha existir")
    void createScenario_Success() {
        // Arrange
        Long trailId = 1L;
        Trail trail = new Trail(trailId, "Trilha de IA", "Desc");

        // Mockando os nós do Jackson 3
        JsonNode mockXrayData = mock(JsonNode.class);
        JsonNode mockQuiz = mock(JsonNode.class);

        ScenarioRequestDTO requestDTO = new ScenarioRequestDTO(
                "Cenário Phishing",
                mockXrayData,
                mockQuiz,
                CidPillar.CONFIDENCIALIDADE,
                trailId
        );

        when(trailRepository.findById(trailId)).thenReturn(Optional.of(trail));

        Scenario savedScenario = new Scenario();
        savedScenario.setId(10L);
        savedScenario.setTitleScenarios("Cenário Phishing");
        savedScenario.setXrayData(mockXrayData);
        savedScenario.setQuiz(mockQuiz);
        savedScenario.setPillar(CidPillar.CONFIDENCIALIDADE);
        savedScenario.setTrail(trail);

        when(scenarioRepository.save(any(Scenario.class))).thenReturn(savedScenario);

        // Act
        Scenario result = adminService.createScenario(requestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Cenário Phishing", result.getTitleScenarios());
        assertEquals(mockXrayData, result.getXrayData());
        verify(trailRepository, times(1)).findById(trailId);
        verify(scenarioRepository, times(1)).save(any(Scenario.class));
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar criar Cenário com Trilha inexistente")
    void createScenario_TrailNotFound() {
        // Arrange
        JsonNode mockXrayData = mock(JsonNode.class);
        JsonNode mockQuiz = mock(JsonNode.class);
        ScenarioRequestDTO requestDTO = new ScenarioRequestDTO(
                "Cenário Falho", mockXrayData, mockQuiz, CidPillar.INTEGRIDADE, 99L
        );

        when(trailRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> adminService.createScenario(requestDTO));

        assertEquals("Trilha não encontrada", exception.getMessage());
        verify(scenarioRepository, never()).save(any(Scenario.class));
    }

    @Test
    @DisplayName("Deve deletar uma Trilha com sucesso")
    void deleteTrail_Success() {
        // Arrange
        Long trailId = 1L;
        when(trailRepository.existsById(trailId)).thenReturn(true);

        // Act
        adminService.deleteTrail(trailId);

        // Assert
        verify(trailRepository, times(1)).deleteById(trailId);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao tentar deletar Trilha inexistente")
    void deleteTrail_NotFound() {
        // Arrange
        Long trailId = 99L;
        when(trailRepository.existsById(trailId)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> adminService.deleteTrail(trailId));

        assertEquals("Trilha não encontrada", exception.getMessage());
        verify(trailRepository, never()).deleteById(anyLong());
    }
}
