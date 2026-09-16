package com.pjsofttech.library.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class RackResponse {
    private Long id;
    private String rackCode;
    private String label;
    private String section;
    private Integer rowNumber;
    private Integer totalCapacity;
    private Long currentBookCount;
    private String description;
    private LocalDateTime createdAt;
}