package com.davexo.backend.dto.response;

import java.time.LocalDate;

import com.davexo.backend.enums.TaskPriority;
import com.davexo.backend.enums.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDto {

    private Integer id;

    private String title;

    private TaskPriority priority;

    private TaskStatus status;

    private LocalDate createdAt;

    private LocalDate dueDate;

    private LocalDate completedAt;

    private String note;
}
