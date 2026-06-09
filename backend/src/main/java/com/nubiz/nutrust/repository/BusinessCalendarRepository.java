package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.BusinessCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessCalendarRepository extends JpaRepository<BusinessCalendar, Long> {

    @Query("SELECT bc FROM BusinessCalendar bc WHERE bc.companyId = :companyId AND bc.status = 'ACTIVE' AND bc.deletedAt IS NULL")
    List<BusinessCalendar> findAllActiveByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT bc FROM BusinessCalendar bc WHERE bc.companyId = :companyId AND bc.isDefault = TRUE AND bc.status = 'ACTIVE' AND bc.deletedAt IS NULL")
    Optional<BusinessCalendar> findDefaultByCompanyId(@Param("companyId") Long companyId);
}
