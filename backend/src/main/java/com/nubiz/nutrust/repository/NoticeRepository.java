package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n WHERE n.companyId = :companyId AND n.deletedAt IS NULL ORDER BY n.isPinned DESC, n.createdAt DESC")
    List<Notice> findAllByCompanyId(@Param("companyId") Long companyId);
}
