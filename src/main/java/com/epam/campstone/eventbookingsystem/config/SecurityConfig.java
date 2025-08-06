package com.epam.campstone.eventbookingsystem.config;

import com.epam.campstone.eventbookingsystem.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import static com.epam.campstone.eventbookingsystem.util.AppConstants.ROLE_ADMIN;
import static com.epam.campstone.eventbookingsystem.util.AppConstants.ROLE_USER;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Value("${token.validity.seconds}")
    private int tokenValiditySeconds;

    @Value("${homepage.url}")
    private String homepageUrl;


    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/home",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**",
                    "/auth/**",
                    "/error/**"
                ).permitAll()
                .requestMatchers(
                    "/user/**"
                ).hasRole(ROLE_USER)
                .requestMatchers(
                    "/admin/**"
                ).hasRole(ROLE_ADMIN)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl(homepageUrl, true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults().matcher("/auth/logout"))
                .logoutSuccessUrl("/auth/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecret")
                .tokenValiditySeconds(tokenValiditySeconds)
                .userDetailsService(userDetailsService)
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/error/access-denied")
            )
            .csrf(AbstractHttpConfigurer::disable); // For development only, enable in production

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> response.sendRedirect(homepageUrl);
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            String errorMessage = "Invalid username or password";
            if (exception.getMessage().equalsIgnoreCase("User account is not active")) {
                errorMessage = "Your account is not active. Please check your email to activate it.";
            }
            response.sendRedirect("/auth/login?error=true&message=" + errorMessage);
        };
    }
}
