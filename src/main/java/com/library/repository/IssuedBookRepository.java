package com.library.repository;

import com.library.model.IssueStatus;
import com.library.model.IssuedBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IssuedBookRepository extends JpaRepository<IssuedBook, Long> {

    List<IssuedBook> findAllByOrderByIssueDateDescIdDesc();

    List<IssuedBook> findByStatusAndDueDateBefore(IssueStatus status, LocalDate date);

    List<IssuedBook> findByStudentIdAndStatus(Long studentId, IssueStatus status);

    List<IssuedBook> findByStudentIdOrderByIssueDateDescIdDesc(Long studentId);

    boolean existsByBookId(Long bookId);

    boolean existsByStudentId(Long studentId);

    boolean existsByStudentIdAndBookIdAndStatus(Long studentId, Long bookId, IssueStatus status);

    long countByStatus(IssueStatus status);

    long countByStatusAndDueDateBefore(IssueStatus status, LocalDate date);
}
