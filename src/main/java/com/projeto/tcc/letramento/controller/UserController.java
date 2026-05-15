package com.projeto.tcc.letramento.controller;

import com.projeto.tcc.letramento.dto.UserDTO;
import com.projeto.tcc.letramento.dto.UserUpdateDTO;
import com.projeto.tcc.letramento.model.User;
import com.projeto.tcc.letramento.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Atualiza o perfil acadêmico do aluno (Curso e Nível).
     * Essencial para a segmentação de dados da pesquisa do TCC.
     */
    @PatchMapping("/{id}/profile")
    public ResponseEntity<UserDTO> updateProfile(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDTO data) {

        User updatedUser = userService.updateProfile(id, data);

        // Convertendo a entidade para DTO para retorno seguro
        UserDTO response = new UserDTO(
                updatedUser.getId(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getCourse(),
                updatedUser.getAcademicLevel()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Busca os dados do perfil do usuário logado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Long id) {
        User user = userService.findById(id);
        UserDTO response = new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCourse(),
                user.getAcademicLevel()
        );
        return ResponseEntity.ok(response);
    }
}
