package com.projeto.tcc.letramento.service;

import com.projeto.tcc.letramento.enums.ProgressStatus;
import com.projeto.tcc.letramento.model.Progress;
import com.projeto.tcc.letramento.repository.ProgressRepository;
import com.projeto.tcc.letramento.repository.ScenarioRepository;
import com.projeto.tcc.letramento.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final ScenarioRepository scenarioRepository;

    public List<Progress> getStudentDashboard(Long userId) {
        return progressRepository.findByUserId(userId);
    }

    public Progress saveProgress(Progress progress) {
        return progressRepository.save(progress);
    }

    // Inicia o progresso quando o aluno abre o simulador Raio-X
    public Progress startUserProgress(Long userId, Long scenarioId) {
        return progressRepository.findByUserIdAndScenarioId(userId, scenarioId)
                .orElseGet(() -> {
                    Progress newProgress = new Progress();
                    newProgress.setUser(userRepository.getReferenceById(userId));
                    newProgress.setScenario(scenarioRepository.getReferenceById(scenarioId));
                    newProgress.setStatus(ProgressStatus.IN_PROGRESS);
                    return progressRepository.save(newProgress);
                });
    }

    // Finaliza o cenário salvando as métricas para a pesquisa acadêmica
    public Progress completeScenario(Long userId, Long scenarioId, BigDecimal score, String feedback, Long timeSpent) {
        Progress progress = progressRepository.findByUserIdAndScenarioId(userId, scenarioId)
                .orElseThrow(() -> new EntityNotFoundException("Progresso não iniciado"));

        progress.setQuizScore(score);
        progress.setUserFeedback(feedback);
        progress.setTimeSpent(timeSpent);
        progress.setStatus(ProgressStatus.COMPLETED);
        progress.setCompletedAt(LocalDateTime.now());

        progressRepository.save(progress);
        return progress;
    }
}
