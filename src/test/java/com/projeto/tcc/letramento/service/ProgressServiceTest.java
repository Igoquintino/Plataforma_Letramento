package com.projeto.tcc.letramento.service;

import com.projeto.tcc.letramento.enums.ProgressStatus;
import com.projeto.tcc.letramento.model.Progress;
import com.projeto.tcc.letramento.repository.ProgressRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    @Mock private ProgressRepository progressRepository;
    @InjectMocks private ProgressService progressService;

    @Test
    @DisplayName("Deve finalizar o cenário e salvar métricas (score, tempo e status)")
    void testCompleteScenario_Success() {
        // Arrange
        Long userId = 1L;
        Long scenarioId = 1L;
        BigDecimal score = new BigDecimal("100.00");
        String feedback = "Excelente";
        Long timeSpent = 120L;

        Progress existingProgress = new Progress();
        existingProgress.setStatus(ProgressStatus.IN_PROGRESS);

        when(progressRepository.findByUserIdAndScenarioId(userId, scenarioId))
                .thenReturn(Optional.of(existingProgress));
        when(progressRepository.save(any(Progress.class))).thenReturn(existingProgress);

        // Act
        Progress result = progressService.completeScenario(userId, scenarioId, score, feedback, timeSpent);

        // Assert
        assertEquals(ProgressStatus.COMPLETED, result.getStatus());
        assertEquals(score, result.getQuizScore());
        assertEquals(timeSpent, result.getTimeSpent());
        assertNotNull(result.getCompletedAt(), "O campo completedAt deve ser preenchido.");

        // Verifica se o repository.save foi chamado
        verify(progressRepository, times(1)).save(existingProgress);
    }

    @Test
    @DisplayName("Deve lançar exceção se tentar finalizar um progresso não iniciado")
    void testCompleteScenario_NotFound() {
        // Arrange
        when(progressRepository.findByUserIdAndScenarioId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> progressService.completeScenario(1L, 1L, BigDecimal.ZERO, "Teste", 60L)
        );

        assertEquals("Progresso não iniciado", exception.getMessage());
    }
}
