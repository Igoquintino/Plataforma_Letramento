package com.projeto.tcc.letramento.model;

import com.projeto.tcc.letramento.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Progress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ProgressStatus status = ProgressStatus.IN_PROGRESS;

    @Column(name = "quiz_score")
    private BigDecimal quizScore = BigDecimal.ZERO;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "time_spent")
    private Long TimeSpent = 0L; // Tempo gasto em segundos

    @Column(name = "user_feedback")
    private String userFeedback;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "scenario_id", nullable = false)
    private Scenario scenario;
}
