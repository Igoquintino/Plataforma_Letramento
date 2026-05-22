package com.projeto.tcc.letramento.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;          // ✅ Jackson 3
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScenarioServiceTest {

    @Mock
    private ScenarioRepository scenarioRepository;

    @InjectMocks
    private ScenarioService scenarioService;

    private Scenario mockScenario;

    // ✅ ObjectMapper do Jackson 3 — instanciado uma vez na classe
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // ✅ readTree() no Jackson 3 não lança checked exception — sem throws necessário
        JsonNode dbQuizNode = objectMapper.readTree("{\"correct_answer\": \"phishing\"}");

        mockScenario = new Scenario();
        mockScenario.setId(1L);
        mockScenario.setQuiz(dbQuizNode);
    }

    @Test
    @DisplayName("Deve retornar TRUE quando o aluno acertar a vulnerabilidade no Raio-X")
    void shouldReturnTrueWhenUserAnswerIsCorrect() {
        // ARRANGE
        Long scenarioId = 1L;
        Scenario scenario = new Scenario();
        scenario.setId(scenarioId);
        scenario.setQuiz(objectMapper.readTree("{\"correct_answer\": \"link_falso_phishing\"}"));

        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));

        JsonNode userInputNode = objectMapper.readTree("{\"answer\": \"link_falso_phishing\"}");

        // ACT
        Boolean isCorrect = scenarioService.compareUserResponse(scenarioId, userInputNode);

        // ASSERT
        assertThat(isCorrect).isTrue();
        verify(scenarioRepository, times(1)).findById(scenarioId);
    }

    @Test
    @DisplayName("Deve retornar FALSE quando o aluno errar a identificação do golpe")
    void shouldReturnFalseWhenUserAnswerIsIncorrect() {
        // ARRANGE
        Long scenarioId = 2L;
        Scenario scenario = new Scenario();
        scenario.setQuiz(objectMapper.readTree("{\"correct_answer\": \"remetente_suspeito\"}"));

        when(scenarioRepository.findById(scenarioId)).thenReturn(Optional.of(scenario));

        JsonNode userInputNode = objectMapper.readTree("{\"answer\": \"anexo_seguro\"}");

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
        assertThatThrownBy(() -> scenarioService.getScenarioWithXray(invalidScenarioId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Scenario not found");
    }

    @Test
    @DisplayName("Deve retornar TRUE quando a resposta do aluno for exatamente igual")
    void testCompareUserResponse_CorrectAnswer() {
        // ARRANGE
        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(mockScenario));
        JsonNode inputNode = objectMapper.readTree("{\"answer\": \"phishing\"}");

        // ACT
        Boolean isCorrect = scenarioService.compareUserResponse(1L, inputNode);

        // ASSERT
        assertTrue(isCorrect, "A resposta deveria ser considerada correta.");
        verify(scenarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar TRUE mesmo se o aluno usar letras maiúsculas/minúsculas diferentes")
    void testCompareUserResponse_CorrectAnswerCaseInsensitive() {
        // ARRANGE
        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(mockScenario));
        JsonNode inputNode = objectMapper.readTree("{\"answer\": \"PhIsHiNg\"}");

        // ACT
        Boolean isCorrect = scenarioService.compareUserResponse(1L, inputNode);

        // ASSERT
        assertTrue(isCorrect, "A validação deve ignorar case (maiúsculas/minúsculas).");
    }

    @Test
    @DisplayName("Deve retornar FALSE quando a resposta do aluno for incorreta")
    void testCompareUserResponse_IncorrectAnswer() {
        // ARRANGE
        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(mockScenario));
        JsonNode inputNode = objectMapper.readTree("{\"answer\": \"spam\"}");

        // ACT
        Boolean isCorrect = scenarioService.compareUserResponse(1L, inputNode);

        // ASSERT
        assertFalse(isCorrect, "A resposta deveria ser considerada incorreta.");
    }
}