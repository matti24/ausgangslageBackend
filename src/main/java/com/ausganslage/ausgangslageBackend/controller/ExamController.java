package com.ausganslage.ausgangslageBackend.controller;

import com.ausganslage.ausgangslageBackend.model.Exam;
import com.ausganslage.ausgangslageBackend.model.Result;
import com.ausganslage.ausgangslageBackend.model.Estimate;
import com.ausganslage.ausgangslageBackend.repository.ExamRepository;
import com.ausganslage.ausgangslageBackend.repository.ResultRepository;
import com.ausganslage.ausgangslageBackend.repository.EstimateRepository;
import com.ausganslage.ausgangslageBackend.repository.PersonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamRepository examRepository;
    private final ResultRepository resultRepository;
    private final EstimateRepository estimateRepository;
    private final PersonRepository personRepository;

    public ExamController(ExamRepository examRepository, ResultRepository resultRepository, EstimateRepository estimateRepository, PersonRepository personRepository) {
        this.examRepository = examRepository;
        this.resultRepository = resultRepository;
        this.estimateRepository = estimateRepository;
        this.personRepository = personRepository;
    }

    @GetMapping
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exam> getExam(@PathVariable Long id) {
        return examRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        Exam saved = examRepository.save(exam);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @RequestBody Exam updatedExam) {
        return examRepository.findById(id)
                .map(exam -> {
                    exam.setTitle(updatedExam.getTitle());
                    exam.setDate(updatedExam.getDate());
                    return ResponseEntity.ok(examRepository.save(exam));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id) {
        if (!examRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        examRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Results endpoints
    @PostMapping("/{examId}/results")
    public ResponseEntity<Result> addResult(@PathVariable Long examId, @RequestBody Result result) {
        return examRepository.findById(examId)
                .map(exam -> {
                    result.setExam(exam);
                    Result saved = resultRepository.save(result);
                    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{examId}/results")
    public List<Result> getResultsByExam(@PathVariable Long examId) {
        return resultRepository.findByExamId(examId);
    }

    // Estimates endpoints
    public static class EstimateRequest {
        public Long personId;
        public Integer value;
    }

    @PostMapping("/{examId}/estimates")
    public ResponseEntity<?> addEstimate(@PathVariable Long examId, @RequestBody EstimateRequest req) {
        return examRepository.findById(examId)
                .map(exam -> {
                    // check person
                    var maybePerson = personRepository.findById(req.personId);
                    if (maybePerson.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Person not found");

                    // count existing estimates by this person for this exam
                    var existing = estimateRepository.findByExamIdAndPersonId(examId, req.personId);

                    java.time.LocalDate examDate;
                    try {
                        examDate = java.time.LocalDate.parse(exam.getDate());
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid exam date format");
                    }
                    java.time.LocalDate today = java.time.LocalDate.now();

                    if (existing.size() == 0) {
                        // first estimate allowed only before exam date
                        if (!today.isBefore(examDate)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("First estimate allowed only before exam date");
                        }
                    } else if (existing.size() == 1) {
                        // second estimate allowed only after exam date
                        if (!today.isAfter(examDate)) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Second estimate allowed only after exam date");
                        }
                    } else {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("Already submitted two estimates");
                    }

                    Estimate estimate = new Estimate();
                    estimate.setExam(exam);
                    estimate.setPerson(maybePerson.get());
                    estimate.setValue(req.value);
                    Estimate saved = estimateRepository.save(estimate);
                    return ResponseEntity.status(HttpStatus.CREATED).body(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{examId}/estimates")
    public List<Estimate> getEstimatesByExam(@PathVariable Long examId) {
        return estimateRepository.findByExamId(examId);
    }
}
