package com.ausganslage.ausgangslageBackend.repository;

import com.ausganslage.ausgangslageBackend.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByExamId(Long examId);
    List<Result> findByPersonId(Long personId);
    Optional<Result> findByExamIdAndPersonId(Long examId, Long personId);
}
