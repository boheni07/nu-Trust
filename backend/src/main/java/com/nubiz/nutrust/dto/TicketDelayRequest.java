package com.nubiz.nutrust.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TicketDelayRequest(
    String reason
) {}
