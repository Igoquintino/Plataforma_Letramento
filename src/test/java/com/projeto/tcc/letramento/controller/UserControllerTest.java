package com.projeto.tcc.letramento.controller;

import org.junit.jupiter.api.Test;
import com.projeto.tcc.letramento.dto.UserUpdateDTO;
import com.projeto.tcc.letramento.model.User;
import com.projeto.tcc.letramento.service.ProgressService;
import com.projeto.tcc.letramento.service.ScenarioService;
import com.projeto.tcc.letramento.service.UserService;
import com.projeto.tcc.letramento.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertTrue;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    // Mocks de infraestrutura necessários para carregar o contexto web
    @MockitoBean
    private ProgressService progressService;

    @MockitoBean
    private ScenarioService scenarioService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser
    @DisplayName("Deve buscar os dados do perfil do usuário logado com sucesso")
    void testGetUserProfile_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName("Igo Quintino");
        mockUser.setEmail("igo@ufopa.edu.br");
        mockUser.setCourse("Sistemas de Informação");
        mockUser.setAcademicLevel("Graduação");

        when(userService.findById(userId)).thenReturn(mockUser);

        // Act & Assert
        mockMvc.perform(get("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Igo Quintino"))
                .andExpect(jsonPath("$.course").value("Sistemas de Informação"));
    }

    @Test
    @WithMockUser
    @DisplayName("Deve atualizar o perfil acadêmico do aluno com sucesso")
    void testUpdateProfile_Success() throws Exception {
        // Arrange
        Long userId = 1L;
        UserUpdateDTO updateData = new UserUpdateDTO("Sistemas de Informação", "Superior Completo");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Igo Quintino");
        updatedUser.setEmail("igo@ufopa.edu.br");
        updatedUser.setCourse("Sistemas de Informação");
        updatedUser.setAcademicLevel("Superior Completo");

        when(userService.updateProfile(eq(userId), any(UserUpdateDTO.class))).thenReturn(updatedUser);

        // Act & Assert
        mockMvc.perform(patch("/api/users/" + userId + "/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData))
                        .with(csrf())) // Proteção CSRF obrigatória para verbos modificadores (PATCH)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.academicLevel").value("Superior Completo"));
    }

    @Test
    void placeholder_userController() {
        // TODO: MockMvc tests for user profile endpoints
        assertTrue(true);
    }
}
