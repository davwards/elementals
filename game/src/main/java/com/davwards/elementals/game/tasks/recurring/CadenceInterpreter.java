package com.davwards.elementals.game.tasks.recurring;

import java.time.LocalDateTime;
import java.util.Iterator;

public interface CadenceInterpreter {
    Iterator<LocalDateTime> nextOccurrences(LocalDateTime lastOccurrence, String cadence);
}
