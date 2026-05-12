package com.projeto.tcc.letramento.dto;

import tools.jackson.databind.JsonNode;

public record AnswerDTO(Long scenarioId, JsonNode answers) {
}
