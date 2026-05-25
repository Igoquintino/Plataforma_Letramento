package com.projeto.tcc.letramento.model;

import com.projeto.tcc.letramento.enums.CidPillar;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import tools.jackson.databind.JsonNode;

@Entity
@Table(name = "scenarios")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title_scenario", nullable = false)
    private String titleScenarios;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "xray_data", nullable = false)
    private JsonNode xrayData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private JsonNode quiz;

    @Enumerated(EnumType.STRING)
    private CidPillar pillar;

    @ManyToOne
    @JoinColumn(name = "trail_id", nullable = false)
    private Trail trail;
}
