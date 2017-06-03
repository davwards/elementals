package com.davwards.elementals.game.tasks;

import java.time.LocalDateTime;
import java.util.Iterator;

public interface CadenceInterpreter {
    Iterator<LocalDateTime> nextOccurrences(LocalDateTime lastOccurrence, String cadence);
}
