package com.ausganslage.ausgangslageBackend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String date;

    // One exam can have many results (cascade delete)
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Result> results;

    // One exam can have many estimates (cascade delete)
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Estimate> estimates;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<Result> getResults() { return results; }
    public void setResults(List<Result> results) { this.results = results; }

    public List<Estimate> getEstimates() { return estimates; }
    public void setEstimates(List<Estimate> estimates) { this.estimates = estimates; }
}
