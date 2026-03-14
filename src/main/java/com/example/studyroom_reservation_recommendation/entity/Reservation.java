package com.example.studyroom_reservation_recommendation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(name = "reservation") // 매핑할 DB 테이블 이름
@Getter
@Setter
@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor // 모든 필드를 포함하는 생성자 자동 생성
@Builder // 객체 생성을 직관적으로 도와주는 빌더 패턴 적용
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "student_id", nullable = false,  length = 20)
    private String studentId;

    @Column(nullable = false)
    private String date;

    @Column(name = "time_slot", nullable = false,  length = 20)
    private String timeSlot;

    @Min(2) @Max(8)
    @Column(nullable = false)
    private int people;

    @Column(columnDefinition = "TEXT")
    private String purpose;

    public Reservation(String name, String studentId, String date, String timeSlot, int people, String purpose) {
        this.name = name;
        this.studentId = studentId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.people = people;
        this.purpose = purpose;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public int getPeople() { return people; }
    public void setPeople(int people) { this.people = people; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
}