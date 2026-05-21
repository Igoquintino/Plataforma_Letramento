package com.projeto.tcc.letramento.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import static org.junit.jupiter.api.Assertions.*;

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
    private Scenario mockScenario;

    // Utilitário para transformar Strings em objetos JsonNode reais para o teste
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        objectMapper = new ObjectMapper();

        // Simula o JSONB que viria do banco de dados
        String dbQuizJson = "{\"correct_answer\": \"phishing\"}";
        JsonNode dbQuizNode = objectMapper.readTree(dbQuizJson);

        mockScenario = new Scenario();
        mockScenario.setId(1L);
        mockScenario.setQuiz(dbQuizNode);
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

    @Test
    @DisplayName("Deve retornar TRUE quando a resposta do aluno for exatamente igual")
    void testCompareUserResponse_CorrectAnswer() throws Exception {
        // Arrange
        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(mockScenario));
        String inputJson = "{\"answer\": \"phishing\"}";
        JsonNode inputNode = objectMapper.readTree(inputJson);

        // Act
        Boolean isCorrect = scenarioService.compareUserResponse(1L, inputNode);

        // Assert
        assertTrue(isCorrect, "A resposta deveria ser considerada correta.");
        verify(scenarioRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar TRUE mesmo se o aluno usar letras maiúsculas/minúsculas diferentes")
    void testCompareUserResponse_CorrectAnswerCaseInsensitive() throws Exception {
        // Arrange
        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(mockScenario));
        String inputJson = "{\"answer\": \"PhIsHiNg\"}";
        JsonNode inputNode = objectMapper.readTree(inputJson);

        // Act
        Boolean isCorrect = scenarioService.compareUserResponse(1L, inputNode);

        // Assert
        assertTrue(isCorrect, "A validação ignorar case (maiúsculas/minúsculas).");
    }

    @Test
    @DisplayName("Deve retornar FALSE quando a resposta do aluno for incorreta")
    void testCompareUserResponse_IncorrectAnswer() throws Exception {
        // Arrange
        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(mockScenario));
        String inputJson = "{\"answer\": \"spam\"}";
        JsonNode inputNode = objectMapper.readTree(inputJson);

        // Act
        Boolean isCorrect = scenarioService.compareUserResponse(1L, inputNode);

        // Assert
        assertFalse(isCorrect, "A resposta deveria ser considerada incorreta.");
    }
}