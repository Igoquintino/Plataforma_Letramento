package com.projeto.tcc.letramento.dto;

import com.projeto.tcc.letramento.enums.CidPillar;
import tools.jackson.databind.JsonNode;

/*
* DTO crucial. Ele carrega a inteligência da sua engenharia reversa.
* */
public record ScenarioDTO(Long id, String title, CidPillar pillar, JsonNode xrayData) {
}
