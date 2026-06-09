package com.nubiz.nutrust.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "business_calendars", indexes = {
    @Index(name = "idx_business_calendars_company", columnList = "company_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BusinessCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(nullable = false, length = 255)
    private String calendarName;

    @Column(length = 7)
    private String colorCode;

    @Column(name = "start_time", nullable = false)
    @Builder.Default
    private LocalTime startTime = LocalTime.of(9, 0);

    @Column(name = "end_time", nullable = false)
    @Builder.Default
    private LocalTime endTime = LocalTime.of(18, 0);

    @Column(name = "break_start")
    @Builder.Default
    private LocalTime breakStart = LocalTime.of(12, 0);

    @Column(name = "break_end")
    @Builder.Default
    private LocalTime breakEnd = LocalTime.of(13, 0);

    @Column(name = "working_days", nullable = false, length = 50)
    @Builder.Default
    private String workingDays = "MON,TUE,WED,THU,FRI";

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
