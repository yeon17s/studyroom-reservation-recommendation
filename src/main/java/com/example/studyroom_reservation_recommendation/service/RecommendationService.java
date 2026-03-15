package com.example.studyroom_reservation_recommendation.service;

import com.example.studyroom_reservation_recommendation.entity.Reservation;
import com.example.studyroom_reservation_recommendation.repository.ReservationRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final ReservationRepository reservationRepository;

    // 스터디룸 운영 시간대 (JSP reserveForm.jsp 기준)
    private static final List<String> ALL_TIME_SLOTS = Arrays.asList(
            "09:00-11:00", "11:00-13:00", "13:00-15:00",
            "15:00-17:00", "17:00-19:00", "19:00-21:00"
    );

    // 내부 데이터 전달용 DTO 클래스
    // Java 14+ Record 문법 - 롬복 필요 없음:
    private record TimeSlotScore(String slot, int score) {}
    //    @Getter
    //    @RequiredArgsConstructor
    //    private static class TimeSlotScore {
    //        private final String slot;
    //        private final int score;
    //    }
    @Getter
    @Builder
    public static class RecommendationResult {
        private String recommendedTime;
        private int score;
        private String reason;
    }

    private boolean isMorning(String timeSlot) {
        return timeSlot.startsWith("09") || timeSlot.startsWith("11");
    }

    private boolean isAfternoon(String timeSlot) {
        return timeSlot.startsWith("13") || timeSlot.startsWith("15") || timeSlot.startsWith("17");
    }

    private boolean isEvening(String timeSlot) {
        return timeSlot.startsWith("19");
    }

    private String generateReason(String timeSlot, Long reservations, String purpose) {
        StringBuilder reason = new StringBuilder();
        // 1. 혼잡도 관련 문구
        if (reservations == 0) {
            reason.append("현재 예약이 없어 쾌적하게 이용할 수 있습니다. ");
        } else {
            reason.append("다른 시간대 대비 비교적 여유로운 퍈입니다. ");
        }

        // 2. 목적 관련 문구
        if (purpose != null) {
            boolean isStudy = purpose.contains("시험") || purpose.contains("공부") || purpose.contains("독서");
            boolean isTeam = purpose.contains("팀플") || purpose.contains("회의") || purpose.contains("발표");

            if (isStudy && isMorning(timeSlot)) {
                reason.append("조용히 집중하기 좋은 오전 시간대라 더욱 추천합니다. ");
            } else if (isTeam && isAfternoon(timeSlot)) {
                reason.append("자유롭게 소통하며 회의하기 좋은 오후 시간대라 추천합니다. ");
            }
        }
        return reason.toString();
    }

    // 개별 시간대의 점수를 계산하는 헬퍼 메서드
    private TimeSlotScore calculateScore(String timeSlot, Long currentReservations, String purpose) {
        int baseScore = 100;
        int congestionPenalty = (int) (currentReservations * 20); // 예약 1건 당 20점 감점
        int purposeBonus = 0;
        int purposePenalty = 0;

        // 목적 텍스트 기반 가중치 부여 (키워드 매칭)
        if (purpose != null) {
            boolean isStudy = purpose.contains("시험") || purpose.contains("공부") || purpose.contains("독서");
            boolean isTeam = purpose.contains("팀플") || purpose.contains("회의") || purpose.contains("발표");

            if (isStudy) {
                if (isMorning(timeSlot)) purposeBonus += 15;        // 집중하기 좋은 오전 가산점
                else if (isEvening(timeSlot)) purposePenalty -= 10; // 붐비고 산만한 저녁 감점
            } else if (isTeam) {
                if (isAfternoon(timeSlot)) purposeBonus += 15;      // 활발한 오후 가산점
                else if (isMorning(timeSlot)) purposePenalty -= 15; // 조용한 오전 민폐 방지 감점
            }
        }

        int totalScore = baseScore - congestionPenalty + purposeBonus + purposePenalty;
        return new TimeSlotScore(timeSlot, Math.max(0, totalScore));
    }

    // 핵심 추천 알고리즘 메서드
    public List<RecommendationResult> recommendTimeSlot(String date, String purpose) {
        // 1. 해당 날짜의 모든 얘약 데이터 가져오기 (혼잡도 계산용)
        List<Reservation> dailyReservations = reservationRepository.findByDate(date);

        // 2. 시간대별 예약 건수 그룹핑
        Map<String, Long> congestionMap = dailyReservations.stream()
                .collect(Collectors.groupingBy(Reservation::getTimeSlot, Collectors.counting()));

        // 3. 모든 시간대의 점수 계산
        List<TimeSlotScore> scores = ALL_TIME_SLOTS.stream()
                .map(slot->calculateScore(slot, congestionMap.getOrDefault(slot, 0L), purpose))
                .collect(Collectors.toList());

        // 4. 동점자 분산 (랜덤 셔플)
        Collections.shuffle(scores);

        // 5. 점수 기준 내림차순 정렬 후 상위 2개만 추출
        List<TimeSlotScore> topScores = scores.stream()
                .sorted(Comparator.comparingInt(TimeSlotScore::getScore).reversed())
                .limit(2)
                .toList(); // 기존 방식: .collect(Collectors.toList());

        // 6. 결과 DTO 리스트로 변환해 반환
        return topScores.stream()
                .map(slotScore -> RecommendationResult.builder()
                        .recommendedTime(slotScore.getSlot())
                        .score(slotScore.getScore())
                        // 각각의 슬롯에 맞는 reason을 생성해서 바로 넣어줌
                        .reason(generateReason(slotScore.getSlot(), congestionMap.getOrDefault(slotScore.getSlot(), 0L), purpose))
                        .build())
                .collect(Collectors.toList());
    }
}
