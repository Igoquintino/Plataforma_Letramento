package com.projeto.tcc.letramento.metrics;

import com.projeto.tcc.letramento.enums.ProgressStatus;
import com.projeto.tcc.letramento.model.Progress;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.repository.ProgressRepository;
import com.projeto.tcc.letramento.repository.ScenarioRepository;
import com.projeto.tcc.letramento.service.TrailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrailProgressMetricsTest {

    @Mock
    private ScenarioRepository scenarioRepository;

    @Mock
    private ProgressRepository progressRepository;

    @InjectMocks
    private TrailService trailService;

    private static final double EPSILON = 0.001;

    @Test
    @DisplayName("MÉTRICA — Deve evitar a divisão por zero (NaN) e retornar 0.0 quando a trilha não tiver cenários")
    void calculateProgress_ZeroScenarios_ShouldReturnZero() {
        Long userId = 1L;
        Long trailId = 100L;

        when(scenarioRepository.findByTrailId(trailId)).thenReturn(Collections.emptyList());

        Double result = trailService.calculateTrailProgress(userId, trailId);

        assertEquals(0.0, result, EPSILON);
        verify(progressRepository, never()).findByUserIdAndScenarioId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("MÉTRICA — Deve computar dízimas periódicas com precisão (ex: 1 de 3 cenários concluídos = 33.333%)")
    void calculateProgress_RepeatingDecimal_ShouldBeAccurate() {
        Long userId = 1L;
        Long trailId = 100L;

        Scenario s1 = new Scenario(); s1.setId(101L);
        Scenario s2 = new Scenario(); s2.setId(102L);
        Scenario s3 = new Scenario(); s3.setId(103L);
        when(scenarioRepository.findByTrailId(trailId)).thenReturn(List.of(s1, s2, s3));

        // 1 cenário concluído
        Progress p1 = new Progress(); p1.setStatus(ProgressStatus.COMPLETED);

        when(progressRepository.findByUserIdAndScenarioId(userId, 101L)).thenReturn(Optional.of(p1));

        when(progressRepository.findByUserIdAndScenarioId(userId, 102L)).thenReturn(Optional.empty());

        when(progressRepository.findByUserIdAndScenarioId(userId, 103L)).thenReturn(Optional.empty());

        Double result = trailService.calculateTrailProgress(userId, trailId);
        assertEquals(33.333, result, 0.005);
    }

    @Test
    @DisplayName("MÉTRICA — Não deve contabilizar status IN_PROGRESS ou FAILED no cálculo de conclusão")
    void calculateProgress_ShouldOnlyCountCompletedStatus() {
        Long userId = 1L;
        Long trailId = 100L;

        Scenario s1 = new Scenario(); s1.setId(201L);
        Scenario s2 = new Scenario(); s2.setId(202L);
        when(scenarioRepository.findByTrailId(trailId)).thenReturn(List.of(s1, s2));

        Progress p1 = new Progress(); p1.setStatus(ProgressStatus.IN_PROGRESS);
        when(progressRepository.findByUserIdAndScenarioId(userId, 201L)).thenReturn(Optional.of(p1));

        Progress p2 = new Progress(); p2.setStatus(ProgressStatus.FAILED);
        when(progressRepository.findByUserIdAndScenarioId(userId, 202L)).thenReturn(Optional.of(p2));

        Double result = trailService.calculateTrailProgress(userId, trailId);

        // 0 de 2 concluídos = 0.0%
        assertEquals(0.0, result, EPSILON);
    }

    @Test
    @DisplayName("MÉTRICA — Deve computar 100.0% perfeitamente quando todos os cenários forem concluídos")
    void calculateProgress_FullCompletion_ShouldReturnOneHundred() {
        Long userId = 1L;
        Long trailId = 100L;

        Scenario s1 = new Scenario(); s1.setId(301L);
        when(scenarioRepository.findByTrailId(trailId)).thenReturn(List.of(s1));

        Progress p1 = new Progress(); p1.setStatus(ProgressStatus.COMPLETED);
        when(progressRepository.findByUserIdAndScenarioId(userId, 301L)).thenReturn(Optional.of(p1));

        Double result = trailService.calculateTrailProgress(userId, trailId);

        assertEquals(100.0, result, EPSILON);
    }
}
