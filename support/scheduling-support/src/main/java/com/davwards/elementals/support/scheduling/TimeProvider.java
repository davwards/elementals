package com.davwards.elementals.support.scheduling;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime currentTime();
}
