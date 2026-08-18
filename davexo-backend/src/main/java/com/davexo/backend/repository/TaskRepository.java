package com.davexo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davexo.backend.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    Optional<Task> findByIdAndUserId(Integer taskId, Integer userId);

    List<Task> findAllByUserId(Integer userId);

    Integer deleteByIdAndUserId(Integer taskId, Integer userId);
}
