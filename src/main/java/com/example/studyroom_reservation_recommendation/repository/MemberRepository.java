package com.example.studyroom_reservation_recommendation.repository;

import com.example.studyroom_reservation_recommendation.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {

}