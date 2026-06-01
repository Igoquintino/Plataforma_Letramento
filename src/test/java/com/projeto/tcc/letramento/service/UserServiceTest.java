package com.projeto.tcc.letramento.service;

import com.projeto.tcc.letramento.dto.UserUpdateDTO;
import com.projeto.tcc.letramento.enums.UserRole;
import com.projeto.tcc.letramento.model.User;
import com.projeto.tcc.letramento.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Deve retornar um usuário existente pelo ID")
    void findById_Success() {
        // Arrange
        Long userId = 1L;
        User user = new User(userId, "Igo", "igo@email.com", "google-123", "Sistemas", "Graduação", UserRole.ALUNO, LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.findById(userId);

        // Assert
        assertNotNull(result);
        assertEquals("Igo", result.getName());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException ao buscar ID inexistente")
    void findById_NotFound() {
        // Arrange
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    @DisplayName("Deve retornar usuário existente durante login OAuth")
    void processOAuthPostLogin_UserExists() {
        // Arrange
        String googleId = "google-123";
        User existingUser = new User(1L, "Igo", "igo@email.com", googleId, null, null, UserRole.ALUNO, LocalDateTime.now());

        when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.of(existingUser));

        // Act
        User result = userService.processOAuthPostLogin("igo@email.com", "Igo", googleId);

        // Assert
        assertEquals(existingUser.getId(), result.getId());
        // Garante que o repositório NÃO tentou salvar um usuário novo
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve criar e retornar um novo usuário durante o login OAuth")
    void processOAuthPostLogin_NewUser() {
        // Arrange
        String email = "novo@email.com";
        String name = "Novo Aluno";
        String googleId = "google-999";

        when(userRepository.findByGoogleId(googleId)).thenReturn(Optional.empty());

        // Simulando o comportamento de salvar no banco e gerar ID
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L); // ID gerado pelo BD
            return savedUser;
        });

        // Act
        User result = userService.processOAuthPostLogin(email, name, googleId);

        // Assert
        assertNotNull(result.getId());
        assertEquals(email, result.getEmail());
        assertEquals(googleId, result.getGoogleId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Deve atualizar o perfil acadêmico do aluno com sucesso")
    void updateProfile_Success() {
        // Arrange
        Long userId = 1L;
        UserUpdateDTO updateDTO = new UserUpdateDTO("Sistemas de Informação", "Superior Incompleto");

        User existingUser = new User(userId, "Igo", "igo@email.com", "google-123", null, null, UserRole.ALUNO, LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.updateProfile(userId, updateDTO);

        // Assert
        assertEquals("Sistemas de Informação", result.getCourse());
        assertEquals("Superior Incompleto", result.getAcademicLevel());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
    }
}
