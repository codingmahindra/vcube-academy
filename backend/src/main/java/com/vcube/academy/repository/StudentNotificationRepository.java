package com.vcube.academy.repository;

import com.vcube.academy.entity.StudentNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentNotificationRepository extends JpaRepository<StudentNotification, Long> {
    List<StudentNotification> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    List<StudentNotification> findByStudentIdAndIsReadFalseOrderByCreatedAtDesc(Long studentId);
    long countByStudentIdAndIsReadFalse(Long studentId);
}
