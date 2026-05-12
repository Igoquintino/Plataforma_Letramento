package com.projeto.tcc.letramento.repository;

import com.projeto.tcc.letramento.model.Scenario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScenarioRepository extends JpaRepository <Scenario, Long> {
    List<Scenario> findByTrailId(Long trailId);
}
