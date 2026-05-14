package com.projeto.tcc.letramento.dto;

/*
* DTO que encapsula a resposta do login. Ele devolve o token JWT que o React vai armazenar e os dados do usuário logado.
* */
public record AuthResponseDTO(String token, UserDTO user) {
}
