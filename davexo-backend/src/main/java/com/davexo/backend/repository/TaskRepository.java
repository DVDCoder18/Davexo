package com.davexo.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.davexo.backend.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    Optional<Task> findByIdAndUserId(Integer taskId, Integer userId);

    List<Task> findAllByUserId(Integer userId);

    long deleteByIdAndUserId(Integer taskId, Integer userId);

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
}
