package com.sahyadri.sahyadripooltrip.config;

import java.util.List;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sahyadri.sahyadripooltrip.repository.UserRepository;
import com.sahyadri.sahyadripooltrip.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final UserRepository userRepository;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        UserRepository userRepository) {

                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.userRepository = userRepository;
        }

        // =====================================================
        // USER DETAILS SERVICE
        // =====================================================

        @Bean
        public UserDetailsService userDetailsService() {

                return username -> userRepository.findByEmail(username)
                                .map(user -> User.builder()
                                                .username(user.getEmail())
                                                .password(user.getPasswordHash())
                                                .roles(user.getRole().name())
                                                .build())
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }

        // =====================================================
        // AUTHENTICATION PROVIDER
        // =====================================================

        @Bean
        public AuthenticationProvider authenticationProvider(
                        UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

                provider.setPasswordEncoder(passwordEncoder);

                return provider;
        }

        // =====================================================
        // AUTHENTICATION MANAGER
        // =====================================================

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration) throws Exception {

                return configuration.getAuthenticationManager();
        }

        // =====================================================
        // SECURITY FILTER CHAIN
        // =====================================================

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http)
                        throws Exception {

                http
                                // =================================================
                                // CSRF
                                // =================================================
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(csrf -> csrf.disable())

                                // =================================================
                                // SESSION
                                // =================================================

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(
                                                                SessionCreationPolicy.STATELESS))

                                // =================================================
                                // EXCEPTION HANDLING
                                // =================================================

                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(
                                                                authenticationEntryPoint())
                                                .accessDeniedHandler(
                                                                accessDeniedHandler()))

                                // =================================================
                                // AUTHORIZATION
                                // =================================================

                                .authorizeHttpRequests(auth -> auth

                                                // =========================
                                                // PUBLIC
                                                // =========================

                                                // =========================
                                                // FRONTEND STATIC RESOURCES
                                                // =========================
                                                .requestMatchers(
                                                                "/",
                                                                "/index.html",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/assets/**",
                                                                "/data/**",
                                                                "/favicon.ico",
                                                                "/trips", "/find-trips", "/driver-register",
                                                                "/agency-register", "/hotel-register", "/register", "/login",
                                                                "/forts", "/spots", "/stays", "/about", "/contact", "/partner",
                                                                "/safety", "/complaint", "/profile", "/dashboard", "/create-trip",
                                                                "/create-property", "/return", "/how", "/privacy", "/terms",
                                                                "/refund", "/trust", "/forgot-password", "/settings-password", "/india-nature", "/nature")
                                                .permitAll()

                                                // =========================
                                                // PUBLIC AUTH / REGISTRATION
                                                // =========================
                                                .requestMatchers(
                                                                "/api/auth/register",
                                                                "/api/auth/login",
                                                                "/api/auth/password/forgot/email",
                                                                "/api/auth/password/forgot/phone",
                                                                "/api/auth/password/verify",
                                                                "/api/auth/password/reset",
                                                                "/api/partners/driver/register",
                                                                "/api/partners/agency/register",
                                                                "/api/partners/hotel/register",
                                                                "/api/contact")
                                                .permitAll()

                                                // Public fort discovery for website visitors.
                                                .requestMatchers(HttpMethod.GET, "/api/forts/**", "/api/spots/**", "/api/destinations/**", "/api/bike-road-trips/**")
                                                .permitAll()

                                                .requestMatchers(HttpMethod.POST, "/api/spots/import", "/api/destinations/import", "/api/bike-road-trips/import")
                                                .hasRole("ADMIN")
                                                // =========================
                                                // USER PROFILE
                                                // =========================

                                                .requestMatchers(
                                                                "/api/users/profile",
                                                                "/api/users/password")
                                                .authenticated()

                                                // =========================
                                                // ADMIN
                                                // =========================

                                                .requestMatchers("/api/admin/**")
                                                .hasRole("ADMIN")

                                                // =========================
                                                // DRIVER
                                                // =========================

                                                .requestMatchers("/api/driver/**")
                                                .hasRole("DRIVER")

                                                .requestMatchers("/api/partner/vehicle-options").permitAll()
                                                .requestMatchers("/api/partner/driver/profile").hasRole("DRIVER")
                                                .requestMatchers("/api/partner/agency/profile").hasRole("AGENCY")

                                                .requestMatchers("/api/sos/admin", "/api/sos/admin/**",
                                                                "/api/complaints/admin", "/api/complaints/admin/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers("/api/sos/**", "/api/complaints/**")
                                                .authenticated()
                                                .requestMatchers("/api/documents").hasRole("ADMIN")

                                                // =========================
                                                // PAYMENTS
                                                // =========================
                                                .requestMatchers(HttpMethod.POST, "/api/payments/webhook").permitAll()
                                                .requestMatchers("/api/payments/**").hasRole("TRAVELER")

                                                // =========================
                                                // PROPERTIES - VIEW
                                                // =========================

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/properties")
                                                .hasAnyRole(
                                                                "TRAVELER",
                                                                "HOTEL_OWNER",
                                                                "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/properties/search")
                                                .hasAnyRole(
                                                                "TRAVELER",
                                                                "HOTEL_OWNER",
                                                                "ADMIN")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/property-bookings/availability/**")
                                                .hasAnyRole(
                                                                "TRAVELER",
                                                                "HOTEL_OWNER",
                                                                "ADMIN")

                                                // =========================
                                                // PROPERTIES - MANAGE
                                                // =========================

                                                .requestMatchers("/api/properties/**")
                                                .hasRole("HOTEL_OWNER")

                                                // =========================
                                                // PROPERTY BOOKINGS
                                                // =========================

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/property-bookings")
                                                .hasRole("TRAVELER")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/property-bookings/my")
                                                .hasRole("TRAVELER")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/property-bookings/property/**")
                                                .hasRole("HOTEL_OWNER")

                                                .requestMatchers(HttpMethod.POST, "/api/property-bookings/*/check-in", "/api/property-bookings/*/check-out").hasRole("HOTEL_OWNER")
                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/property-bookings/**")
                                                .hasRole("TRAVELER")

                                                // =========================
                                                // REVIEWS
                                                // =========================

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/reviews")
                                                .hasRole("TRAVELER")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/reviews/my")
                                                .hasRole("TRAVELER")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/reviews/**")
                                                .hasRole("TRAVELER")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/reviews/**")
                                                .permitAll()

                                                // =========================
                                                // TRAVELER
                                                // =========================

                                                .requestMatchers("/api/traveler/**")
                                                .hasRole("TRAVELER")

                                                // =========================
                                                // TRIPS - TRAVELER
                                                // =========================

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/trips",
                                                                "/api/trips/search",
                                                                "/api/trips/vehicles",
                                                                "/api/trips/pricing/quote")
                                                .permitAll()
                                                // =========================
                                                // TRIPS - DRIVER
                                                // =========================

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/trips")
                                                .hasRole("DRIVER")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/trips/my")
                                                .hasRole("DRIVER")

                                                .requestMatchers(
                                                                HttpMethod.PUT,
                                                                "/api/trips/**")
                                                .hasRole("DRIVER")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/trips/**")
                                                .hasRole("DRIVER")

                                                // =========================
                                                // TRIP BOOKINGS
                                                // =========================

                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/bookings")
                                                .hasRole("TRAVELER")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/bookings/my")
                                                .hasRole("TRAVELER")

                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/bookings/trip/**")
                                                .hasRole("DRIVER")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/bookings/driver/**")
                                                .hasRole("DRIVER")

                                                .requestMatchers(
                                                                HttpMethod.DELETE,
                                                                "/api/bookings/**")
                                                .hasRole("TRAVELER")

                                                // =========================
                                                // EVERYTHING ELSE
                                                // =========================

                                                .anyRequest()
                                                .authenticated())

                                // =================================================
                                // JWT FILTER
                                // =================================================

                                .addFilterBefore(
                                                jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        // =====================================================
        // 401 - UNAUTHORIZED
        // =====================================================

        @Bean
        public AuthenticationEntryPoint authenticationEntryPoint() {

                return (request, response, authException) -> {

                        response.setStatus(
                                        HttpServletResponse.SC_UNAUTHORIZED);

                        response.setContentType("application/json");

                        response.getWriter().write("""
                                        {
                                            "success": false,
                                            "message": "Authentication required"
                                        }
                                        """);
                };
        }

        // =====================================================
        // 403 - FORBIDDEN
        // =====================================================

        @Bean
        public AccessDeniedHandler accessDeniedHandler() {

                return (request, response, accessDeniedException) -> {

                        response.setStatus(
                                        HttpServletResponse.SC_FORBIDDEN);

                        response.setContentType("application/json");

                        response.getWriter().write("""
                                        {
                                            "success": false,
                                            "message": "Access denied"
                                        }
                                        """);
                };
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration configuration = new CorsConfiguration();

                configuration.setAllowedOriginPatterns(List.of(
                                "http://localhost:*",
                                "http://127.0.0.1:*",
                                "https://*.pages.dev"));

                configuration.setAllowedMethods(List.of(
                                "GET",
                                "POST",
                                "PUT",
                                "DELETE",
                                "OPTIONS"));

                configuration.setAllowedHeaders(List.of("*"));

                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", configuration);

                return source;
        }
}