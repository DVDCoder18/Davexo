package com.davexo.backend.mapper;

import org.springframework.stereotype.Component;

import com.davexo.backend.dto.request.TaskCreateRequestDto;
import com.davexo.backend.dto.response.TaskResponseDto;
import com.davexo.backend.entity.Task;


@Component
public class TaskMapper {


    public TaskResponseDto toTaskResponseDto(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .priority(task.getPriority())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .dueDate(task.getDueDate())
                .completedAt(task.getCompletedAt())
                .note(task.getNote())
                .build();
    }

    public Task toTaskEntity(TaskCreateRequestDto taskCreateRequestDto) {
        return Task.builder()
                .title(taskCreateRequestDto.getTitle())
                .priority(taskCreateRequestDto.getPriority())
                .dueDate(taskCreateRequestDto.getDueDate())
                .note(taskCreateRequestDto.getNote())
                .build();
    }
}
