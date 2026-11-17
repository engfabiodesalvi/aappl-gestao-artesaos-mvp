package br.org.aappl.mvp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(a -> a
                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers("/fechamento/**").hasRole("COORDENADORA")
                .anyRequest().authenticated())
            .formLogin(f -> f
                .loginPage("/login")
                .defaultSuccessUrl("/caixa", true)
                .failureUrl("/login?erro"))
            .logout(l -> l
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?saiu")
                .invalidateHttpSession(true));
        return http.build();
    }
}
