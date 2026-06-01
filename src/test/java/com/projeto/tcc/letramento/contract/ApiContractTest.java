package com.projeto.tcc.letramento.contract;
import com.projeto.tcc.letramento.controller.UserController;
import com.projeto.tcc.letramento.enums.UserRole;
import com.projeto.tcc.letramento.model.User;
import com.projeto.tcc.letramento.security.JwtTokenProvider;
import com.projeto.tcc.letramento.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class ApiContractTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private UserService userService;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("CONTRATO GLOBAL — O payload de UserDTO deve manter rigorosamente a estrutura de chaves e tipos exigida pelo React")
    void validateUserDTO_PayloadContract() throws Exception {
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName("Igo Quintino");
        mockUser.setEmail("igo@ufopa.edu.br");
        mockUser.setCourse("Sistemas de Informação");
        mockUser.setAcademicLevel("Graduação");
        mockUser.setRole(UserRole.ALUNO);
        mockUser.setCreatedAt(LocalDateTime.now());

        when(userService.findById(userId)).thenReturn(mockUser);

        mockMvc.perform(get("/api/users/" + userId)
                        .with(oauth2Login())
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").isString())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.course").isString())
                .andExpect(jsonPath("$.academicLevel").isString())
                .andExpect(jsonPath("$.role").isString())
                .andExpect(jsonPath("$.createdAt").exists())
                // Garante que chaves privadas de infraestrutura do backend (como o googleId) nunca vazem neste DTO
                .andExpect(jsonPath("$.googleId").doesNotExist());
    }
}
