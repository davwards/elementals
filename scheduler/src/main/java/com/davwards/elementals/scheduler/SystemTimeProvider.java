package com.davwards.elementals.scheduler;

import java.time.LocalDateTime;

public class SystemTimeProvider implements TimeProvider {
    @Override
    public LocalDateTime currentTime() {
        return LocalDateTime.now();
    }
}
