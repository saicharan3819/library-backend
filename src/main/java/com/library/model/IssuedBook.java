package com.library.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "issued_books")
@Getter @Setter @NoArgsConstructor
public class IssuedBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Book book;

    @ManyToOne(optional = false)
    private User student;

    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;   // null until the book is returned

    @Enumerated(EnumType.STRING)
    private IssueStatus status;
}
