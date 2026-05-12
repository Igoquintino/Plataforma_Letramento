package com.projeto.tcc.letramento.service;

import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.repository.ScenarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;

    public Scenario getScenarioWithXRay(Long id) {
        return scenarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scenario not found with id: " + id));
    }

    public boolean compareUserResponse(Long scenarioId, Object input){
        return true;
    }
}
