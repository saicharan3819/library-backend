package com.library.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "fines")
@Getter @Setter @NoArgsConstructor
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private IssuedBook issuedBook;

    @ManyToOne(optional = false)
    private User student;

    private int amount;
    private LocalDate fineDate;
    private boolean paid;
    private LocalDate paymentDate;
}
