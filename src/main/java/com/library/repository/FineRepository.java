package com.library.repository;

import com.library.model.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FineRepository extends JpaRepository<Fine, Long> {

    List<Fine> findAllByOrderByFineDateDescIdDesc();

    List<Fine> findByStudentIdOrderByFineDateDescIdDesc(Long studentId);

    boolean existsByStudentIdAndPaidFalse(Long studentId);

    @Query("select coalesce(sum(f.amount), 0) from Fine f where f.paid = false")
    long pendingTotal();
}
