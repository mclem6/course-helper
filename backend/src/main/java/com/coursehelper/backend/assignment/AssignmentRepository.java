package com.coursehelper.backend.assignment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByUserId(Long userId);
    List<Assignment> findByUserIdAndStatus(Long userId, String status);
    List<Assignment> findByUserIdAndCourseIdIn(Long userId, List<Long> courseIds);
    List<Assignment> findByUserIdAndCourseIdInAndStatus(Long userId, List<Long> courseIds, String status);
    List<Assignment> findByCourseIdAndUserId(Long courseId, Long userId);
    List<Assignment> findByCourseIdAndUserIdAndStatus(Long courseId, Long userId, String status);
    
}
