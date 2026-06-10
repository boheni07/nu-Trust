package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.HolidayCreateRequest;
import com.nubiz.nutrust.dto.HolidayResponse;
import com.nubiz.nutrust.dto.HolidayUpdateRequest;
import com.nubiz.nutrust.entity.Holiday;
import com.nubiz.nutrust.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HolidayService {

	private final HolidayRepository holidayRepository;

	@Transactional
	public HolidayResponse create(Long companyId, HolidayCreateRequest request) {
		Holiday holiday = Holiday.builder()
			.companyId(companyId)
			.holidayDate(request.getHolidayDate())
			.holidayName(request.getHolidayName())
			.holidayType(request.getHolidayType())
			.build();

		Holiday saved = holidayRepository.save(holiday);
		return toResponse(saved);
	}

	@Transactional
	public HolidayResponse update(Long companyId, Long id, HolidayUpdateRequest request) {
		Holiday holiday = holidayRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Holiday not found: " + id));

		if (request.getHolidayDate() != null) {
			holiday.setHolidayDate(request.getHolidayDate());
		}
		if (request.getHolidayName() != null) {
			holiday.setHolidayName(request.getHolidayName());
		}
		if (request.getHolidayType() != null) {
			holiday.setHolidayType(request.getHolidayType());
		}

		Holiday saved = holidayRepository.save(holiday);
		return toResponse(saved);
	}

	@Transactional
	public void delete(Long companyId, Long id) {
		Holiday holiday = holidayRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Holiday not found: " + id));

		holiday.setDeletedAt(LocalDateTime.now());
		holidayRepository.save(holiday);
	}

	public List<HolidayResponse> findAll(Long companyId) {
		return holidayRepository.findAllActiveByCompanyId(companyId).stream()
			.map(this::toResponse)
			.toList();
	}

	public HolidayResponse findById(Long companyId, Long id) {
		Holiday holiday = holidayRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Holiday not found: " + id));
		return toResponse(holiday);
	}

	public List<HolidayResponse> findByDateRange(Long companyId, String startDate, String endDate) {
		List<Holiday> holidays = holidayRepository.findByDateRange(
			companyId,
			java.time.LocalDate.parse(startDate),
			java.time.LocalDate.parse(endDate)
		);
		return holidays.stream().map(this::toResponse).toList();
	}

	private HolidayResponse toResponse(Holiday holiday) {
		return HolidayResponse.builder()
			.id(holiday.getId())
			.companyId(holiday.getCompanyId())
			.holidayDate(holiday.getHolidayDate())
			.holidayName(holiday.getHolidayName())
			.holidayType(holiday.getHolidayType())
			.createdAt(holiday.getCreatedAt())
			.updatedAt(holiday.getUpdatedAt())
			.build();
	}
}
