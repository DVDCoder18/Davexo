package com.davexo.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.davexo.backend.dto.request.TaskCreateRequestDto;
import com.davexo.backend.dto.request.TaskUpdateRequestDto;
import com.davexo.backend.dto.response.TaskResponseDto;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDto> addTask(@Valid @RequestBody TaskCreateRequestDto taskCreateRequestDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        TaskResponseDto response = taskService.addTask(taskCreateRequestDto, customUserDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> getTaskDetail(
            @PathVariable Integer taskId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        TaskResponseDto response = taskService.getTaskDetail(taskId, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);

    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<TaskResponseDto> response = taskService.getAllTasks(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer taskId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        taskService.deleteTask(taskId, customUserDetails.getUser().getId());
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDto> updateTask (@Valid @RequestBody TaskUpdateRequestDto taskUpdateRequestDto,
            @PathVariable Integer taskId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        TaskResponseDto response = taskService.updateTask(taskUpdateRequestDto, taskId,
                customUserDetails.getUser().getId());
        return ResponseEntity.ok(response);
    }


}
