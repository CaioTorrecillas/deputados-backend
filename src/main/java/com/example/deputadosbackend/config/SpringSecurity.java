package com.example.deputadosbackend.config;




import com.example.deputadosbackend.Jwt.JWTAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SpringSecurity {

    private final JWTAuthFilter jwtAuthFilter;

    public SpringSecurity(JWTAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/users/**").permitAll() //remover posteriormente
                        .requestMatchers("/deputados/**").permitAll()
                        .requestMatchers("/deputados").permitAll()
                        .requestMatchers("/proposicao/**").permitAll()
                        .requestMatchers("/proposicao/sincronizar-pl").permitAll()
                        .requestMatchers("/proposicao/{id}**").permitAll()
                        .requestMatchers("/{id}/proposicoes").permitAll()
                        .requestMatchers("/{id}/proposicoes").permitAll()
                        .requestMatchers("/proposicao/vincular-autores").permitAll()


                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}