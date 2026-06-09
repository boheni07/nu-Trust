package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    @Query("SELECT h FROM Holiday h WHERE h.companyId = :companyId AND h.deletedAt IS NULL")
    List<Holiday> findAllActiveByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT h FROM Holiday h WHERE h.companyId = :companyId AND h.holidayDate BETWEEN :start AND :end AND h.deletedAt IS NULL")
    List<Holiday> findByDateRange(@Param("companyId") Long companyId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
