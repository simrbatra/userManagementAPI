package com.example.usermanagementapi.config;

import com.example.usermanagementapi.repository.UserRepository;
import com.example.usermanagementapi.services.CustomUserDetailsService;
import com.example.usermanagementapi.services.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration  //register the class as part of the application context
@EnableWebSecurity //imports spring-security's defaults so that we can override them
public class SecurityConfig {
    @Autowired
    private UserRepository userRepository;

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;
    public SecurityConfig(CustomUserDetailsService customUserDetailsService,JwtAuthFilter jwtAuthFilter){
        this.customUserDetailsService=customUserDetailsService;
        this.jwtAuthFilter=jwtAuthFilter;
    }
    @Bean
    public UserDetailsService userDetailsService(){
        return new CustomUserDetailsService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        //DaoAuthenticationProvider is the standard JDBC/JPA provider.
        //It compares the raw password from the login request to the bycrypt-hashed password stored in the db using the passwordEncoder bean
        DaoAuthenticationProvider authProvider=new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        //When we register a user, hash the plain text with passwordEncoder().encode(rawPassword) before persisting.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        //Allows any origin (*) to call your API with the listed HTTP verbs.
        CorsConfiguration config=new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        //AllowCredentials=true lets browsers send cookies/Auth headers across origins.
        config.setAllowCredentials(true);

        //we need to inject this into the filter chain so browsers don’t block cross‑site requests.
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**",config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors->cors.configurationSource(corsConfigurationSource()))  //link CORS bean
                .csrf(AbstractHttpConfigurer::disable)   //disable CSRF
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth->auth.requestMatchers("/api/users/register").permitAll() //open endpoint
                        .requestMatchers("/api/users/page").permitAll()   //open endpoint
                        .requestMatchers("/api/users").hasRole("ADMIN")   //only ADMIN
                        .requestMatchers("/api/users").hasAnyRole("USER","ADMIN") //USER or ADMIN
                        .anyRequest().authenticated())
//                        .httpBasic(Customizer.withDefaults())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); //no HTTP session
        return http.build();
    }

}