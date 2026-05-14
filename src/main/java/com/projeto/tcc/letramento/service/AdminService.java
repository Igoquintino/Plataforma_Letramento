package com.projeto.tcc.letramento.service;

import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
import com.projeto.tcc.letramento.repository.ScenarioRepository;
import com.projeto.tcc.letramento.repository.TrailRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TrailRepository trailRepository;
    private final ScenarioRepository scenarioRepository;

    @Transactional
    public Trail createTrail(Trail trail) {
        return trailRepository.save(trail);
    }

    @Transactional
    public Scenario createScenario(Scenario scenario) {
        return scenarioRepository.save(scenario);
    }

    @Transactional
    public void deleteTrail(Long trailId) {
        trailRepository.deleteById(trailId);
    }
}
