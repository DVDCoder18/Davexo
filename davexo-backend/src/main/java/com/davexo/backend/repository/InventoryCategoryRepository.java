package com.davexo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davexo.backend.entity.InventoryCategory;

public interface InventoryCategoryRepository extends JpaRepository<InventoryCategory, Integer> {

    Optional<InventoryCategory> findByIdAndUserId(Integer inventoryCategoryId, Integer userId);

    List<InventoryCategory> findAllByUserId(Integer userId);

    long deleteByIdAndUserId(Integer inventoryCategoryId, Integer userId);

    boolean existsByNameIgnoreCaseAndUserId(String name, Integer userId);

    boolean existsByNameIgnoreCaseAndUserIdAndIdNot(String name, Integer userId, Integer inventoryCategoryId);

}
