package com.projeto.tcc.letramento.service;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrailServiceTest {
    @Mock
    private TrailRepository trailRepository;

    @Mock
    private ScenarioRepository scenarioRepository;

    @Mock
    private ProgressRepository progressRepository;

    @InjectMocks
    private TrailService trailService; // Injeta automaticamente os mocks acima aqui dentro

    @Test
    @DisplayName("Deve retornar zero de progresso quando a trilha não tiver cenários")
    void shouldReturnZeroProgressWhenTrailHasNoScenarios() {
        // ARRANGE (Preparação)
        Long userId = 1L;
        Long trailId = 10L;

        // Simulando que o repositório de cenários retorna uma lista vazia para esta trilha
        when(scenarioRepository.findByTrailId(trailId)).thenReturn(Collections.emptyList());

        // ACT (Ação)
        Double progress = trailService.calculateTrailProgress(userId, trailId);

        // ASSERT (Verificação)
        assertThat(progress).isEqualTo(0.0);

        // Garante que o repositório de progresso sequer foi consultado (otimização de performance)
        verifyNoInteractions(progressRepository);
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
        verify(trailRepository, times(1)).findAll(); // Garante que chamou o banco exatamente 1 vez
    }
}
