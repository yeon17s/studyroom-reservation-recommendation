package com.example.studyroom_reservation_recommendation.controller;

import com.example.studyroom_reservation_recommendation.entity.Reservation;
import com.example.studyroom_reservation_recommendation.repository.ReservationRepository;
import com.example.studyroom_reservation_recommendation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reserve") // 공통 URL 설정
public class ReservationController {
    private final ReservationService reservationService;

    // 메인 목록 조회 및 검색 (GET 요청)
    // 기존 action=main 과 action=search 를 하나로 합침
    @GetMapping
    public String mainPage(@RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String action,
                           Model model,
                           Authentication authentication) {
        List<Reservation> list;
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 검색어가 있으면 검색, 없으면 전체 조회
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = reservationService.searchReservations(keyword.trim());
            model.addAttribute("searchKeyword", keyword); // 검색어 유지
        } else {
            // 관리자 로그인이면 전체 목록, 아니면 개인 목록 조회
            if (isAdmin) {
                list = reservationService.getAllReservations();
            } else {
                list = reservationService.getStudentIdReservations(authentication.getName());
            }
        }
        model.addAttribute("list", list);

        // application.properties에 설정한 prefix/suffix 덕분에 "main"만 리턴해도 /WEB-INF/jsp/main.jsp로 이동함
        return "main";
    }

    // 예약 등록 (POST 요청, form의 action=insert 일 때만 실행)
    // Spring: JSP의 input name과 Reservation 객체의 필드명이 같으면 알아서 데이터(학번, 이름 등)를 자동으로 넣어줌
    @PostMapping(params = "action=insert")
    public String insertReservation(@ModelAttribute Reservation reservation, RedirectAttributes redirectAttributes) {
        try {
            reservationService.registerReservation(reservation);
            return "redirect:/reserve?success=true";
        } catch (Exception e) {
            // 중복 예약이나 기타 에러 발생 시
            // 1. 에러 메시지 전달
            redirectAttributes.addFlashAttribute("errorMsg", "이미 해당 날짜와 시간에 예약이 존재합니다. 다른 시간을 선택해 주세요.");

            // 2. 입력했던 데이터들을 다시 돌려보내서 사용자가 다시 치지 않게 함
            redirectAttributes.addFlashAttribute("prevData", reservation);

            return "redirect:/reserve"; // 다시 입력 폼으로 리다이렉트
        }
    }

    // 선택 삭제 (POST 요청, form의 action=delete 일 때만 실행)
    @PostMapping(params = "action=delete")
    public String deleteReservation(@RequestParam(value = "deleteIds", required = false) List<Long> deleteIds) {
        if (deleteIds != null && !deleteIds.isEmpty()) {
            reservationService.deleteReservations(deleteIds);
        }
        return "redirect:/reserve";
    }
}