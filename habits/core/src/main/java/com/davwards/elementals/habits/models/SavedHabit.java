package com.davwards.elementals.habits.models;

import com.davwards.elementals.support.persistence.SavedEntity;
import org.immutables.value.Value;

@Value.Immutable
public interface SavedHabit extends Habit, SavedEntity<HabitId> {
}
