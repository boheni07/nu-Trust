package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class BusinessCalendarCreateRequest {

    @NotBlank
    private String calendarName;

    private String colorCode;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private LocalTime breakStart;

    private LocalTime breakEnd;

    @NotBlank
    private String workingDays;

    private Boolean isDefault;
}
