package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("SELECT t FROM Ticket t WHERE t.project.id = :projectId AND t.deletedAt IS NULL")
    Page<Ticket> findByProjectIdAndNotDeleted(@Param("projectId") Long projectId, Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.project.id = :projectId AND t.status = :status AND t.deletedAt IS NULL")
    Page<Ticket> findByProjectIdAndStatusAndNotDeleted(@Param("projectId") Long projectId,
                                                       @Param("status") String status,
                                                       Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE t.project.id = :projectId AND t.deletedAt IS NULL AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Ticket> searchByProjectAndKeyword(@Param("projectId") Long projectId,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);

    @Query("SELECT t.status, COUNT(t) FROM Ticket t " +
            "WHERE t.project.id = :projectId AND t.deletedAt IS NULL " +
            "GROUP BY t.status")
    java.util.List<Object[]> countByProjectIdAndStatus(@Param("projectId") Long projectId);

    List<Ticket> findByDueDateBeforeAndStatusNotIn(LocalDate dueDate, List<String> status);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE DATE(t.createdAt) = :date AND t.deletedAt IS NULL")
    Long countByCreatedDate(@Param("date") LocalDate date);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.dueDate < CURRENT_DATE AND t.status NOT IN ('COMPLETED', 'DELAYED') AND t.deletedAt IS NULL")
    Long countSlaWarnings();

    @Query("""
        SELECT t.status, COUNT(t) FROM Ticket t
        WHERE t.deletedAt IS NULL
        GROUP BY t.status
        """)
    java.util.List<Object[]> aggregateStatusCount();

    @Query("SELECT t FROM Ticket t WHERE t.deletedAt IS NULL ORDER BY t.updatedAt DESC LIMIT :limit")
    List<Ticket> findRecentTickets(@Param("limit") int limit);
}
