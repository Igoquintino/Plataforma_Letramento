package com.projeto.tcc.letramento.service;

import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
import com.projeto.tcc.letramento.repository.ProgressRepository;
import com.projeto.tcc.letramento.repository.ScenarioRepository;
import com.projeto.tcc.letramento.repository.TrailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrailService {

    private final TrailRepository trailRepository;
    private final ScenarioRepository scenarioRepository;
    private final ProgressRepository progressRepository;

    // Retornas todas as trilhas cadastradas
    public List<Trail> findAllActiveTrails() {
        return trailRepository.findAll();
    }

    public List<Scenario> getScenariosByTrail(Long trailId) {
        return scenarioRepository.findByTrailId(trailId);
    }

    public Double calculateTrailProgress(Long userId, Long trailId) {
        List<Scenario> trailScenarios = scenarioRepository.findByTrailId(trailId);
        if (trailScenarios.isEmpty()) {
            return 0.0;
        }

        long completedCount = trailScenarios.stream()
                .filter(scenario -> progressRepository
                        .findByUserIdAndScenarioId(userId, scenario.getId())
                        .map(progress -> progress.getStatus().name().equals("COMPLETED"))
                        .orElse(false))
                .count();
        return (double) completedCount / trailScenarios.size() * 100;
    }
}
