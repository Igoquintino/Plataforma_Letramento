package com.projeto.tcc.letramento.service;

import com.projeto.tcc.letramento.dto.ScenarioRequestDTO;
import com.projeto.tcc.letramento.dto.TrailRequestDTO;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
import com.projeto.tcc.letramento.repository.ScenarioRepository;
import com.projeto.tcc.letramento.repository.TrailRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TrailRepository trailRepository;
    private final ScenarioRepository scenarioRepository;

    @Transactional
    public Trail createTrail(@NonNull TrailRequestDTO data) {
        Trail trail = new Trail();
        trail.setTitle(data.title());
        trail.setDescription(data.description());
        return trailRepository.save(trail);
    }

    @Transactional
    public Scenario createScenario(@NonNull ScenarioRequestDTO data) {
        Trail trail = trailRepository.findById(data.trailId())
                .orElseThrow(() -> new EntityNotFoundException("Trilha não encontrada"));
        Scenario scenario = new Scenario();
        scenario.setTitleScenarios(data.titleScenario());
        scenario.setXrayData(data.xrayData());
        scenario.setQuiz(data.quiz());
        scenario.setPillar(data.pillar());
        scenario.setTrail(trail);

        return scenarioRepository.save(scenario);
    }

    @Transactional
    public void deleteTrail(Long trailId) {
        if (!trailRepository.existsById(trailId)) {
            throw new EntityNotFoundException("Trilha não encontrada");
        }
        trailRepository.deleteById(trailId);
    }
}
