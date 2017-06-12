package com.davwards.elementals.game.habits.models;

import com.davwards.elementals.game.support.persistence.SavedEntity;
import org.immutables.value.Value;

@Value.Immutable
public interface SavedHabit extends Habit, SavedEntity<HabitId> {
}
