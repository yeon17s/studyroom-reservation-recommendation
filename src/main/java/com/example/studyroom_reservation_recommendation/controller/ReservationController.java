package com.example.studyroom_reservation_recommendation.controller;

import com.example.studyroom_reservation_recommendation.entity.Reservation;
import com.example.studyroom_reservation_recommendation.repository.ReservationRepository;
import com.example.studyroom_reservation_recommendation.service.ReservationService;
import lombok.RequiredArgsConstructor;
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
                           Model model) {
        List<Reservation> list;

        // 검색어가 있으면 검색, 없으면 전체 조회
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = reservationService.searchReservations(keyword.trim());
            model.addAttribute("searchKeyword", keyword); // 검색어 유지
        } else {
            list = reservationService.getAllReservations();
        }
        model.addAttribute("list", list);

        // application.properties에 설정한 prefix/suffix 덕분에 "main"만 리턴해도 /WEB-INF/jsp/main.jsp로 이동함
        return "main";
    }

    // 예약 등록 (POST 요청, form의 action=insert 일 때만 실행)
    // Spring: JSP의 input name과 Reservation 객체의 필드명이 같으면 알아서 데이터(학번, 이름 등)를 자동으로 넣어줌
    @PostMapping(params = "action=insert")
    public String insertReservation(@ModelAttribute Reservation reservation, Model model) {
        // 서비스 단에서 저장 및 중복 체크 시도
        boolean isSuccess = reservationService.registerReservation(reservation);

        if (!isSuccess) {
            // 중복일 경우 에러 메시지와 사용자가 입력했던 데이터를 다시 모델에 담아 화면으로 돌려보냄
            model.addAttribute("errorMsg", "해당 날짜와 시간에는 이미 예약이 존재합니다.");

            // 입력 데이터 유지
            model.addAttribute("name", reservation.getName());
            model.addAttribute("student_id", reservation.getStudentId());
            model.addAttribute("date", reservation.getDate());
            model.addAttribute("time_slot", reservation.getTimeSlot());
            model.addAttribute("people", reservation.getPeople());
            model.addAttribute("purpose", reservation.getPurpose());

            // 목록이 사라지지 않도록 다시 조회해서 담아줌
            model.addAttribute("list", reservationService.getAllReservations());

            return "main"; // 포워딩 (redirect 아님)
        }
        // 성공 시 리다이렉트
        return "redirect:/reserve?success=true";
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