package com.davexo.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import com.davexo.backend.exception.CustomErrorResponse;
import com.davexo.backend.security.CustomUserDetails;
import com.davexo.backend.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management")
public class TaskController {

    private final TaskService taskService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create task")
    @ApiResponse(
            responseCode = "201",
            description = "Task created successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TaskResponseDto.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<TaskResponseDto> addTask(
            @Valid @RequestBody TaskCreateRequestDto taskCreateRequestDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        TaskResponseDto response =
                taskService.addTask(taskCreateRequestDto, customUserDetails.getUser());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(
            value = "/{taskId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get task")
    @ApiResponse(
            responseCode = "200",
            description = "Task returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TaskResponseDto.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Task not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<TaskResponseDto> getTaskDetail(
            @PathVariable("taskId") Integer taskId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        TaskResponseDto response = taskService.getTaskDetail(
                taskId,
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all tasks")
    @ApiResponse(
            responseCode = "200",
            description = "Tasks returned successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(
                            schema = @Schema(implementation = TaskResponseDto.class))))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<List<TaskResponseDto>> getAllTasks(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok(
                taskService.getAllTasks(customUserDetails.getUser().getId()));
    }

    @PutMapping(
            value = "/{taskId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update task")
    @ApiResponse(
            responseCode = "200",
            description = "Task updated successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TaskResponseDto.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Invalid request",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Task not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "409",
            description = "Task business rule conflict",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<TaskResponseDto> updateTask(
            @Valid @RequestBody TaskUpdateRequestDto taskUpdateRequestDto,
            @PathVariable("taskId") Integer taskId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        TaskResponseDto response = taskService.updateTask(
                taskUpdateRequestDto,
                taskId,
                customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "Delete task")
    @ApiResponse(
            responseCode = "204",
            description = "Task deleted successfully",
            content = @Content)
    @ApiResponse(
            responseCode = "401",
            description = "Authentication required",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "Task not found",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = CustomErrorResponse.class)))
    public ResponseEntity<Void> deleteTask(
            @PathVariable("taskId") Integer taskId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        taskService.deleteTask(
                taskId,
                customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }
}