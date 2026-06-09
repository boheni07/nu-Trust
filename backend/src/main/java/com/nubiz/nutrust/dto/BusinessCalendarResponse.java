package com.nubiz.nutrust.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @AllArgsConstructor @NoArgsConstructor @Builder
public class BusinessCalendarResponse {

    private Long id;
    private Long companyId;
    private String calendarName;
    private String colorCode;
    private String startTime;
    private String endTime;
    private String breakStart;
    private String breakEnd;
    private String workingDays;
    private Boolean isDefault;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
