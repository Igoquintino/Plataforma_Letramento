package com.projeto.tcc.letramento.dto;

import com.fasterxml.jackson.databind.JsonNode;
/*
* Quando o aluno terminar de responder o quiz ou a atividade prática no React, o frontend enviará este objeto para o
* backend validar
* */
public record AnswerDTO(Long scenarioId, JsonNode answers) {
}
