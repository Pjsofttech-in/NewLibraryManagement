package com.pjsofttech.library.dto.response;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class BookResponse {
    private Long id;
    private String title;
    private String isbn;
    private String description;
    private Integer publicationYear;
    private String language;
    private BigDecimal price;
    private PublisherResponse publisher;
    private Integer totalCopies;
    private Integer availableCopies;
    private CategoryResponse category;
    private Set<AuthorResponse> authors;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}