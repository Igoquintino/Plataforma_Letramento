package com.projeto.tcc.letramento.service;

import com.projeto.tcc.letramento.enums.ProgressStatus;
import com.projeto.tcc.letramento.model.Progress;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
import com.projeto.tcc.letramento.repository.ProgressRepository;
import com.projeto.tcc.letramento.repository.ScenarioRepository;
import com.projeto.tcc.letramento.repository.TrailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrailServiceTest {

    @Mock private TrailRepository trailRepository;
    @Mock private ScenarioRepository scenarioRepository;
    @Mock private ProgressRepository progressRepository;
    @InjectMocks private TrailService trailService;

    @Test
    @DisplayName("Deve retornar zero de progresso quando a trilha não tiver cenários")
    void shouldReturnZeroProgressWhenTrailHasNoScenarios() {
        // ARRANGE (Preparação)
        Long userId = 1L;
        Long trailId = 10L;

        when(scenarioRepository.findByTrailId(trailId)).thenReturn(Collections.emptyList());

        // ACT (Ação)
        Double progress = trailService.calculateTrailProgress(userId, trailId);

        // ASSERT (Verificação)
        assertThat(progress).isEqualTo(0.0);

        verifyNoInteractions(progressRepository);
    }

    @Test
    @DisplayName("Deve calcular corretamente a porcentagem de progresso parcial da trilha")
    void calculateTrailProgress_PartialCompletion() {
        // Arrange
        Long userId = 1L;
        Long trailId = 10L;

        Scenario s1 = new Scenario(); s1.setId(101L);
        Scenario s2 = new Scenario(); s2.setId(102L);
        List<Scenario> scenarios = List.of(s1, s2); // 2 cenários na trilha

        when(scenarioRepository.findByTrailId(trailId)).thenReturn(scenarios);

        Progress p1 = new Progress(); p1.setStatus(ProgressStatus.COMPLETED);
        when(progressRepository.findByUserIdAndScenarioId(userId, 101L)).thenReturn(Optional.of(p1));

        Progress p2 = new Progress(); p2.setStatus(ProgressStatus.IN_PROGRESS);
        when(progressRepository.findByUserIdAndScenarioId(userId, 102L)).thenReturn(Optional.of(p2));

        // Act
        Double progress = trailService.calculateTrailProgress(userId, trailId);

        // Assert
        assertEquals(50.0, progress);
    }

    @Test
    @DisplayName("Deve retornar todas as trilhas ativas cadastradas")
    void shouldReturnAllActiveTrails() {
        // ARRANGE
        Trail trail1 = new Trail();
        trail1.setId(1L);
        trail1.setTitle("Redes Sociais");

        Trail trail2 = new Trail();
        trail2.setId(2L);
        trail2.setTitle("Engenharia Social");

        when(trailRepository.findAll()).thenReturn(List.of(trail1, trail2));

        // ACT
        List<Trail> result = trailService.findAllActiveTrails();

        // ASSERT
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Redes Sociais");
        verify(trailRepository, times(1)).findAll();
    }
}
