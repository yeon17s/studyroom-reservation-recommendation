package com.example.studyroom_reservation_recommendation.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@Table(  // 매핑할 DB 테이블 이름, 날짜와 시간대 조합은 무조건 유일해야 한다고 DB에 선언
        name = "reservation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_date_time_slot",
                        columnNames = {"date", "time_slot"}
                )
        }
)
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
}