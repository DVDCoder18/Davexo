package com.davexo.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.davexo.backend.entity.InventoryItem;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {
    
    Optional<InventoryItem> findByIdAndUserId(Integer inventoryItemId, Integer userId);

    List<InventoryItem> findAllByUserId(Integer userId);

    long deleteByIdAndUserId(Integer inventoryItemId, Integer userId);

    @Query("""
        SELECT i
        FROM InventoryItem i
        WHERE i.user.id = :userId
        AND i.status IN (
            com.davexo.backend.enums.InventoryStatus.MISSING,
            com.davexo.backend.enums.InventoryStatus.TO_REPLACE,
            com.davexo.backend.enums.InventoryStatus.LOW_STOCK,
            com.davexo.backend.enums.InventoryStatus.NEED_MORE,
            com.davexo.backend.enums.InventoryStatus.OUT_OF_SERVICE
        )
        ORDER BY
            CASE
                WHEN i.status IN (
                    com.davexo.backend.enums.InventoryStatus.MISSING,
                    com.davexo.backend.enums.InventoryStatus.TO_REPLACE
                ) THEN 1
                WHEN i.status IN (
                    com.davexo.backend.enums.InventoryStatus.LOW_STOCK,
                    com.davexo.backend.enums.InventoryStatus.NEED_MORE
                ) THEN 2
                WHEN i.status = com.davexo.backend.enums.InventoryStatus.OUT_OF_SERVICE THEN 3
                ELSE 4
            END,
            LOWER(i.name)
        """)
    List<InventoryItem> findShoppingListByUserId(@Param("userId") Integer userId);

}
