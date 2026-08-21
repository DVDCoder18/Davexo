package com.davexo.backend.service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.davexo.backend.dto.request.TaskCreateRequestDto;
import com.davexo.backend.dto.request.TaskUpdateRequestDto;
import com.davexo.backend.dto.response.TaskResponseDto;
import com.davexo.backend.entity.Task;
import com.davexo.backend.entity.User;
import com.davexo.backend.enums.TaskStatus;
import com.davexo.backend.exception.BusinessException;
import com.davexo.backend.exception.ResourceNotFoundException;
import com.davexo.backend.mapper.TaskMapper;
import com.davexo.backend.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final Clock clock;

    @Transactional
    public TaskResponseDto addTask(
            TaskCreateRequestDto taskCreateRequestDto,
            User authenticatedUser) {

        Task task = taskMapper.toTaskEntity(taskCreateRequestDto);

        task.setUser(authenticatedUser);

        Task savedTask = taskRepository.save(task);

        return taskMapper.toTaskResponseDto(savedTask);
    }

    public TaskResponseDto getTaskDetail(
            Integer taskId,
            Integer userId) {

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        return taskMapper.toTaskResponseDto(task);
    }

    public List<TaskResponseDto> getAllTasks(Integer userId) {

        List<Task> taskList = taskRepository.findAllByUserId(userId);

        return taskList.stream()
                .map(taskMapper::toTaskResponseDto)
                .toList();
    }

    @Transactional
    public void deleteTask(
            Integer taskId,
            Integer userId) {

        long deleteCount = taskRepository.deleteByIdAndUserId(taskId, userId);

        if (deleteCount == 0) {
            throw new ResourceNotFoundException("Task not found");
        }
    }

    @Transactional
    public TaskResponseDto updateTask(
            TaskUpdateRequestDto dto,
            Integer taskId,
            Integer userId) {

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (dto.getDueDate() != null && dto.getDueDate().isBefore(task.getCreatedAt())) {
            throw new BusinessException("The due date can't be before the date of the task's creation");
        }

        if (dto.getCompletedAt() != null) {
            if (dto.getCompletedAt().isBefore(task.getCreatedAt())
                    || dto.getCompletedAt().isAfter(LocalDate.now(clock))) {

                throw new BusinessException(
                        "Date of completion must be between the task creation date and today's date");
            }
        }

        if (dto.getStatus() == TaskStatus.COMPLETED) {
            if (dto.getCompletedAt() == null) {
                task.setCompletedAt(LocalDate.now(clock));
            } else {
                task.setCompletedAt(dto.getCompletedAt());
            }
        }

        if (dto.getStatus() == TaskStatus.TO_DO) {
            task.setCompletedAt(null);
        }

        task.setTitle(dto.getTitle());
        task.setPriority(dto.getPriority());
        task.setStatus(dto.getStatus());
        task.setDueDate(dto.getDueDate());
        task.setNote(dto.getNote());

        Task savedTask = taskRepository.save(task);

        return taskMapper.toTaskResponseDto(savedTask);
    }
}