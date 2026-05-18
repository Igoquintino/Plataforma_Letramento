package com.projeto.tcc.letramento.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.projeto.tcc.letramento.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    // Chave secreta definida no application.properties (com um fallback seguro para desenvolvimento)
    @Value("${app.jwt.secret:minha_chave_secreta_super_segura_para_o_tcc_ufopa_2026}")
    private String secretKey;

    // Tempo de expiração do Token (padrão de 24 horas)
    @Value("${app.jwt.expiration-ms:86400000}")
    private long expirationMs;

    /**
     * Gera o token JWT customizado carregando os dados essenciais do usuário.
     */
    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return JWT.create()
                .withSubject(String.valueOf(user.getId())) // Subject guarda o ID primário do banco
                .withClaim("email", user.getEmail())
                .withClaim("name", user.getName())
                .withClaim("role", user.getRole().name())   // Guarda se é ALUNO ou ADMIN
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMs))
                .sign(algorithm);
    }

    /**
     * Valida a assinatura e o tempo de expiração do token recebido.
     * Retorna o JWT decodificado ou null caso seja inválido/expirado.
     */
    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            return JWT.require(algorithm)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            // Token corrompido, assinatura inválida ou expirado
            return null;
        }
    }
}
