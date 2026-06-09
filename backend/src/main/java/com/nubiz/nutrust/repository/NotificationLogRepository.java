package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    @Query("SELECT n FROM NotificationLog n WHERE n.targetUserId = :userId AND n.status IN :statuses ORDER BY n.createdAt DESC")
    List<NotificationLog> findByTargetUserIdAndStatusInOrderByCreatedAtDesc(
        @Param("userId") Long userId,
        @Param("statuses") List<String> statuses
    );

    Page<NotificationLog> findByTargetUserIdOrderByCreatedAtDesc(Long targetUserId, Pageable pageable);
}
