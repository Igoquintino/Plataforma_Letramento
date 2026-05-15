package com.projeto.tcc.letramento.controller;

import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
import com.projeto.tcc.letramento.service.TrailService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/trails")
@AllArgsConstructor
public class TrailController {

    private final TrailService trailService;

    // Lista todas as trilhas (ex: Redes Sociais, E-mail, I.A.)
    @GetMapping
    public ResponseEntity<List<Trail>> getAllTrails() {
        return ResponseEntity.ok(trailService.findAllActiveTrails());
    }

    // Lista os cenários de uma trilha específica
    @GetMapping("/{id}/scenarios")
    public ResponseEntity<List<Scenario>> getScenarios(@PathVariable Long id) {
        return ResponseEntity.ok(trailService.getScenariosByTrail(id));
    }

    // Retorna a porcentagem de conclusão da trilha para o aluno
    @GetMapping("/{id}/progress/{userId}")
    public ResponseEntity<Double> getTrailProgress(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.ok(trailService.calculateTrailProgress(userId, id));
    }
}
