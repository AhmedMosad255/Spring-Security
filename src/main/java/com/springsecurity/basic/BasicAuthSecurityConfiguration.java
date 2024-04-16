package com.springsecurity.basic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class BasicAuthSecurityConfiguration {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) -> {
            auth
                    .requestMatchers("/users").hasRole("USER")
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated();
        });
        //http.formLogin(Customizer.withDefaults());
        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.httpBasic(Customizer.withDefaults());
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
    @Bean
    public DataSource dataSource(){
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                .build();
    }
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        var user = User.withUsername("ahmed")
                .password("2100401")
                .passwordEncoder(str -> bCryptPasswordEncoder().encode(str))
                .roles("USER")
                .build();
        var admin = User.withUsername("admin")
                .password("2100401")
                .passwordEncoder(str -> bCryptPasswordEncoder().encode(str))
                .roles("ADMIN")
                .build();
        var userDetailsManager = new JdbcUserDetailsManager(dataSource);
        userDetailsManager.createUser(user);
        userDetailsManager.createUser(admin);
        return userDetailsManager;
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
