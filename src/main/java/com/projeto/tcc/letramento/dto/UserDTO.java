package com.projeto.tcc.letramento.dto;

/*
 * DTO usado para devolver os dados do usuário para o frontend sem expor informações sensíveis,
 * como o googleId ou dados internos de controle.
 * */
public record UserDTO(Long id, String name, String email, String course, String academicLevel) {
}
