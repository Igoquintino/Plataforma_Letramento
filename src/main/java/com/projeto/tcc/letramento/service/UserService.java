package com.projeto.tcc.letramento.service;

import com.projeto.tcc.letramento.dto.UserUpdateDTO;
import com.projeto.tcc.letramento.model.User;
import com.projeto.tcc.letramento.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    // Metodo chamado pelo Google OAuth após o login com sucesso
    public User processOAuthPostLogin(String email, String name, String googleId) {
        Optional<User> existUser = userRepository.findByGoogleId(googleId);

        if (existUser.isPresent()) {
            return existUser.get();
        } else {
            // Cria um usuário se for o primeiro acesso
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setGoogleId(googleId);
            return userRepository.save(newUser);
        }
    }

    // Atualiza os dados acadêmicos do aluno para segmentação na sua pesquisa
    public User updateProfile(Long userId, UserUpdateDTO data) {

        User user = findById(userId);
        user.setCourse(data.course());
        user.setAcademicLevel(data.academicLevel());
        return userRepository.save(user);
    }

    public User findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com o Google ID: " + googleId));
    }
}
