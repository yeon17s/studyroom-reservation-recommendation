package com.example.studyroom_reservation_recommendation.service;

import com.example.studyroom_reservation_recommendation.entity.Reservation;
import com.example.studyroom_reservation_recommendation.repository.ReservationRepository;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service // 서비스 클래스
@RequiredArgsConstructor // final이 붙은 필드의 생성자를 롬복이 자동으로 만들어줌 (의존성 주입)
@Transactional(readOnly = true) // 기본적으로 데이터 변경 없는 읽기 전용으로 설정 (성능 최적화)
public class ReservationService {
    private final ReservationRepository reservationRepository;

    // 예약 등록 (기존 insert 대체, 동시성 제어 적용)
    @Transactional
    public boolean registerReservation(Reservation reservation) {
        try {
            // 1. 정상적인 상황이거나 동시 접속 중 1등으로 도착했을 때
            reservationRepository.save(reservation);
            return true; // 저장되고 true 반환
        } catch (DataIntegrityViolationException e) {
            // 2. 동시 접속 중 2등으로 도착했거나 이미 예약이 있는데 억지로 시도했을 때
            log.warn("예약 중복 발생 (DB 방어): {} - {}", reservation.getDate(), reservation.getTimeSlot());
            return false; // 에러로 저장하지 못해 false 반환, 컨트롤러가 화면에 에러 메시지 띄움
        }
    }

    // 전체 목록 조회 (기존 getAll 대체)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAllOrderByDateAscTimeSlotAsc();
    }

    // 학번으로 개인 목록 조회
    public List<Reservation> getStudentIdReservations(String studentId) {
        return reservationRepository.findByStudentIdOrderByDateAscTimeSlotAsc(studentId);
    }

    // 검색 (기존 search 대체)
    public List<Reservation> searchReservations(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllReservations(); // 검색어가 없으면 전체 조회
        }
        return reservationRepository.findByNameContainingOrStudentIdContainingOrderByDateAscTimeSlotAsc(keyword, keyword);
    }

    // 선택 삭제 (기존 delete 대체)
    @Transactional
    public void deleteReservations(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            // 전달받은 ID 리스트에 해당하는 데이터들을 한 번에 삭제 (DELETE 쿼리 자동 실행)
            reservationRepository.deleteAllByIdInBatch(ids);
        }
    }
}
