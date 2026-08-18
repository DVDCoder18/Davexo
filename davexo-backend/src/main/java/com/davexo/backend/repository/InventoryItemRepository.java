package com.davexo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.davexo.backend.entity.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {
    
    Optional<InventoryItem> findByIdAndUserId(Integer inventoryItemId, Integer userId);

    List<InventoryItem> findAllByUserId(Integer userId);

    long deleteByIdAndUserId(Integer inventoryItemId, Integer userId);

}
