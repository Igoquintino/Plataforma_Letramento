package com.projeto.tcc.letramento.dto;

import com.projeto.tcc.letramento.enums.CidPillar;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.JsonNode;

public record ScenarioRequestDTO(
        @NotBlank String titleScenario,
        @NotNull JsonNode xrayData,
        @NotNull JsonNode quiz,
        @NotNull CidPillar pillar,
        @NotNull Long trailId
) {}
