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
        List<String> messages = new ArrayList<>();
        // 1. 혼잡도 관련 문구
        if (reservations == 0) {
            messages.add("현재 예약자가 없어 가장 조용하고 쾌적한 상태입니다.");
        } else if (reservations == 1) {
            messages.add("예약이 1건 있으나, 이용에 크게 지장 없는 여유로운 편입니다.");
        } else {
            // 예약이 2건 이상인 경우 (감점이 많이 된 상태)
            messages.add("다른 시간대보다 상대적으로 예약 건수가 적어 추천 리스트에 올랐습니다.");
        }

        // 2. 목적-시간대 관련 문구
        if (purpose != null) {
            boolean isStudy = purpose.contains("시험") || purpose.contains("공부") || purpose.contains("독서");
            boolean isTeam = purpose.contains("팀플") || purpose.contains("회의") || purpose.contains("발표");

            if (isStudy) {
                if (isMorning(timeSlot)) {
                    messages.add("오전은 집중도가 높아 개인 학습에 최적인 시간대입니다.");
                } else if (isEvening(timeSlot)) {
                    messages.add("저녁 시간은 주변이 다소 산만할 수 있어 집중력이 필요합니다.");
                }
            } else if (isTeam) {
                if (isAfternoon(timeSlot)) {
                    messages.add("활발한 소통과 회의가 권장되는 팀 프로젝트 황금 시간대입니다.");
                } else if (isMorning(timeSlot)) {
                    messages.add("오전은 정숙한 분위기가 유지되어야 하므로 소음에 유의해 주세요.");
                }
            }
        }

        // 3. 리스트에 담긴 문장들을 자연스럽게 한 문장으로 연결
        return String.join(" ", messages);
    }

    // 개별 시간대의 점수를 계산하는 헬퍼 메서드
    private TimeSlotScore calculateScore(String timeSlot, Long currentReservations, String purpose) {
        int score = 100;

        // 1. 혼잡도 감점 - 예약 1건 당 20점 감점
        int congestionPenalty = (int) (currentReservations * 20);
        score -= congestionPenalty;

        // 2. 목적-시간대 부적합 감점
        if (purpose != null) {
            boolean isStudy = purpose.contains("시험") || purpose.contains("공부") || purpose.contains("독서");
            boolean isTeam = purpose.contains("팀플") || purpose.contains("회의") || purpose.contains("발표");

            if (isStudy) {
                // 공부는 오전이 최적, 그 외 시간대는 '집중도 하락'으로 간주하여 감점
                if (isEvening(timeSlot)) {
                    score -= 20; // 저녁은 소음이 많으므로 큰 감점
                } else if (isAfternoon(timeSlot)) {
                    score -= 10; // 오후는 적당한 감점
                }
                // 오전(isMorning)은 감점 없음 (최적 상태 유지)

            } else if (isTeam) {
                // 팀플은 활발한 소통이 가능한 오후가 최적
                if (isMorning(timeSlot)) {
                    score -= 20; // 오전의 정숙한 분위기 저해 우려로 큰 감점
                } else if (isEvening(timeSlot)) {
                    score -= 10; // 너무 늦은 시간은 소폭 감점
                }
                // 오후(isAfternoon)는 감점 없음 (최적 상태 유지)
            }
        }

        // 3. 최종 점수 보정 (0점 밑으로 내려가지 않게 보호)
        return new TimeSlotScore(timeSlot, Math.max(0, score));
    }

    // 추천 시스템의 신뢰도를 위한 커트라인 점수 (예: 40점)
    private static final int RECOMMENDATION_CUTOFF = 40;

    // 핵심 추천 알고리즘 메서드
    public List<RecommendationResult> recommendTimeSlot(String date, String purpose) {
        // 1. 해당 날짜의 모든 얘약 데이터 가져오기 (혼잡도 계산용)
        List<Reservation> dailyReservations = reservationRepository.findByDate(date);

        // 2. 시간대별 예약 건수 그룹핑
        Map<String, Long> congestionMap = dailyReservations.stream()
                .collect(Collectors.groupingBy(Reservation::getTimeSlot, Collectors.counting()));

        // 3. 모든 시간대의 점수 계산
        List<TimeSlotScore> qualifiedScores = ALL_TIME_SLOTS.stream()
                .map(slot->calculateScore(slot, congestionMap.getOrDefault(slot, 0L), purpose))
                // 커트라인 적용: 설정한 점수 미만은 추천 대상에서 즉시 제외
                .filter(slotScore -> slotScore.score() >= RECOMMENDATION_CUTOFF)
                .collect(Collectors.toList());

        // 4. 동점자 분산을 위해 남은 항목들만 셔플
        Collections.shuffle(qualifiedScores);

        // 5. 점수 기준 내림차순 정렬 후 상위 2개만 추출
        List<TimeSlotScore> topScores = qualifiedScores.stream()
                .sorted(Comparator.comparingInt(TimeSlotScore::score).reversed())
                .limit(2)
                .toList(); // 기존 방식: .collect(Collectors.toList());

        // 6. 결과 DTO 리스트로 변환해 반환
        return topScores.stream()
                .map(slotScore -> RecommendationResult.builder()
                        .recommendedTime(slotScore.slot())
                        .score(slotScore.score())
                        // 각각의 슬롯에 맞는 reason을 생성해서 바로 넣어줌
                        .reason(generateReason(slotScore.slot(), congestionMap.getOrDefault(slotScore.slot(), 0L), purpose))
                        .build())
                .collect(Collectors.toList());
    }
}
