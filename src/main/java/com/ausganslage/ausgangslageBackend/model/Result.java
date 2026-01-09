package com.ausganslage.ausgangslageBackend.model;

import jakarta.persistence.*;

@Entity
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    private Integer result; // 1-6 (school grades)

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public Integer getResult() { return result; }
    public void setResult(Integer result) { this.result = result; }
}
