package com.projeto.tcc.letramento.security;

import com.projeto.tcc.letramento.model.User;
import com.projeto.tcc.letramento.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Extraindo os dados fornecidos pelo Google OAuth2
        assert oAuth2User != null;
        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Regra de negócio: Processa o login salvando ou atualizando no banco via JPA
        User user = userService.processOAuthPostLogin(email, name, googleId);

        // Gera o JWT customizado da plataforma com o ID do usuário e a Role dele (ALUNO ou ADMIN)
        String token = jwtTokenProvider.generateToken(user);

        // O seu frontend vai capturar esse token e salvar no LocalStorage ou Context API
        String targetUrl = "http://localhost:5173/login-success?token=" + token;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
