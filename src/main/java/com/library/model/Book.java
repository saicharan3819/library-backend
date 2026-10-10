package com.library.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank private String title;
    @NotBlank private String author;

    @NotBlank
    @Column(unique = true)
    private String isbn;

    private String category;
    private String publisher;
    private String edition;
    private Integer publicationYear;

    @Min(1)
    private int totalCopies;
    private int availableCopies;

    private String shelfNumber;

    // Calculated values - not stored in DB, but included in the JSON
    @JsonProperty("issuedCopies")
    public int getIssuedCopies() {
        return totalCopies - availableCopies;
    }

    @JsonProperty("status")
    public String getStatus() {
        if (availableCopies <= 0) return "ALL_ISSUED";
        if (availableCopies < totalCopies) return "PARTIALLY_AVAILABLE";
        return "AVAILABLE";
    }
}
