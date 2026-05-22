package com.projeto.tcc.letramento.controller;

import com.projeto.tcc.letramento.dto.AnswerDTO;
import com.projeto.tcc.letramento.dto.ScenarioDTO;
import com.projeto.tcc.letramento.service.ProgressService;
import com.projeto.tcc.letramento.service.ScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper; // ✅ Jackson 3

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
public class ScenarioController {

    private final ScenarioService scenarioService;
    private final ProgressService progressService;

    // ✅ ObjectMapper instanciado uma vez na classe, não dentro de cada método
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        // ✅ Agora usa tools.jackson (Jackson 3) — mesmo comportamento do valueToTree
        JsonNode answersNode = objectMapper.valueToTree(answerData.answers());

        // 1. Valida se a resposta está correta comparando com o JSONB do banco
        boolean isCorrect = scenarioService.compareUserResponse(
                answerData.scenarioId(),
                answersNode
        );

        // 2. Score simples: 100 se correto, 0 se errado
        java.math.BigDecimal score = isCorrect ?
                new java.math.BigDecimal("100.00") :
                java.math.BigDecimal.ZERO;

        // 3. Salva no banco de dados
        // TODO: substituir userId fixo pelo valor vindo do token OAuth
        progressService.completeScenario(
                1L,
                answerData.scenarioId(),
                score,
                "Resposta enviada via simulador Raio-X",
                60L
        );

        return ResponseEntity.ok(isCorrect ? "Correto!" : "Incorreto. Tente analisar o Raio-X novamente.");
    }

    @GetMapping("/{id}/quiz")
    public ResponseEntity<JsonNode> getQuizData(@PathVariable Long id) {
        JsonNode quiz = scenarioService.getQuizData(id);
        return ResponseEntity.ok(quiz);
    }
}