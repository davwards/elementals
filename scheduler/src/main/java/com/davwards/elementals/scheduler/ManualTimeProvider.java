package com.davwards.elementals.scheduler;

import java.time.LocalDateTime;

public class ManualTimeProvider implements TimeProvider {
    LocalDateTime currentTime;

    public ManualTimeProvider() {
        this.currentTime = LocalDateTime.now();
    }

    public ManualTimeProvider(LocalDateTime currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public LocalDateTime currentTime() {
        return this.currentTime;
    }

    public void setCurrentTime(LocalDateTime currentTime) {
        this.currentTime = currentTime;
    }
}
