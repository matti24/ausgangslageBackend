package com.ausganslage.ausgangslageBackend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Estimate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Column(name = "\"value\"")
    private Integer value; // user's estimate (1-6 or similar)

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Exam getExam() { return exam; }
    public void setExam(Exam exam) { this.exam = exam; }

    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }

    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
}
