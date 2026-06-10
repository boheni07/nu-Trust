package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.BusinessCalendarCreateRequest;
import com.nubiz.nutrust.dto.BusinessCalendarResponse;
import com.nubiz.nutrust.dto.BusinessCalendarUpdateRequest;
import com.nubiz.nutrust.entity.BusinessCalendar;
import com.nubiz.nutrust.repository.BusinessCalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessCalendarService {

	private final BusinessCalendarRepository businessCalendarRepository;

	@Transactional
	public BusinessCalendarResponse create(Long companyId, BusinessCalendarCreateRequest request) {
		BusinessCalendar calendar = BusinessCalendar.builder()
			.companyId(companyId)
			.calendarName(request.getCalendarName())
			.colorCode(request.getColorCode())
			.startTime(request.getStartTime())
			.endTime(request.getEndTime())
			.breakStart(request.getBreakStart())
			.breakEnd(request.getBreakEnd())
			.workingDays(request.getWorkingDays())
			.isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
			.status("ACTIVE")
			.build();

		BusinessCalendar saved = businessCalendarRepository.save(calendar);
		return toResponse(saved);
	}

	@Transactional
	public BusinessCalendarResponse update(Long companyId, Long id, BusinessCalendarUpdateRequest request) {
		BusinessCalendar calendar = businessCalendarRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("BusinessCalendar not found: " + id));

		if (request.getCalendarName() != null) {
			calendar.setCalendarName(request.getCalendarName());
		}
		if (request.getColorCode() != null) {
			calendar.setColorCode(request.getColorCode());
		}
		if (request.getStartTime() != null) {
			calendar.setStartTime(request.getStartTime());
		}
		if (request.getEndTime() != null) {
			calendar.setEndTime(request.getEndTime());
		}
		if (request.getBreakStart() != null) {
			calendar.setBreakStart(request.getBreakStart());
		}
		if (request.getBreakEnd() != null) {
			calendar.setBreakEnd(request.getBreakEnd());
		}
		if (request.getWorkingDays() != null) {
			calendar.setWorkingDays(request.getWorkingDays());
		}
		if (request.getIsDefault() != null) {
			calendar.setIsDefault(request.getIsDefault());
		}

		BusinessCalendar saved = businessCalendarRepository.save(calendar);
		return toResponse(saved);
	}

	@Transactional
	public void delete(Long companyId, Long id) {
		BusinessCalendar calendar = businessCalendarRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("BusinessCalendar not found: " + id));

		calendar.setDeletedAt(LocalDateTime.now());
		calendar.setStatus("INACTIVE");
		businessCalendarRepository.save(calendar);
	}

	public List<BusinessCalendarResponse> findAll(Long companyId) {
		return businessCalendarRepository.findAllActiveByCompanyId(companyId).stream()
			.map(this::toResponse)
			.toList();
	}

	public BusinessCalendarResponse findById(Long companyId, Long id) {
		BusinessCalendar calendar = businessCalendarRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("BusinessCalendar not found: " + id));
		return toResponse(calendar);
	}

	public BusinessCalendarResponse findDefault(Long companyId) {
		return businessCalendarRepository.findDefaultByCompanyId(companyId)
			.map(this::toResponse)
			.orElse(null);
	}

	private BusinessCalendarResponse toResponse(BusinessCalendar calendar) {
		return BusinessCalendarResponse.builder()
			.id(calendar.getId())
			.companyId(calendar.getCompanyId())
			.calendarName(calendar.getCalendarName())
			.colorCode(calendar.getColorCode())
			.startTime(calendar.getStartTime() != null ? calendar.getStartTime().toString() : null)
			.endTime(calendar.getEndTime() != null ? calendar.getEndTime().toString() : null)
			.breakStart(calendar.getBreakStart() != null ? calendar.getBreakStart().toString() : null)
			.breakEnd(calendar.getBreakEnd() != null ? calendar.getBreakEnd().toString() : null)
			.workingDays(calendar.getWorkingDays())
			.isDefault(calendar.getIsDefault())
			.status(calendar.getStatus())
			.createdAt(calendar.getCreatedAt())
			.updatedAt(calendar.getUpdatedAt())
			.build();
	}
}
