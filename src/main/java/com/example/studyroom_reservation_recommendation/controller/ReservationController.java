package com.example.studyroom_reservation_recommendation.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import studyroom.model.Reservation;
import studyroom.model.ReservationDAO;

@WebServlet("/reserve")
public class ReservationController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ReservationDAO dao = new ReservationDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doProcess(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doProcess(request, response);
    }

    protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String action = request.getParameter("action");

        // 목록 전체 보기 (기본값)
        if (action == null || action.equals("main")) {
            List<Reservation> list = dao.getAll();
            request.setAttribute("list", list); 
            
            RequestDispatcher rd = request.getRequestDispatcher("/main.jsp");
            rd.forward(request, response);
        }
        
        // 예약 등록 요청
        else if ("insert".equals(action)) {
            String name = request.getParameter("name");
            String studentId = request.getParameter("student_id");
            String date = request.getParameter("date");
            String timeSlot = request.getParameter("time_slot");
            
            // 중복 예약 확인
            if (dao.isBooked(date, timeSlot)) {
                // 1. 에러 메시지 설정
                request.setAttribute("errorMsg", "해당 날짜와 시간에는 이미 예약이 존재합니다.");
                
                // 2. 입력했던 데이터 유지 (사용자가 다시 치지 않게)
                request.setAttribute("name", name);
                request.setAttribute("student_id", studentId);
                request.setAttribute("date", date);
                request.setAttribute("time_slot", timeSlot);
                request.setAttribute("people", request.getParameter("people"));
                request.setAttribute("purpose", request.getParameter("purpose"));
                
                // 3. 우측 목록 데이터도 다시 조회해서 같이 보냄 (안 그러면 목록이 사라짐)
                List<Reservation> list = dao.getAll();
                request.setAttribute("list", list);
                
                // 4. 메인 화면으로 포워딩 (Redirect 아님!)
                RequestDispatcher rd = request.getRequestDispatcher("/main.jsp");
                rd.forward(request, response);
                return; 
            }
            
            int people = 0;
            try {
                people = Integer.parseInt(request.getParameter("people"));
            } catch (NumberFormatException e) { e.printStackTrace(); }
            
            String purpose = request.getParameter("purpose");

            Reservation r = new Reservation(name, studentId, date, timeSlot, people, purpose);
            dao.insert(r);

            response.sendRedirect(request.getContextPath() + "/reserve?action=main&success=true");
        }
        
        // 검색 요청
        else if ("search".equals(action)) {
            String keyword = request.getParameter("keyword");
            List<Reservation> list;
            
            if(keyword == null || keyword.trim().isEmpty()) {
                 list = dao.getAll(); // 검색어 없으면 전체 조회
            } else {
                 list = dao.search(keyword.trim()); // 검색 실행
            }
            
            request.setAttribute("list", list);
            request.setAttribute("searchKeyword", keyword); // 검색어 유지용
            RequestDispatcher rd = request.getRequestDispatcher("/main.jsp");
            rd.forward(request, response);
        }

        // 삭제 요청 
        else if ("delete".equals(action)) {
            String[] deleteIds = request.getParameterValues("deleteIds"); // 체크박스 값들
            if (deleteIds != null) {
                dao.delete(deleteIds);
            }
            // 삭제 후 목록으로 리다이렉트
            response.sendRedirect(request.getContextPath() + "/reserve?action=main");
        }
    }
}