package com.davexo.backend.dto.request;

import java.time.LocalDate;

import com.davexo.backend.enums.TaskPriority;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
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
public class TaskCreateRequestDto {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotNull
    private TaskPriority priority;

    @FutureOrPresent
    @Schema(nullable = true)
    private LocalDate dueDate;

    @Size(max = 255)
    @Schema(nullable = true)
    private String note;
}
