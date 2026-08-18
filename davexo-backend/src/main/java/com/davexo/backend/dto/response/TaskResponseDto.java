package com.davexo.backend.dto.response;

import java.time.LocalDate;

import com.davexo.backend.enums.PriorityLevels;
import com.davexo.backend.enums.Status;

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

    private PriorityLevels priority;

    private Status status;

    private LocalDate createdAt;

    private LocalDate dueDate;

    private LocalDate completedAt;

    private String note;
}
