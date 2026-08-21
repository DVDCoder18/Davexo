package com.davexo.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.davexo.backend.dto.request.TaskCreateRequestDto;
import com.davexo.backend.dto.request.TaskUpdateRequestDto;
import com.davexo.backend.dto.response.TaskResponseDto;
import com.davexo.backend.entity.Task;
import com.davexo.backend.entity.User;
import com.davexo.backend.enums.TaskPriority;
import com.davexo.backend.enums.TaskStatus;
import com.davexo.backend.exception.BusinessException;
import com.davexo.backend.exception.ResourceNotFoundException;
import com.davexo.backend.mapper.TaskMapper;
import com.davexo.backend.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    private static final Integer USER_ID = 1;
    private static final Integer TASK_ID = 10;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(
                Instant.parse("2026-08-20T12:00:00Z"),
                ZoneId.of("Europe/Paris"));

        taskService = new TaskService(
                taskRepository,
                taskMapper,
                fixedClock);
    }

    @Test
    void addTask_shouldAssociateAuthenticatedUserAndSaveTask() {
        TaskCreateRequestDto dto = new TaskCreateRequestDto();
        User authenticatedUser = new User();

        Task mappedTask = new Task();
        Task savedTask = new Task();
        TaskResponseDto responseDto = new TaskResponseDto();

        when(taskMapper.toTaskEntity(dto))
                .thenReturn(mappedTask);

        when(taskRepository.save(mappedTask))
                .thenReturn(savedTask);

        when(taskMapper.toTaskResponseDto(savedTask))
                .thenReturn(responseDto);

        TaskResponseDto result = taskService.addTask(
                dto,
                authenticatedUser);

        assertSame(
                authenticatedUser,
                mappedTask.getUser());

        assertSame(
                responseDto,
                result);

        verify(taskRepository).save(mappedTask);
    }

    @Test
    void getTaskDetail_shouldReturnTaskWhenFound() {
        Task task = new Task();
        TaskResponseDto responseDto = new TaskResponseDto();

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID))
                .thenReturn(Optional.of(task));

        when(taskMapper.toTaskResponseDto(task))
                .thenReturn(responseDto);

        TaskResponseDto result = taskService.getTaskDetail(
                TASK_ID,
                USER_ID);

        assertSame(
                responseDto,
                result);
    }

    @Test
    void getTaskDetail_shouldThrowWhenTaskIsNotFound() {
        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.getTaskDetail(
                        TASK_ID,
                        USER_ID));

        assertEquals(
                "Task not found",
                exception.getMessage());
    }

    @Test
    void getAllTasks_shouldReturnMappedTasks() {
        Task task1 = new Task();
        Task task2 = new Task();

        TaskResponseDto responseDto1 = new TaskResponseDto();
        TaskResponseDto responseDto2 = new TaskResponseDto();

        when(taskRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(
                        task1,
                        task2));

        when(taskMapper.toTaskResponseDto(task1))
                .thenReturn(responseDto1);

        when(taskMapper.toTaskResponseDto(task2))
                .thenReturn(responseDto2);

        List<TaskResponseDto> result = taskService.getAllTasks(USER_ID);

        assertEquals(
                2,
                result.size());

        assertSame(
                responseDto1,
                result.get(0));

        assertSame(
                responseDto2,
                result.get(1));
    }

    @Test
    void deleteTask_shouldDeleteTaskWhenItExists() {
        when(taskRepository.deleteByIdAndUserId(
                TASK_ID,
                USER_ID))
                .thenReturn(1L);

        taskService.deleteTask(
                TASK_ID,
                USER_ID);

        verify(taskRepository).deleteByIdAndUserId(
                TASK_ID,
                USER_ID);
    }

    @Test
    void deleteTask_shouldThrowWhenTaskIsNotFound() {
        when(taskRepository.deleteByIdAndUserId(
                TASK_ID,
                USER_ID))
                .thenReturn(0L);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.deleteTask(
                        TASK_ID,
                        USER_ID));

        assertEquals(
                "Task not found",
                exception.getMessage());
    }

    @Test
    void updateTask_shouldThrowWhenTaskIsNotFound() {
        TaskUpdateRequestDto dto = new TaskUpdateRequestDto();

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> taskService.updateTask(
                        dto,
                        TASK_ID,
                        USER_ID));

        assertEquals(
                "Task not found",
                exception.getMessage());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_shouldRejectDueDateBeforeCreationDate() {
        Task task = createExistingTask();

        TaskUpdateRequestDto dto = createValidUpdateDto();

        dto.setDueDate(
                LocalDate.of(2026, 8, 9));

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID))
                .thenReturn(Optional.of(task));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> taskService.updateTask(
                        dto,
                        TASK_ID,
                        USER_ID));

        assertEquals(
                "The due date can't be before the date of the task's creation",
                exception.getMessage());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_shouldRejectCompletionDateBeforeCreationDate() {
        Task task = createExistingTask();

        TaskUpdateRequestDto dto = createValidUpdateDto();

        dto.setCompletedAt(
                LocalDate.of(2026, 8, 9));

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID))
                .thenReturn(Optional.of(task));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> taskService.updateTask(
                        dto,
                        TASK_ID,
                        USER_ID));

        assertEquals(
                "Date of completion must be between the task creation date and today's date",
                exception.getMessage());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_shouldRejectCompletionDateInFuture() {
        Task task = createExistingTask();

        TaskUpdateRequestDto dto = createValidUpdateDto();

        dto.setCompletedAt(
                LocalDate.of(2026, 8, 21));

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID))
                .thenReturn(Optional.of(task));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> taskService.updateTask(
                        dto,
                        TASK_ID,
                        USER_ID));

        assertEquals(
                "Date of completion must be between the task creation date and today's date",
                exception.getMessage());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_shouldSetTodayAsCompletionDateWhenStatusBecomesCompletedAndDateIsNull() {
        Task task = createExistingTask();

        TaskUpdateRequestDto dto = createValidUpdateDto();

        dto.setStatus(TaskStatus.COMPLETED);
        dto.setCompletedAt(null);

        TaskResponseDto responseDto = new TaskResponseDto();

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(task))
                .thenReturn(task);

        when(taskMapper.toTaskResponseDto(task))
                .thenReturn(responseDto);

        TaskResponseDto result = taskService.updateTask(
                dto,
                TASK_ID,
                USER_ID);

        assertEquals(
                LocalDate.of(2026, 8, 20),
                task.getCompletedAt());

        assertEquals(
                TaskStatus.COMPLETED,
                task.getStatus());

        assertSame(
                responseDto,
                result);
    }

    @Test
    void updateTask_shouldUseProvidedCompletionDateWhenStatusIsCompleted() {
        Task task = createExistingTask();

        TaskUpdateRequestDto dto = createValidUpdateDto();

        LocalDate completionDate = LocalDate.of(
                2026,
                8,
                18);

        dto.setStatus(TaskStatus.COMPLETED);
        dto.setCompletedAt(completionDate);

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(task))
                .thenReturn(task);

        when(taskMapper.toTaskResponseDto(task))
                .thenReturn(new TaskResponseDto());

        taskService.updateTask(
                dto,
                TASK_ID,
                USER_ID);

        assertEquals(
                completionDate,
                task.getCompletedAt());
    }

    @Test
    void updateTask_shouldClearCompletionDateWhenStatusBecomesToDo() {
        Task task = createExistingTask();

        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(
                LocalDate.of(2026, 8, 18));

        TaskUpdateRequestDto dto = createValidUpdateDto();

        dto.setStatus(TaskStatus.TO_DO);
        dto.setCompletedAt(null);

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(task))
                .thenReturn(task);

        when(taskMapper.toTaskResponseDto(task))
                .thenReturn(new TaskResponseDto());

        taskService.updateTask(
                dto,
                TASK_ID,
                USER_ID);

        assertNull(task.getCompletedAt());

        assertEquals(
                TaskStatus.TO_DO,
                task.getStatus());
    }

    @Test
    void updateTask_shouldUpdateTaskFieldsAndSave() {
        Task task = createExistingTask();

        TaskUpdateRequestDto dto = createValidUpdateDto();

        TaskResponseDto responseDto = new TaskResponseDto();

        when(taskRepository.findByIdAndUserId(
                TASK_ID,
                USER_ID))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(task))
                .thenReturn(task);

        when(taskMapper.toTaskResponseDto(task))
                .thenReturn(responseDto);

        TaskResponseDto result = taskService.updateTask(
                dto,
                TASK_ID,
                USER_ID);

        assertEquals(
                dto.getTitle(),
                task.getTitle());

        assertEquals(
                dto.getPriority(),
                task.getPriority());

        assertEquals(
                dto.getStatus(),
                task.getStatus());

        assertEquals(
                dto.getDueDate(),
                task.getDueDate());

        assertEquals(
                dto.getNote(),
                task.getNote());

        assertSame(
                responseDto,
                result);

        verify(taskRepository).save(task);
    }

    private Task createExistingTask() {
        Task task = new Task();

        task.setId(TASK_ID);
        task.setCreatedAt(
                LocalDate.of(2026, 8, 10));

        task.setTitle("Existing task");
        task.setPriority(TaskPriority.IMPORTANT);
        task.setStatus(TaskStatus.TO_DO);
        task.setDueDate(
                LocalDate.of(2026, 8, 25));
        task.setNote("Existing note");

        return task;
    }

    private TaskUpdateRequestDto createValidUpdateDto() {
        TaskUpdateRequestDto dto = new TaskUpdateRequestDto();

        dto.setTitle("Updated task");
        dto.setPriority(TaskPriority.URGENT);
        dto.setStatus(TaskStatus.TO_DO);
        dto.setDueDate(
                LocalDate.of(2026, 8, 25));
        dto.setNote("Updated note");
        dto.setCompletedAt(null);

        return dto;
    }
}