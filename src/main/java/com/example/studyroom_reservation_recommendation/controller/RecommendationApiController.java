package com.example.studyroom_reservation_recommendation.controller;

import com.example.studyroom_reservation_recommendation.service.RecommendationService;
import com.example.studyroom_reservation_recommendation.service.RecommendationService.RecommendationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // 데이터(JSON) 반환
@RequestMapping("/api/recommend") // API 전용 URL
@RequiredArgsConstructor
public class RecommendationApiController {
    private final RecommendationService recommendationService;

    // 프론트엔드에서 날짜와 목적을 보내면 추천 시간대 리스트를 JSON으로 반환하는 API
    @GetMapping
    public ResponseEntity<List<RecommendationResult>> getRecommendations(
            @RequestParam String date,
            @RequestParam(required = false) String purpose) {

        // 날짜가 안 넘어왔을 때 직접 예외를 던짐
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("날짜(date) 파라미터는 필수입니다.");
        }

        // 1. 추천 알고리즘 호출
        List<RecommendationResult> results = recommendationService.recommendTimeSlot(date, purpose);

        // 2. HTTP 상태 코드 200(OK)과 함께 결과 리스트를 JSON 형태로 포장해서 반환
        return ResponseEntity.ok(results);
    }
}
