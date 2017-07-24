package com.davwards.elementals.habits.persistence;

import com.davwards.elementals.habits.models.HabitId;
import com.davwards.elementals.habits.models.SavedHabit;
import com.davwards.elementals.habits.models.UnsavedHabit;
import com.davwards.elementals.players.models.PlayerId;
import com.davwards.elementals.support.persistence.CrudRepository;

import java.util.List;

public interface HabitRepository extends CrudRepository<UnsavedHabit, SavedHabit, HabitId> {
    List<SavedHabit> findByPlayerId(PlayerId playerId);
}
