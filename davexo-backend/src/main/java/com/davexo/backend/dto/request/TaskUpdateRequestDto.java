package com.davexo.backend.dto.request;

import java.time.LocalDate;

import com.davexo.backend.enums.PriorityLevels;
import com.davexo.backend.enums.Status;

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
    private PriorityLevels priority;

    @NotNull
    private Status status;

    private LocalDate dueDate;

    private LocalDate completedAt;

    @Size(max = 255)
    private String note;
    
}
