package com.library.service;

import com.library.dto.Dtos.IssueDto;
import com.library.exception.ApiException;
import com.library.model.*;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssuedBookRepository issues;
    private final BookRepository books;
    private final UserRepository users;
    private final FineRepository fines;

    @Value("${library.loan-days}") private int loanDays;
    @Value("${library.fine-per-day}") private int finePerDay;

    /** Issue a book: checks rules, reduces available copies, sets the due date. */
    @Transactional
    public IssueDto issue(Long bookId, Long studentId) {
        Book book = books.findById(bookId).orElseThrow(() -> new ApiException(404, "Book not found"));
        User student = users.findById(studentId).filter(u -> u.getRole() == Role.STUDENT)
                .orElseThrow(() -> new ApiException(404, "Student not found"));

        if (book.getAvailableCopies() <= 0)
            throw new ApiException(409, "No copies available - all issued");
        if (issues.existsByStudentIdAndBookIdAndStatus(studentId, bookId, IssueStatus.ISSUED))
            throw new ApiException(409, "Student already has this book");
        if (fines.existsByStudentIdAndPaidFalse(studentId))
            throw new ApiException(409, "Student has unpaid fines - clear them first");

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        books.save(book);

        IssuedBook i = new IssuedBook();
        i.setBook(book);
        i.setStudent(student);
        i.setIssueDate(LocalDate.now());
        i.setDueDate(LocalDate.now().plusDays(loanDays));
        i.setStatus(IssueStatus.ISSUED);
        return IssueDto.from(issues.save(i), finePerDay);
    }

    /** Return a book: increases copies and creates a fine if it is late. */
    @Transactional
    public IssueDto returnBook(Long issueId) {
        IssuedBook i = issues.findById(issueId).orElseThrow(() -> new ApiException(404, "Issue record not found"));
        if (i.getStatus() == IssueStatus.RETURNED) throw new ApiException(409, "Book already returned");

        LocalDate today = LocalDate.now();
        i.setReturnDate(today);
        i.setStatus(IssueStatus.RETURNED);

        Book book = i.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        books.save(book);

        long lateDays = Math.max(0, ChronoUnit.DAYS.between(i.getDueDate(), today));
        if (lateDays > 0) {
            Fine f = new Fine();
            f.setIssuedBook(i);
            f.setStudent(i.getStudent());
            f.setAmount((int) (lateDays * finePerDay));
            f.setFineDate(today);
            f.setPaid(false);
            fines.save(f);
        }
        return IssueDto.from(issues.save(i), finePerDay);
    }

    public List<IssueDto> all() {
        return issues.findAllByOrderByIssueDateDescIdDesc().stream().map(this::dto).toList();
    }

    public List<IssueDto> overdue() {
        return issues.findByStatusAndDueDateBefore(IssueStatus.ISSUED, LocalDate.now())
                .stream().map(this::dto).toList();
    }

    public List<IssueDto> current(Long studentId) {
        return issues.findByStudentIdAndStatus(studentId, IssueStatus.ISSUED).stream().map(this::dto).toList();
    }

    public List<IssueDto> history(Long studentId) {
        return issues.findByStudentIdOrderByIssueDateDescIdDesc(studentId).stream().map(this::dto).toList();
    }

    /** Numbers for the admin overview cards. */
    public Map<String, Long> stats() {
        long total = books.sumTotalCopies();
        long available = books.sumAvailableCopies();
        return Map.of(
                "bookTitles", books.count(),
                "totalCopies", total,
                "availableCopies", available,
                "issuedCopies", total - available,
                "students", users.countByRole(Role.STUDENT),
                "overdueBooks", issues.countByStatusAndDueDateBefore(IssueStatus.ISSUED, LocalDate.now()),
                "pendingFines", fines.pendingTotal());
    }

    private IssueDto dto(IssuedBook i) {
        return IssueDto.from(i, finePerDay);
    }
}
