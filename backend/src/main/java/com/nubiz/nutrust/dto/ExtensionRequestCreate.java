package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ExtensionRequestCreate(
    @NotNull Long userId,
    @NotNull @Size(max = 500) String reason,
    @NotNull Integer requestedExtendDays
) {}
