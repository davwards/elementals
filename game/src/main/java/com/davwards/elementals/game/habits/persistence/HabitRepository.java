package com.davwards.elementals.game.habits.persistence;

import com.davwards.elementals.game.habits.models.HabitId;
import com.davwards.elementals.game.habits.models.SavedHabit;
import com.davwards.elementals.game.habits.models.UnsavedHabit;
import com.davwards.elementals.game.players.models.PlayerId;
import com.davwards.elementals.game.support.persistence.CrudRepository;

import java.util.List;

public interface HabitRepository extends CrudRepository<UnsavedHabit, SavedHabit, HabitId> {
    List<SavedHabit> findByPlayerId(PlayerId playerId);
}
