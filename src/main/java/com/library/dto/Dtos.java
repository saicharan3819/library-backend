package com.library.dto;

import com.library.model.Fine;
import com.library.model.IssuedBook;
import com.library.model.IssueStatus;
import com.library.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** All request/response objects in one place (Java records = tiny immutable classes). */
public class Dtos {

    // ---------- requests ----------
    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    public record RegisterRequest(@NotBlank String name,
                                  @NotBlank @Email String email,
                                  @NotBlank @Size(min = 6) String password,
                                  String phone,
                                  String department) {}

    public record StudentUpdateRequest(@NotBlank String name, String phone, String department) {}

    public record IssueRequest(@NotNull Long bookId, @NotNull Long studentId) {}

    // ---------- responses ----------
    public record AuthResponse(String token, User user) {}

    public record IssueDto(Long id, Long bookId, String bookTitle, String isbn,
                           Long studentId, String studentName,
                           LocalDate issueDate, LocalDate dueDate, LocalDate returnDate,
                           String status, boolean overdue, long daysOverdue, long currentFine) {

        public static IssueDto from(IssuedBook i, int finePerDay) {
            LocalDate end = i.getReturnDate() != null ? i.getReturnDate() : LocalDate.now();
            long late = Math.max(0, ChronoUnit.DAYS.between(i.getDueDate(), end));
            boolean overdue = i.getStatus() == IssueStatus.ISSUED && late > 0;
            return new IssueDto(i.getId(), i.getBook().getId(), i.getBook().getTitle(), i.getBook().getIsbn(),
                    i.getStudent().getId(), i.getStudent().getName(),
                    i.getIssueDate(), i.getDueDate(), i.getReturnDate(),
                    i.getStatus().name(), overdue, late, late * finePerDay);
        }
    }

    public record FineDto(Long id, Long issueId, String bookTitle, Long studentId, String studentName,
                          int amount, LocalDate fineDate, boolean paid, LocalDate paymentDate) {

        public static FineDto from(Fine f) {
            return new FineDto(f.getId(), f.getIssuedBook().getId(), f.getIssuedBook().getBook().getTitle(),
                    f.getStudent().getId(), f.getStudent().getName(),
                    f.getAmount(), f.getFineDate(), f.isPaid(), f.getPaymentDate());
        }
    }
}
