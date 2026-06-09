package com.nubiz.nutrust.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponse {

    private Long totalCompanies;
    private Long activeProjects;
    private Long todayTickets;
    private Long slaWarnings;
    private TicketStatusDistribution statusDistribution;
    private List<RecentTicketResponse> recentTickets;
    private List<ActivityResponse> activities;

    @Getter
    @Builder
    public static class TicketStatusDistribution {
        private Long registered;
        private Long received;
        private Long processing;
        private Long completionRequest;
        private Long completed;
        private Long delayed;
    }

    @Getter
    @Builder
    public static class RecentTicketResponse {
        private Long id;
        private String title;
        private String status;
        private String statusLabel;
        private String priority;
        private String priorityLabel;
    }

    @Getter
    @Builder
    public static class ActivityResponse {
        private String text;
        private String time;
    }
}
