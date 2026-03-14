package com.example.studyroom_reservation_recommendation.entity;

public class Reservation {
    private int id;
    private String name;
    private String studentId;
    private String date;
    private String timeSlot;
    private int people;
    private String purpose;

    public Reservation() {}

    public Reservation(String name, String studentId, String date, String timeSlot, int people, String purpose) {
        this.name = name;
        this.studentId = studentId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.people = people;
        this.purpose = purpose;
    }

    public Reservation(int id, String name, String studentId, String date, String timeSlot, int people, String purpose) {
        this.id = id;
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