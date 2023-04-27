package com.example.springsecurityapplication.config;

import com.example.springsecurityapplication.services.PersonDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    private final PersonDetailsService personDetailsService;

    @Bean
    public PasswordEncoder getPasswordEncode() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers("/admin").hasRole("ADMIN").requestMatchers("/authentication",
                "/registration", "/error", "/resources/**", "/static/**", "/css/**", "/pics/**", "/js/**", "/img/**",
                "/product", "/orders", "/users",
                "/product/info/{id}", "product/search", "/contacts").permitAll().anyRequest().hasAnyRole("USER",
                "ADMIN").and().formLogin().loginPage("/authentication").loginProcessingUrl("/process_login").defaultSuccessUrl("/personalaccount", true).failureUrl("/authentication?error").and().logout().logoutUrl("/logout").logoutSuccessUrl("/authentication");
        return http.build();
    }




    @Autowired
    public SecurityConfig(PersonDetailsService personDetailsService) {
        this.personDetailsService = personDetailsService;
    }

    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(personDetailsService).passwordEncoder(getPasswordEncode());
    }
}
