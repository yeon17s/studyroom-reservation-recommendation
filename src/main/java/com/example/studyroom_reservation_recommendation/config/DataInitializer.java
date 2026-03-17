package com.example.studyroom_reservation_recommendation.config;

import com.example.studyroom_reservation_recommendation.entity.Member;
import com.example.studyroom_reservation_recommendation.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // 이미 유저가 있으면 생성하지 않음
        if (memberRepository.count() > 0) return;
        
        // 1. 관리자 생성 (ID: admin / PW: 1234)
        memberRepository.save(Member.builder()
                .studentId("admin")
                .password(passwordEncoder.encode("1234"))
                .name("관리자")
                .role(Member.Role.ADMIN)
                .build());

        // 2. 일반 학생 생성 (ID: 202202152 / PW: 1234)
        memberRepository.save(Member.builder()
                .studentId("202202152")
                .password(passwordEncoder.encode("1234"))
                .name("박서연")
                .role(Member.Role.USER)
                .build());

        System.out.println("테스트용 계정이 생성되었습니다. (admin / 202202152)");
    }
}
