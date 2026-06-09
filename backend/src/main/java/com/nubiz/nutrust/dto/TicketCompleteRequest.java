package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotNull;

public record TicketCompleteRequest(
    @NotNull
    Boolean approve
) {}
