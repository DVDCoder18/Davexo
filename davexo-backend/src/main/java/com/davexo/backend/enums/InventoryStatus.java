package com.davexo.backend.enums;

public enum InventoryStatus {

    // Consumable inventory status
    IN_STOCK,
    LOW_STOCK,
    MISSING,
            
    // Durable inventory status
    IN_SERVICE,
    OUT_OF_SERVICE,
    TO_REPLACE,
    NEED_MORE

}
