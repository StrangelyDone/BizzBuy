package com.example.BizzBuy.util;

import java.util.List;

public final class IdGenerator {

    private IdGenerator() {
    }

    public static long nextId(List<?> existing) {
        if (existing == null || existing.isEmpty()) {
            return 1L;
        }

        long maxId = 0L;
        for (Object item : existing) {
            try {
                Long id = (Long) item.getClass().getMethod("getId").invoke(item);
                if (id != null && id > maxId) {
                    maxId = id;
                }
            } catch (Exception e) {
                // Skip items without getId() method
            }
        }
        return maxId + 1;
    }
}
