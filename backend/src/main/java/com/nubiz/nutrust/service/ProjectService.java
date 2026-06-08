package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.*;
import com.nubiz.nutrust.entity.*;
import com.nubiz.nutrust.repository.CompanyRepository;
import com.nubiz.nutrust.repository.ProjectManagerRepository;
import com.nubiz.nutrust.repository.ProjectRepository;
import com.nubiz.nutrust.repository.ProjectSupportManagerRepository;
import com.nubiz.nutrust.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectManagerRepository projectManagerRepository;
    private final ProjectSupportManagerRepository projectSupportManagerRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_ADMIN')")
    @Transactional
    public ProjectResponse create(ProjectCreateRequest request) {
        companyRepository.findById(request.getCompanyId())
            .orElseThrow(() -> new IllegalArgumentException("Company not found: " + request.getCompanyId()));

        userRepository.findById(request.getOwnerId())
            .orElseThrow(() -> new IllegalArgumentException("Owner not found: " + request.getOwnerId()));

        if (request.getCustomerCompanyId() != null) {
            companyRepository.findById(request.getCustomerCompanyId())
                .orElseThrow(() -> new IllegalArgumentException("Customer company not found: " + request.getCustomerCompanyId()));
        }

        Project project = Project.builder()
            .companyId(request.getCompanyId())
            .customerCompanyId(request.getCustomerCompanyId())
            .name(request.getName())
            .ownerId(request.getOwnerId())
            .contractDate(request.getContractDate())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .status("ACTIVE")
            .description(request.getDescription())
            .build();

        return toResponse(projectRepository.save(project));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_ADMIN')")
    @Transactional(readOnly = true)
    public List<ProjectResponse> list() {
        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getRoles().stream()
            .anyMatch(r -> "ADMIN".equals(r.getName()));

        List<Project> projects;
        if (isAdmin) {
            projects = projectRepository.findByDeletedAtIsNull();
        } else {
            Long companyId = currentUser.getCompanyId();
            projects = projectRepository.findByCompanyIdOrCustomerCompanyId(companyId);
        }

        return projects.stream().map(this::toResponse).toList();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_ADMIN')")
    @Transactional(readOnly = true)
    public ProjectResponse get(Long id) {
        return toResponse(projectRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id)));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_ADMIN')")
    @Transactional
    public ProjectResponse update(Long id, ProjectCreateRequest request) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));

        project.setName(request.getName());
        if (request.getContractDate() != null) project.setContractDate(request.getContractDate());
        if (request.getStartDate() != null) project.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) project.setEndDate(request.getEndDate());
        if (request.getDescription() != null) project.setDescription(request.getDescription());

        projectRepository.save(project);
        return toResponse(project);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_ADMIN')")
    @Transactional
    public void delete(Long id) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
        project.softDelete();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_ADMIN')")
    @Transactional
    public ProjectResponse updateStatus(Long id, String status) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));

        if (!Arrays.asList("ACTIVE", "COMPLETED", "ON_HOLD", "CANCELLED").contains(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        project.setStatus(status);
        return toResponse(projectRepository.save(project));
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_ADMIN')")
    @Transactional
    public ProjectDetailResponse assignManager(Long projectId, Long managerId, String role) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        User manager = userRepository.findById(managerId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + managerId));

        ProjectManagerId pmi = new ProjectManagerId(projectId, managerId);
        ProjectManager existing = projectManagerRepository.findById(pmi).orElse(null);

        if (existing != null) {
            existing.setRole(role != null ? role : "PROJECT_ADMIN");
            projectManagerRepository.save(existing);
        } else {
            User currentUser = getCurrentUser();
            ProjectManager pm = ProjectManager.builder()
                .project(project)
                .manager(manager)
                .role(role != null ? role : "PROJECT_ADMIN")
                .assignedBy(currentUser)
                .build();
            projectManagerRepository.save(pm);
        }

        return buildProjectDetailResponse(project);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_ADMIN')")
    @Transactional
    public void removeManager(Long projectId, Long managerId) {
        ProjectManagerId id = new ProjectManagerId(projectId, managerId);
        if (!projectManagerRepository.existsById(id)) {
            throw new IllegalArgumentException("Manager assignment not found");
        }
        projectManagerRepository.deleteById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPANY_ADMIN')")
    @Transactional(readOnly = true)
    public ProjectDetailResponse getDetail(Long id) {
        Project project = projectRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));

        return buildProjectDetailResponse(project);
    }

    private ProjectDetailResponse buildProjectDetailResponse(Project project) {
        Company company = companyRepository.findById(project.getCompanyId()).orElse(null);
        Company customerCompany = project.getCustomerCompanyId() != null
            ? companyRepository.findById(project.getCustomerCompanyId()).orElse(null)
            : null;
        User owner = project.getOwnerId() != null
            ? userRepository.findById(project.getOwnerId()).orElse(null)
            : null;

        ProjectDetailResponse base = ProjectDetailResponse.from(project, company, customerCompany, owner);
        List<ProjectManager> managers = projectManagerRepository.findAll();
        List<ProjectDetailResponse.ManagerResponse> managerResponses = managers.stream()
            .filter(m -> m.getProject().getId().equals(project.getId()))
            .map(m -> {
                User mgr = m.getManager();
                return ProjectDetailResponse.ManagerResponse.builder()
                    .managerId(mgr.getId())
                    .name(mgr.getName())
                    .email(mgr.getEmail())
                    .role(m.getRole())
                    .build();
            })
            .toList();

        return ProjectDetailResponse.builder()
            .id(base.getId())
            .name(base.getName())
            .company(base.getCompany())
            .companyCode(base.getCompanyCode())
            .customerCompany(base.getCustomerCompany())
            .customerCompanyCode(base.getCustomerCompanyCode())
            .owner(base.getOwner())
            .description(base.getDescription())
            .startDate(base.getStartDate())
            .endDate(base.getEndDate())
            .contractDate(base.getContractDate())
            .status(base.getStatus())
            .statusLabel(base.getStatusLabel())
            .managers(managerResponses)
            .supports(new ArrayList<>())
            .build();
    }

    private ProjectResponse toResponse(Project project) {
        Company company = companyRepository.findById(project.getCompanyId()).orElse(null);
        User owner = project.getOwnerId() != null
            ? userRepository.findById(project.getOwnerId()).orElse(null)
            : null;
        String ownerName = owner != null ? owner.getName() : null;

        return ProjectResponse.from(project, company, ownerName);
    }
}
