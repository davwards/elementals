package com.davwards.elementals.game.support.persistence;

import java.time.LocalDateTime;

public interface SavedEntity<I> {
    I getId();
    LocalDateTime createdAt();
}
