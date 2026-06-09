package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.DashboardResponse;
import com.nubiz.nutrust.dto.DashboardResponse.RecentTicketResponse;
import com.nubiz.nutrust.dto.DashboardResponse.ActivityResponse;
import com.nubiz.nutrust.dto.DashboardResponse.TicketStatusDistribution;
import com.nubiz.nutrust.entity.Ticket;
import com.nubiz.nutrust.repository.CompanyRepository;
import com.nubiz.nutrust.repository.ProjectRepository;
import com.nubiz.nutrust.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

	private final CompanyRepository companyRepository;
	private final ProjectRepository projectRepository;
	private final TicketRepository ticketRepository;

	private static final String[] STATUS_LABELS = {
		"REGISTERED", "RECEIVED", "PROCESSING", "COMPLETION_REQUEST", "COMPLETED", "DELAYED"
	};

	private static final String[] STATUS_NAMES = {
		"등록", "수신", "처리중", "완료요청", "완료", "지연"
	};

	private static final String[] PRIORITY_LABELS = {
		"LOW", "MEDIUM", "HIGH", "URGENT"
	};

	private static final String[] PRIORITY_NAMES = {
		"하", "중", "상", "긴급"
	};

	public DashboardResponse getDashboardData() {
		Long totalCompanies = companyRepository.count();

		Long activeProjects = projectRepository.countByStatus("ACTIVE");

		LocalDate today = LocalDate.now();
		Long todayTickets = ticketRepository.countByCreatedDate(today);

		Long slaWarnings = ticketRepository.countSlaWarnings();

		String registered = "0", received = "0", processing = "0",
			completionRequest = "0", completed = "0", delayed = "0";

		for (Object[] row : ticketRepository.aggregateStatusCount()) {
			String status = (String) row[0];
			long count = ((Number) row[1]).longValue();
			switch (status) {
				case "REGISTERED" -> registered = String.valueOf(count);
				case "RECEIVED" -> received = String.valueOf(count);
				case "PROCESSING" -> processing = String.valueOf(count);
				case "COMPLETION_REQUEST" -> completionRequest = String.valueOf(count);
				case "COMPLETED" -> completed = String.valueOf(count);
				case "DELAYED" -> delayed = String.valueOf(count);
			}
		}

		TicketStatusDistribution statusDistribution = TicketStatusDistribution.builder()
			.registered(Long.parseLong(registered))
			.received(Long.parseLong(received))
			.processing(Long.parseLong(processing))
			.completionRequest(Long.parseLong(completionRequest))
			.completed(Long.parseLong(completed))
			.delayed(Long.parseLong(delayed))
			.build();

		List<RecentTicketResponse> recentTickets = ticketRepository.findRecentTickets(5).stream()
			.map(this::toRecentTicketResponse)
			.toList();

		List<ActivityResponse> activities = buildActivities(recentTickets);

		return DashboardResponse.builder()
			.totalCompanies(totalCompanies)
			.activeProjects(activeProjects)
			.todayTickets(todayTickets)
			.slaWarnings(slaWarnings)
			.statusDistribution(statusDistribution)
			.recentTickets(recentTickets)
			.activities(activities)
			.build();
	}

	private RecentTicketResponse toRecentTicketResponse(Ticket ticket) {
		return RecentTicketResponse.builder()
			.id(ticket.getId())
			.title(ticket.getTitle())
			.status(ticket.getStatus())
			.statusLabel(mapStatusLabel(ticket.getStatus()))
			.priority(ticket.getPriority())
			.priorityLabel(mapPriorityLabel(ticket.getPriority()))
			.build();
	}

	private String mapStatusLabel(String status) {
		for (int i = 0; i < STATUS_LABELS.length; i++) {
			if (STATUS_LABELS[i].equals(status)) {
				return STATUS_NAMES[i];
			}
		}
		return status;
	}

	private String mapPriorityLabel(String priority) {
		for (int i = 0; i < PRIORITY_LABELS.length; i++) {
			if (PRIORITY_LABELS[i].equals(priority)) {
				return PRIORITY_NAMES[i] + "우선";
			}
		}
		return priority;
	}

	private List<ActivityResponse> buildActivities(List<RecentTicketResponse> recentTickets) {
		return recentTickets.stream()
			.map(ticket -> {
				String text = "티켓 #" + ticket.getId() + " " + ticket.getStatusLabel() + "으로 변경됨";
				return ActivityResponse.builder()
					.text(text)
					.time("방금 전")
					.build();
			})
			.toList();
	}
}
