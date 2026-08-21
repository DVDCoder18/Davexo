package com.davexo.backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.davexo.backend.entity.Task;
import com.davexo.backend.entity.User;
import com.davexo.backend.enums.Role;
import com.davexo.backend.enums.TaskPriority;
import com.davexo.backend.enums.TaskStatus;
import com.davexo.backend.integration.AbstractIntegrationTest;

import jakarta.persistence.EntityManager;

@Transactional
class TaskRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void countPlannedTasksByPeriod_shouldCountOnlyUserTasksDueInsidePeriod() {
        User user1 = createUser(
                "user1@test.com",
                "User 1");

        User user2 = createUser(
                "user2@test.com",
                "User 2");

        createTask(
                "Start boundary",
                TaskPriority.IMPORTANT,
                TaskStatus.TO_DO,
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 8, 1),
                null,
                user1);

        createTask(
                "End boundary",
                TaskPriority.URGENT,
                TaskStatus.TO_DO,
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 8, 31),
                null,
                user1);

        createTask(
                "Outside period",
                TaskPriority.NON_CRITICAL,
                TaskStatus.TO_DO,
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 9, 1),
                null,
                user1);

        createTask(
                "Other user",
                TaskPriority.URGENT,
                TaskStatus.TO_DO,
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 8, 15),
                null,
                user2);

        createTask(
                "No due date",
                TaskPriority.IMPORTANT,
                TaskStatus.TO_DO,
                LocalDate.of(2026, 7, 20),
                null,
                null,
                user1);

        entityManager.flush();

        long result = taskRepository.countPlannedTasksByPeriod(
                user1.getId(),
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31));

        assertEquals(
                2L,
                result);
    }

    @Test
    void countCompletedPlannedTasksByPeriod_shouldCountOnlyPlannedTasksWithCompletionDate() {
        User user = createUser(
                "user@test.com",
                "User");

        createTask(
                "Completed planned task",
                TaskPriority.URGENT,
                TaskStatus.COMPLETED,
                LocalDate.of(2026, 7, 25),
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 8),
                user);

        createTask(
                "Incomplete planned task",
                TaskPriority.IMPORTANT,
                TaskStatus.TO_DO,
                LocalDate.of(2026, 7, 25),
                LocalDate.of(2026, 8, 15),
                null,
                user);

        createTask(
                "Completed outside due period",
                TaskPriority.NON_CRITICAL,
                TaskStatus.COMPLETED,
                LocalDate.of(2026, 7, 25),
                LocalDate.of(2026, 9, 5),
                LocalDate.of(2026, 8, 10),
                user);

        entityManager.flush();

        long result = taskRepository.countCompletedPlannedTasksByPeriod(
                user.getId(),
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31));

        assertEquals(
                1L,
                result);
    }

    @Test
    void countCompletedOnTimeTasksByPeriod_shouldCountOnlyTasksCompletedOnOrBeforeDueDate() {
        User user = createUser(
                "user@test.com",
                "User");

        createTask(
                "Completed before due date",
                TaskPriority.URGENT,
                TaskStatus.COMPLETED,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 8),
                user);

        createTask(
                "Completed exactly on due date",
                TaskPriority.IMPORTANT,
                TaskStatus.COMPLETED,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 8, 15),
                user);

        createTask(
                "Completed late",
                TaskPriority.NON_CRITICAL,
                TaskStatus.COMPLETED,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 8, 22),
                user);

        createTask(
                "Not completed",
                TaskPriority.IMPORTANT,
                TaskStatus.TO_DO,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 25),
                null,
                user);

        entityManager.flush();

        long result = taskRepository.countCompletedOnTimeTasksByPeriod(
                user.getId(),
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31));

        assertEquals(
                2L,
                result);
    }

    @Test
    void findCompletionDatesByPeriod_shouldFilterByCompletionDateNotDueDate() {
        User user = createUser(
                "user@test.com",
                "User");

        createTask(
                "Completed during period",
                TaskPriority.URGENT,
                TaskStatus.COMPLETED,
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 9, 1),
                LocalDate.of(2026, 8, 10),
                user);

        createTask(
                "Completed outside period",
                TaskPriority.IMPORTANT,
                TaskStatus.COMPLETED,
                LocalDate.of(2026, 7, 25),
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 9, 1),
                user);

        entityManager.flush();

        List<Object[]> result = taskRepository.findCompletionDatesByPeriod(
                user.getId(),
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31));

        assertEquals(
                1,
                result.size());

        assertEquals(
                LocalDate.of(2026, 7, 20),
                result.get(0)[0]);

        assertEquals(
                LocalDate.of(2026, 8, 10),
                result.get(0)[1]);
    }

    @Test
    void countCompletedTasksByPriorityAndPeriod_shouldGroupCompletedTasksByPriority() {
        User user1 = createUser(
                "user1@test.com",
                "User 1");

        User user2 = createUser(
                "user2@test.com",
                "User 2");

        createTask(
                "Urgent 1",
                TaskPriority.URGENT,
                TaskStatus.COMPLETED,
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 8, 5),
                LocalDate.of(2026, 8, 4),
                user1);

        createTask(
                "Urgent 2",
                TaskPriority.URGENT,
                TaskStatus.COMPLETED,
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 9),
                user1);

        createTask(
                "Important",
                TaskPriority.IMPORTANT,
                TaskStatus.COMPLETED,
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 8, 14),
                user1);

        createTask(
                "Outside completion period",
                TaskPriority.NON_CRITICAL,
                TaskStatus.COMPLETED,
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 9, 5),
                LocalDate.of(2026, 9, 4),
                user1);

        createTask(
                "Other user urgent",
                TaskPriority.URGENT,
                TaskStatus.COMPLETED,
                LocalDate.of(2026, 7, 20),
                LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 8, 14),
                user2);

        entityManager.flush();

        List<Object[]> result = taskRepository.countCompletedTasksByPriorityAndPeriod(
                user1.getId(),
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 31));

        assertEquals(
                2,
                result.size());

        long urgentCount = findPriorityCount(
                result,
                TaskPriority.URGENT);

        long importantCount = findPriorityCount(
                result,
                TaskPriority.IMPORTANT);

        assertEquals(
                2L,
                urgentCount);

        assertEquals(
                1L,
                importantCount);
    }

    private long findPriorityCount(
            List<Object[]> results,
            TaskPriority priority) {

        return results.stream()
                .filter(row -> row[0] == priority)
                .map(row -> (Long) row[1])
                .findFirst()
                .orElse(0L);
    }

    private User createUser(
            String email,
            String pseudo) {

        User user = new User();

        user.setPseudo(pseudo);
        user.setEmail(email);
        user.setPasswordHash("encoded-password");
        user.setCreatedAt(
                LocalDate.of(2026, 1, 1));
        user.setRole(Role.USER);
        user.setIsActive(true);

        entityManager.persist(user);

        return user;
    }

    private Task createTask(
            String title,
            TaskPriority priority,
            TaskStatus status,
            LocalDate createdAt,
            LocalDate dueDate,
            LocalDate completedAt,
            User user) {

        Task task = new Task();

        task.setTitle(title);
        task.setPriority(priority);
        task.setStatus(status);
        task.setCreatedAt(createdAt);
        task.setDueDate(dueDate);
        task.setCompletedAt(completedAt);
        task.setUser(user);

        entityManager.persist(task);

        return task;
    }
}