package com.shopapp.product_service.config;

import com.shopapp.product_service.auth.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.shopapp.product_service.model.Role.ADMIN;
import static com.shopapp.product_service.model.Role.USER;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // ... (Authorize Requests เดิม)
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(GET, "/api/v1/products/**").permitAll()

                        .requestMatchers("/oauth2/authorization/**").permitAll()
                        .requestMatchers("/login/oauth2/code/**").permitAll()
                        .requestMatchers("/oauth2/loginSuccess").permitAll()

                        .requestMatchers(POST, "/api/v1/products/**").hasAnyAuthority(ADMIN.name())
                        .requestMatchers(PUT, "/api/v1/products/**").hasAnyAuthority(ADMIN.name())
                        .requestMatchers(DELETE, "/api/v1/products/**").hasAnyAuthority(ADMIN.name())
                        .requestMatchers("/api/v1/orders/admin/**").hasAnyAuthority(ADMIN.name())

                        .requestMatchers("/api/v1/cart/**").hasAnyAuthority(USER.name())
                        .requestMatchers("/api/v1/orders/**").hasAnyAuthority(USER.name())

                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth -> oauth
                        .successHandler(oAuth2SuccessHandler)
                        .failureUrl("/oauth2/loginFailure")
                )

                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}