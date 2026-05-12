package com.projeto.tcc.letramento.repository;

import com.projeto.tcc.letramento.model.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByUserId(Long userId);
    Optional<Progress> findByUserIdAndScenarioId(Long userId, Long scenarioId);
}

