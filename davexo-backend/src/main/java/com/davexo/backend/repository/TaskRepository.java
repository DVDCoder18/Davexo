package com.davexo.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.davexo.backend.entity.Task;
import com.davexo.backend.enums.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    Optional<Task> findByIdAndUserId(Integer taskId, Integer userId);

    List<Task> findAllByUserId(Integer userId);

    long deleteByIdAndUserId(Integer taskId, Integer userId);

    long countByUserIdAndStatus(Integer userId, TaskStatus status);

    @Query("""
            SELECT COUNT(t)
            FROM Task t
            WHERE t.user.id = :userId
            AND t.dueDate BETWEEN :startDate AND :endDate
            """)
    long countPlannedTasksByPeriod(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT COUNT(t)
            FROM Task t
            WHERE t.user.id = :userId
            AND t.dueDate BETWEEN :startDate AND :endDate
            AND t.completedAt IS NOT NULL
            """)
    long countCompletedPlannedTasksByPeriod(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT COUNT(t)
            FROM Task t
            WHERE t.user.id = :userId
            AND t.dueDate BETWEEN :startDate AND :endDate
            AND t.completedAt IS NOT NULL
            AND t.completedAt <= t.dueDate
            """)
    long countCompletedOnTimeTasksByPeriod(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT t.createdAt, t.completedAt
            FROM Task t
            WHERE t.user.id = :userId
            AND t.completedAt BETWEEN :startDate AND :endDate
            """)
    List<Object[]> findCompletionDatesByPeriod(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT t.priority, COUNT(t)
            FROM Task t
            WHERE t.user.id = :userId
            AND t.completedAt BETWEEN :startDate AND :endDate
            GROUP BY t.priority
            """)
    List<Object[]> countCompletedTasksByPriorityAndPeriod(
            @Param("userId") Integer userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            SELECT t
            FROM Task t
            WHERE t.user.id = :userId
              AND t.status = com.davexo.backend.enums.TaskStatus.TO_DO
            ORDER BY
                CASE
                    WHEN t.priority = com.davexo.backend.enums.TaskPriority.URGENT THEN 1
                    WHEN t.priority = com.davexo.backend.enums.TaskPriority.IMPORTANT THEN 2
                    WHEN t.priority = com.davexo.backend.enums.TaskPriority.NON_CRITICAL THEN 3
                    ELSE 4
                END,
                CASE
                    WHEN t.dueDate IS NULL THEN 1
                    ELSE 0
                END,
                t.dueDate ASC
            """)
    List<Task> findDashboardTasksByUserId(@Param("userId") Integer userId);
}
