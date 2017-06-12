package com.davwards.elementals.game.habits.persistence;

import com.davwards.elementals.game.habits.models.HabitId;
import com.davwards.elementals.game.habits.models.SavedHabit;
import com.davwards.elementals.game.habits.models.UnsavedHabit;
import com.davwards.elementals.game.support.persistence.CrudRepository;

public interface HabitRepository extends CrudRepository<UnsavedHabit, SavedHabit, HabitId> {
}
