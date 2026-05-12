package com.projeto.tcc.letramento.controller;

import com.projeto.tcc.letramento.dto.ScenarioDTO;
import com.projeto.tcc.letramento.service.ScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
public class ScenarioController {
    private final ScenarioService scenarioService;

    @GetMapping("/{id}/xray")
    public ResponseEntity<ScenarioDTO> getXRay(@PathVariable Long id) {
        var scenario = scenarioService.getScenarioWithXRay(id);
        return ResponseEntity.ok(new ScenarioDTO(
                scenario.getId(),
                scenario.getTitleScenarios(),
                scenario.getPillar(),
                scenario.getXrayData()
        ));
    }
}
