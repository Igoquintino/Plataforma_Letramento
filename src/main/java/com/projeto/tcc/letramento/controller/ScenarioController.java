package com.projeto.tcc.letramento.controller;

import com.projeto.tcc.letramento.dto.AnswerDTO;
import com.projeto.tcc.letramento.dto.ScenarioDTO;
import com.projeto.tcc.letramento.model.User;
import com.projeto.tcc.letramento.service.ProgressService;
import com.projeto.tcc.letramento.service.ScenarioService;
import com.projeto.tcc.letramento.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper; // ✅ Jackson 3

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
public class ScenarioController {

    private final ScenarioService scenarioService;
    private final ProgressService progressService;
    private final UserService userService;

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
    public ResponseEntity<String> postAnswer(
            @RequestBody AnswerDTO answerData,
            @AuthenticationPrincipal OAuth2User principal) {

        String googleId = principal.getAttribute("sub");
        User user = userService.findByGoogleId(googleId);

        JsonNode answersNode = objectMapper.valueToTree(answerData.answers());
        boolean isCorrect = scenarioService.compareUserResponse(
                answerData.scenarioId(),
                answersNode
        );

        java.math.BigDecimal score = isCorrect ?
                new java.math.BigDecimal("100.00") :
                java.math.BigDecimal.ZERO;

        // 3. Salva no banco de dados
        progressService.completeScenario(
                user.getId(),
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