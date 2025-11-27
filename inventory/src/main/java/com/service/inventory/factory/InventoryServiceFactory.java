package com.service.inventory.factory;

import com.service.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryServiceFactory {

    private final InventoryService defaultInventoryService;

    public InventoryService getService() {
        // In the future, this method could decide which implementation of InventoryService to return
        // based on some logic (e.g., feature flags, configuration, etc.).
        return defaultInventoryService;
    }
}
