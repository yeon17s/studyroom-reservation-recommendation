package com.example.studyroom_reservation_recommendation.config;

import jakarta.servlet.DispatcherType;
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화 도구
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    // JSP forward / error dispatch는 인증 없이 통과시켜야 로그인 페이지가 렌더링됨
                    .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR, DispatcherType.INCLUDE).permitAll()
                    // 1. 누구나 접근 가능한 경로 (로그인 폼이 있는 메인 페이지 포함)
                    .requestMatchers("/", "/login", "/error", "/api/recommend", "/static/**", "/css/**", "/js/**").permitAll()
                    // 2. 관리자 전용 경로
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    // 3. 나머지는 로그인한 유저만 (예: /reserve 등록 등)
                    .anyRequest().authenticated()
            )
            .formLogin(form -> form
                    .loginPage("/")           // 로그인 폼이 메인에 있으므로 / 로 설정
                    .loginProcessingUrl("/login") // <form action="/login"> 과 일치해야 함
                    .defaultSuccessUrl("/", true) // 로그인 성공 시 다시 메인으로
                    .failureUrl("/?error=true")   // 로그인 실패 시 에러 파라미터 들고 메인으로
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/")    // 로그아웃 하면 메인으로
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            );
        return http.build();
    }
}
