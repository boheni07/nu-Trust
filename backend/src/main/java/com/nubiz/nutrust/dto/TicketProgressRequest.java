package com.nubiz.nutrust.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TicketProgressRequest(
    @Min(0) @Max(100)
    Integer progress,

    String updatedAt
) {
}
