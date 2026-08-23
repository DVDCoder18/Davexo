package com.davexo.backend.dto.request;

import java.time.LocalDate;

import com.davexo.backend.enums.TaskPriority;
import com.davexo.backend.enums.TaskStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class TaskUpdateRequestDto {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotNull
    private TaskPriority priority;

    @NotNull
    private TaskStatus status;

    @Schema(nullable = true)
    private LocalDate dueDate;

    @Schema(nullable = true)
    private LocalDate completedAt;

    @Size(max = 255)
    @Schema(nullable = true)
    private String note;
        
}
