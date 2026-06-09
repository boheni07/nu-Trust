package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.DashboardResponse;
import com.nubiz.nutrust.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping
	public ResponseEntity<DashboardResponse> getDashboard() {
		return ResponseEntity.ok(dashboardService.getDashboardData());
	}
}
