package com.ausganslage.ausgangslageBackend.controller;

import com.ausganslage.ausgangslageBackend.model.Exam;
import com.ausganslage.ausgangslageBackend.model.Result;
import com.ausganslage.ausgangslageBackend.model.Estimate;
import com.ausganslage.ausgangslageBackend.repository.ExamRepository;
import com.ausganslage.ausgangslageBackend.repository.ResultRepository;
import com.ausganslage.ausgangslageBackend.repository.EstimateRepository;
import com.ausganslage.ausgangslageBackend.repository.PersonRepository;
import com.ausganslage.ausgangslageBackend.exception.ResourceNotFoundException;
import com.ausganslage.ausgangslageBackend.exception.InvalidOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
    public ResponseEntity<Exam> getExam(@PathVariable Long id) throws ResourceNotFoundException {
        return examRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", id));
    }

    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        Exam saved = examRepository.save(exam);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exam> updateExam(@PathVariable Long id, @RequestBody Exam updatedExam) 
            throws ResourceNotFoundException {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", id));
        
        exam.setTitle(updatedExam.getTitle());
        exam.setDate(updatedExam.getDate());
        
        return ResponseEntity.ok(examRepository.save(exam));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id) throws ResourceNotFoundException {
        if (!examRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exam", id);
        }
        examRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Results endpoints
    @PostMapping("/{examId}/results")
    public ResponseEntity<Result> addResult(@PathVariable Long examId, @RequestBody Result result) 
            throws ResourceNotFoundException {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", examId));
        
        result.setExam(exam);
        Result saved = resultRepository.save(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
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
    public ResponseEntity<?> addEstimate(@PathVariable Long examId, @RequestBody EstimateRequest req) 
            throws ResourceNotFoundException, InvalidOperationException {
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", examId));
        
        var maybePerson = personRepository.findById(req.personId);
        if (maybePerson.isEmpty()) {
            throw new ResourceNotFoundException("Person", req.personId);
        }

        // Zähle existierende Schätzungen für diese Person und dieses Exam
        var existing = estimateRepository.findByExamIdAndPersonId(examId, req.personId);

        LocalDate examDate;
        try {
            examDate = LocalDate.parse(exam.getDate());
        } catch (DateTimeParseException e) {
            throw new InvalidOperationException("Ungültiges Datumsformat bei Exam", e);
        }
        
        LocalDate today = LocalDate.now();

        try {
            if (existing.size() == 0) {
                // Erste Schätzung ist nur vor dem Exam-Datum erlaubt
                if (!today.isBefore(examDate)) {
                    throw new InvalidOperationException(
                        "Erste Schätzung ist nur vor dem Exam-Datum (" + exam.getDate() + ") erlaubt");
                }
            } else if (existing.size() == 1) {
                // Zweite Schätzung ist nur nach dem Exam-Datum erlaubt
                if (!today.isAfter(examDate)) {
                    throw new InvalidOperationException(
                        "Zweite Schätzung ist nur nach dem Exam-Datum (" + exam.getDate() + ") erlaubt");
                }
            } else {
                throw new InvalidOperationException(
                    "Es wurden bereits zwei Schätzungen für dieses Exam abgegeben");
            }

            Estimate estimate = new Estimate();
            estimate.setExam(exam);
            estimate.setPerson(maybePerson.get());
            estimate.setValue(req.value);
            
            Estimate saved = estimateRepository.save(estimate);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
            
        } finally {
            // Cleanup/Logging für mögliche Fehlerbehandlung
            // z.B. Audit-Log für gescheiterte Versuche
        }
    }

    @GetMapping("/{examId}/estimates")
    public List<Estimate> getEstimatesByExam(@PathVariable Long examId) {
        return estimateRepository.findByExamId(examId);
    }
}
