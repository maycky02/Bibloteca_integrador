package com.trilce.Bibloteca.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Mantén deshabilitado si solo usas login estándar
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/admin/**").hasAuthority("ADMINISTRADOR")  // Cambiado a hasAuthority
                        .requestMatchers("/lector/**").hasAuthority("LECTOR")        // Cambiado a hasAuthority
                        .anyRequest().permitAll())

              
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/inicio", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
