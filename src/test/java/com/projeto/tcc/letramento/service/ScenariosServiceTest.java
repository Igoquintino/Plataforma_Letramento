package com.projeto.tcc.letramento.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.repository.ScenarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScenarioServiceTest {

    @Mock
    private ScenarioRepository scenarioRepository;

    @InjectMocks
    private ScenarioService scenarioService;

    // Utilitário para transformar Strings em objetos JsonNode reais para o teste
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Deve retornar TRUE quando o aluno acertar a vulnerabilidade no Raio-X")
    void shouldReturnTrueWhenUserAnswerIsCorrect() throws Exception {
        // ARRANGE (Preparação)
        Long scenarioId = 1L;
        Scenario mockScenario = new Scenario();
        mockScenario.setId(scenarioId);

        // Simulando o JSONB que viria do banco de dados (o Gabarito)
        String dbQuizJson = "{\"correct_answer\": \"link_falso_phishing\"}";
        JsonNode dbQuizNode = objectMapper.readTree(dbQuizJson);
        mockScenario.setQuiz(dbQuizNode);

        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(mockScenario));

        // Simulando o JSON que o React (Frontend) enviaria no DTO
        String userInputJson = "{\"answer\": \"link_falso_phishing\"}";
        JsonNode userInputNode = objectMapper.readTree(userInputJson);

        // ACT (Ação)
        Boolean isCorrect = scenarioService.compareUserResponse(scenarioId, userInputNode);

        // ASSERT (Verificação)
        assertThat(isCorrect).isTrue();
        verify(scenarioRepository, times(1)).findById(scenarioId);
    }

    @Test
    @DisplayName("Deve retornar FALSE quando o aluno errar a identificação do golpe")
    void shouldReturnFalseWhenUserAnswerIsIncorrect() throws Exception {
        // ARRANGE
        Long scenarioId = 2L;
        Scenario mockScenario = new Scenario();

        String dbQuizJson = "{\"correct_answer\": \"remetente_suspeito\"}";
        mockScenario.setQuiz(objectMapper.readTree(dbQuizJson));

        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(mockScenario));

        String userInputJson = "{\"answer\": \"anexo_seguro\"}"; // Resposta errada
        JsonNode userInputNode = objectMapper.readTree(userInputJson);

        // ACT
        Boolean isCorrect = scenarioService.compareUserResponse(scenarioId, userInputNode);

        // ASSERT
        assertThat(isCorrect).isFalse();
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException se o cenário não existir no banco")
    void shouldThrowExceptionWhenScenarioDoesNotExist() {
        // ARRANGE
        Long invalidScenarioId = 99L;
        when(scenarioRepository.findById(invalidScenarioId)).thenReturn(Optional.empty());

        // ACT & ASSERT
        // Verifica se o sistema protege contra IDs inválidos estourando a exceção correta
        assertThatThrownBy(() -> scenarioService.getScenarioWithXray(invalidScenarioId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Scenario not found");
    }
}