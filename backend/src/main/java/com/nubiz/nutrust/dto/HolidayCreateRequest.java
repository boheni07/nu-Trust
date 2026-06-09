package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class HolidayCreateRequest {

    @NotNull
    private LocalDate holidayDate;

    @NotBlank
    private String holidayName;

    private String holidayType;
}
