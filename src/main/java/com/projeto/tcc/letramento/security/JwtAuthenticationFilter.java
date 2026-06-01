package com.projeto.tcc.letramento.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = recoverToken(request);

        if (token != null) {
            DecodedJWT decodedJwt = tokenProvider.validateToken(token);

            if (decodedJwt != null) {
                String userId = decodedJwt.getSubject();
                String role = decodedJwt.getClaim("role").asString();

                // Regra crucial do Spring Security: Roles precisam do prefixo "ROLE_"
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

                // Autentica o utilizador no contexto da requisição atual
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, // Define o ID como o Principal para capturarmos facilmente nos Controllers
                        null,
                        Collections.singletonList(authority)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Permite que a requisição continue o seu fluxo para o Controller correspondente
        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o Token do formato "Bearer token_aqui" contido no cabeçalho Authorization
     */
    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}
