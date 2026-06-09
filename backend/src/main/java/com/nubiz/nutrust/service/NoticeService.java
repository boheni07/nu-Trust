package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.NoticeCreateRequest;
import com.nubiz.nutrust.dto.NoticeResponse;
import com.nubiz.nutrust.dto.NoticeUpdateRequest;
import com.nubiz.nutrust.entity.Notice;
import com.nubiz.nutrust.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

	private final NoticeRepository noticeRepository;

	public NoticeResponse create(Long companyId, Long authorId, NoticeCreateRequest request) {
		Notice notice = Notice.builder()
			.companyId(companyId)
			.authorId(authorId)
			.title(request.getTitle())
			.content(request.getContent())
			.isPinned(request.getIsPinned() != null ? request.getIsPinned() : false)
			.status("ACTIVE")
			.build();

		Notice saved = noticeRepository.save(notice);
		return toResponse(saved);
	}

	public NoticeResponse update(Long companyId, Long id, NoticeUpdateRequest request) {
		Notice notice = noticeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Notice not found: " + id));

		if (request.getTitle() != null) {
			notice.setTitle(request.getTitle());
		}
		if (request.getContent() != null) {
			notice.setContent(request.getContent());
		}
		if (request.getIsPinned() != null) {
			notice.setIsPinned(request.getIsPinned());
		}

		Notice saved = noticeRepository.save(notice);
		return toResponse(saved);
	}

	public void delete(Long companyId, Long id) {
		Notice notice = noticeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Notice not found: " + id));

		notice.setDeletedAt(LocalDateTime.now());
		notice.setStatus("INACTIVE");
		noticeRepository.save(notice);
	}

	public List<NoticeResponse> findAll(Long companyId) {
		return noticeRepository.findAllByCompanyId(companyId).stream()
			.map(this::toResponse)
			.toList();
	}

	public NoticeResponse findById(Long companyId, Long id) {
		Notice notice = noticeRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Notice not found: " + id));
		return toResponse(notice);
	}

	private NoticeResponse toResponse(Notice notice) {
		return NoticeResponse.builder()
			.id(notice.getId())
			.companyId(notice.getCompanyId())
			.title(notice.getTitle())
			.content(notice.getContent())
			.isPinned(notice.getIsPinned())
			.status(notice.getStatus())
			.authorId(notice.getAuthorId())
			.createdAt(notice.getCreatedAt())
			.updatedAt(notice.getUpdatedAt())
			.build();
	}
}
