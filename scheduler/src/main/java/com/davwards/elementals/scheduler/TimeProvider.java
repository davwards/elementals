package com.davwards.elementals.scheduler;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime currentTime();
}
