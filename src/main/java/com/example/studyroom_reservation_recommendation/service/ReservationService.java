package com.example.studyroom_reservation_recommendation.service;

import com.example.studyroom_reservation_recommendation.entity.Reservation;
import com.example.studyroom_reservation_recommendation.repository.ReservationRepository;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // 서비스 클래스
@RequiredArgsConstructor // final이 붙은 필드의 생성자를 롬복이 자동으로 만들어줌 (의존성 주입)
@Transactional(readOnly = true) // 기본적으로 데이터 변경 없는 읽기 전용으로 설정 (성능 최적화)
public class ReservationService {
    private final ReservationRepository reservationRepository;

    // 예약 등록 (기존 insert 대체)
    @Transactional
    public boolean registerReservation(Reservation reservation) {
        // 중복 예약 체크 로직
        boolean isDuplicate = reservationRepository.existsByDateAndTimeSlot(
                reservation.getDate(),
                reservation.getTimeSlot()
        );
        if (isDuplicate) {
            return false; // 중복이면 저장 안 하고 false 반환
        }
        // 중복이 아니면 저장 (INSERT 쿼리 자동 실행)
        reservationRepository.save(reservation);
        return true;
    }

    // 전체 목록 조회 (기존 getAll 대체)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAllByOrderByDateAscTimeSlotAsc();
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
