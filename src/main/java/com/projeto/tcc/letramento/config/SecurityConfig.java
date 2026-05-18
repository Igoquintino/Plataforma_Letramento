package com.projeto.tcc.letramento.config;

import com.projeto.tcc.letramento.security.JwtAuthenticationFilter;
import com.projeto.tcc.letramento.security.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar @PreAuthorize nos controllers futuramente
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final OAuth2AuthenticationSuccessHandler oauth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { http
            .cors(cors -> cors.configure(http))
//            .csrf(csrf -> csrf.disable()) // Desabilitado temporariamente para facilitar os testes REST via Postman/React

            .csrf(AbstractHttpConfigurer::disable) // Desabilitado temporariamente para facilitar os testes REST via Postman/React

            // Define que a aplicação não guardará estado de sessão no servidor
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


            .authorizeHttpRequests(auth -> auth
                    // Rotas Administrativas exigem explicitamente o papel de ADMIN
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")

                    // Endpoints públicos (caso queira liberar a listagem inicial de trilhas, por exemplo)
                    .requestMatchers(HttpMethod.GET, "/api/trails").permitAll()

                    // Qualquer outra requisição dentro da API precisa de autenticação ativa
                    .anyRequest().authenticated()
            )
            // Configuração do Fluxo Google OAuth2
            .oauth2Login(oauth2 -> oauth2
                    // Aponta para o handler que vai gerar o JWT local após o sucesso no Google
                    .successHandler(oauth2SuccessHandler)
            );
        // Injeta o seu filtro de JWT personalizado antes do filtro padrão de autenticação do Spring
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
