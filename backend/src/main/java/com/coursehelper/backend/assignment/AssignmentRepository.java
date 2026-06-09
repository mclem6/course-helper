package com.coursehelper.backend.assignment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByUserId(Long userId);
    List<Assignment> findByCourseIdAndUserId(Long courseId, Long userId);
    
}
