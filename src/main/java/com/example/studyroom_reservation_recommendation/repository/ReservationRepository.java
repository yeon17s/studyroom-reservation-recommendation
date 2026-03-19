package com.example.studyroom_reservation_recommendation.repository;

import com.example.studyroom_reservation_recommendation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// JpaRepository<엔티티 클래스, PK(기본키) 데이터 타입>
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    // 중복 예약 확인
    boolean existsByDateAndTimeSlot(String date, String timeslot);

    // 목록 조회
    List<Reservation> findAllByOrderByDateAscTimeSlotAsc();
    // 학번으로 개인 목록 조회
    List<Reservation> findByStudentIdOrderByDateAscTimeSlotAsc(String studentId);

    // 검색
    List<Reservation> findByNameContainingOrStudentIdContainingOrderByDateAscTimeSlotAsc(String name, String studentId);

    List<Reservation> findByDate(String date);
}