package com.ausganslage.ausgangslageBackend.repository;

import com.ausganslage.ausgangslageBackend.model.Estimate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EstimateRepository extends JpaRepository<Estimate, Long> {
    List<Estimate> findByExamId(Long examId);
    List<Estimate> findByPersonId(Long personId);
    List<Estimate> findByExamIdAndPersonId(Long examId, Long personId);
}
