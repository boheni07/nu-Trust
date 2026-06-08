package com.nubiz.nutrust.dto;

import java.util.Map;

public record TicketCountSummaryResponse(
    Map<String, Long> statusCounts
) {
}
