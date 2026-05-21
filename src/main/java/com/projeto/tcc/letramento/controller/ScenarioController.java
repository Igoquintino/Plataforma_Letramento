package com.projeto.tcc.letramento.controller;

import com.projeto.tcc.letramento.dto.AnswerDTO;
import com.projeto.tcc.letramento.dto.ScenarioDTO;
import com.projeto.tcc.letramento.service.ProgressService;
import com.projeto.tcc.letramento.service.ScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
public class ScenarioController {

    private final ScenarioService scenarioService;
    private final ProgressService progressService;

    @GetMapping("/{id}/xray")
    public ResponseEntity<ScenarioDTO> getXRay(@PathVariable Long id) {
        var scenario = scenarioService.getScenarioWithXray(id);
        return ResponseEntity.ok(new ScenarioDTO(
                scenario.getId(),
                scenario.getTitleScenarios(),
                scenario.getPillar(),
                scenario.getXrayData()
        ));
    }

    @PostMapping("/answer")
    public ResponseEntity<String> postAnswer(@RequestBody AnswerDTO answerData) {

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        com.fasterxml.jackson.databind.JsonNode answersNode = mapper.valueToTree(answerData.answers());
        // 1. Valida se a resposta está correta comparando com o JSONB do banco
        boolean isCorrect = scenarioService.compareUserResponse(
                answerData.scenarioId(),
                answersNode
        );

        // 2. Aqui você definiria a nota (score). Exemplo simples: 100 se correto, 0 se errado.
        java.math.BigDecimal score = isCorrect ?
                new java.math.BigDecimal("100.00") :
                java.math.BigDecimal.ZERO;

        // 3. Salva no banco de dados (Simulando userId vindo do token futuramente)
        // Por enquanto, o userId pode vir no DTO ou ser fixo para testes
        progressService.completeScenario(
                1L, // Exemplo de userId fixo até configurar o OAuth
                answerData.scenarioId(),
                score,
                "Resposta enviada via simulador Raio-X",
                60L // Exemplo: 60 segundos (o front deve enviar o tempo real)
        );

        return ResponseEntity.ok(isCorrect ? "Correto!" : "Incorreto. Tente analisar o Raio-X novamente.");
    }

    @GetMapping("/{id}/quiz")
    public ResponseEntity<JsonNode> getQuizData(@PathVariable Long id) {
        // Chamando a função que já criamos no ScenarioService
        JsonNode quiz = scenarioService.getQuizData(id);
        return ResponseEntity.ok(quiz);
    }

}
