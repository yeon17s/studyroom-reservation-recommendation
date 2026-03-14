package com.example.studyroom_reservation_recommendation.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.example.studyroom_reservation_recommendation.entity.Reservation;
import studyroom.util.DBUtil;

public class ReservationDAO {

    // 예약 등록 
    public int insert(Reservation r) {
        String sql = "INSERT INTO reservation (name, student_id, date, time_slot, people, purpose) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, r.getName());
            pstmt.setString(2, r.getStudentId());
            pstmt.setString(3, r.getDate());
            pstmt.setString(4, r.getTimeSlot());
            pstmt.setInt(5, r.getPeople());
            pstmt.setString(6, r.getPurpose());
            
            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // 중복 예약 확인
    public boolean isBooked(String date, String timeSlot) {
        boolean isBooked = false;
        String sql = "SELECT count(*) FROM reservation WHERE date = ? AND time_slot = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, date);
            pstmt.setString(2, timeSlot);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // count(*) 값이 1 이상이면 이미 예약된 상태
                    if (rs.getInt(1) > 0) {
                        isBooked = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isBooked;
    }

    // 목록 조회
    public List<Reservation> getAll() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation ORDER BY date ASC, time_slot ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setName(rs.getString("name"));
                r.setStudentId(rs.getString("student_id"));
                r.setDate(rs.getString("date"));
                r.setTimeSlot(rs.getString("time_slot"));
                r.setPeople(rs.getInt("people"));
                r.setPurpose(rs.getString("purpose"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // 검색
    public List<Reservation> search(String keyword) {
        List<Reservation> list = new ArrayList<>();
        // 이름이나 학번에 키워드가 포함된 경우 조회
        String sql = "SELECT * FROM reservation WHERE name LIKE ? OR student_id LIKE ? ORDER BY date ASC, time_slot ASC";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("id"));
                    r.setName(rs.getString("name"));
                    r.setStudentId(rs.getString("student_id"));
                    r.setDate(rs.getString("date"));
                    r.setTimeSlot(rs.getString("time_slot"));
                    r.setPeople(rs.getInt("people"));
                    r.setPurpose(rs.getString("purpose"));
                    list.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // 선택 삭제
    public void delete(String[] ids) {
        if (ids == null || ids.length == 0) return;

        // "DELETE FROM reservation WHERE id IN (1, 3, 5)" 형태로 변환
        StringBuilder sql = new StringBuilder("DELETE FROM reservation WHERE id IN (");
        for (int i = 0; i < ids.length; i++) {
            sql.append("?");
            if (i < ids.length - 1) sql.append(", ");
        }
        sql.append(")");

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < ids.length; i++) {
                pstmt.setInt(i + 1, Integer.parseInt(ids[i]));
            }
            pstmt.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}