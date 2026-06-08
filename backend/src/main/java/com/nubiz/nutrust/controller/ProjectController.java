package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.*;
import com.nubiz.nutrust.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody ProjectCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(projectService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> list() {
        return ResponseEntity.ok(projectService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody ProjectCreateRequest request
    ) {
        return ResponseEntity.ok(projectService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectResponse> updateStatus(
        @PathVariable Long id,
        @RequestParam String status
    ) {
        return ResponseEntity.ok(projectService.updateStatus(id, status));
    }

    @PostMapping("/{projectId}/managers")
    public ResponseEntity<ProjectDetailResponse> assignManager(
        @PathVariable Long projectId,
        @Valid @RequestBody ProjectManagerAssignRequest request
    ) {
        return ResponseEntity.ok(projectService.assignManager(projectId, request.getManagerId(), request.getRole()));
    }

    @DeleteMapping("/{projectId}/managers/{managerId}")
    public ResponseEntity<Void> removeManager(
        @PathVariable Long projectId,
        @PathVariable Long managerId
    ) {
        projectService.removeManager(projectId, managerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ProjectDetailResponse> getDetail(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getDetail(id));
    }
}
