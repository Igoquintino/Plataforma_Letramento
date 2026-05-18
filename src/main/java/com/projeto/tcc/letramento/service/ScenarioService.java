package com.projeto.tcc.letramento.service;

import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.repository.ScenarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;

    // Busca o cenário completo (incluindo o JSONB do Raio-X)
    public Scenario getScenarioWithXray(Long scenarioId) {
        return scenarioRepository.findById(scenarioId)
                .orElseThrow(() -> new EntityNotFoundException("Scenario not found with id: " + scenarioId));
    }

    // Compara a resposta do usuário (input) com a resposta correta no JSONB do banco
    public Boolean compareUserResponse(Long scenarioId, JsonNode input) {
        Scenario scenario = getScenarioWithXray(scenarioId);
        JsonNode quizData = scenario.getQuiz();

        // Exemplo: Verifica se o campo "answer" do input bate com a "correct_answer" do BD
        if (quizData.has("correct_answer") && input.has("answer")) {
            return quizData.get("correct_answer").toString().equalsIgnoreCase(input.get("answer").toString());
        }
        return false;
    }

    // Retorna apenas o JSON do Quiz
    public JsonNode getQuizData(Long scenarioId) {
        Scenario scenario = getScenarioWithXray(scenarioId);
        return scenario.getQuiz();
    }
}
