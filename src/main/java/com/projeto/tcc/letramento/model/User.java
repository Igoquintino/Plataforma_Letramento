package com.projeto.tcc.letramento.model;
import com.projeto.tcc.letramento.enums.UserRole;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "google_id", nullable = false, unique = true)
    private String googleId;

    private String course;

    @Column(name = "academic_level")
    private String academicLevel;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ALUNO;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
