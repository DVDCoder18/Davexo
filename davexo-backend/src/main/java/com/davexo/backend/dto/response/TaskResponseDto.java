package com.davexo.backend.dto.response;

import java.time.LocalDate;

import com.davexo.backend.enums.TaskPriority;
import com.davexo.backend.enums.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Task data")
public class TaskResponseDto {

    @NotNull
    @Schema(description = "Task identifier")
    private Integer id;

    @NotNull
    @Schema(description = "Task title")
    private String title;

    @NotNull
    @Schema(description = "Task priority")
    private TaskPriority priority;

    @NotNull
    @Schema(description = "Current task status")
    private TaskStatus status;

    @NotNull
    @Schema(description = "Task creation date")
    private LocalDate createdAt;

    @Schema(description = "Optional task due date", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private LocalDate dueDate;

    @Schema(description = "Completion date, or null when the task is not completed", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private LocalDate completedAt;

    @Schema(description = "Optional task note", requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String note;
}
