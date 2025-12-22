package org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.config;

import org.iesalixar.daw2.GarikAsatryan.dwese_ticket_logger_webapp.services.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Entrando en el método securityFilterChain");
        http
                .authorizeHttpRequests(auth -> {
                    logger.debug("Configurando autorización de solicitudes HTTP");
                    auth
                            // 1. Permitir recursos estáticos y páginas públicas
                            .requestMatchers("/", "/login", "/error", "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                            // 2. Rutas de Administración (Solo ADMIN)
                            .requestMatchers("/admin/**").hasRole("ADMIN")

                            // 3. Rutas de Gestión (ADMIN o MANAGER)
                            .requestMatchers("/regions/**", "/provinces/**").hasAnyRole("ADMIN", "MANAGER")

                            // 4. Rutas de Usuario (ADMIN, MANAGER o USER)
                            .requestMatchers("/tickets/**").hasAnyRole("ADMIN", "MANAGER", "USER")

                            .anyRequest().authenticated();
                })
                .formLogin(form -> {
                    logger.debug("Configurando formulario de inicio de sesión");
                    form
                            .loginPage("/login")
                            .defaultSuccessUrl("/", true)
                            .permitAll();
                })
                .sessionManagement(session -> {
                    logger.debug("Configurando política de gestión de sesiones");
                    session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                });
        logger.info("Saliendo del método securityFilterChain");
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Entrando en el método passwordEncoder");
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        logger.info("Saliendo del método passwordEncoder");
        return encoder;
    }
}
