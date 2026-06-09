package com.nubiz.nutrust.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @AllArgsConstructor @NoArgsConstructor @Builder
public class HolidayResponse {

    private Long id;
    private Long companyId;
    private LocalDate holidayDate;
    private String holidayName;
    private String holidayType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
