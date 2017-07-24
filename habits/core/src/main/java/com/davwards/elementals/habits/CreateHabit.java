package com.davwards.elementals.habits;

import com.davwards.elementals.habits.models.ImmutableUnsavedHabit;
import com.davwards.elementals.habits.models.SavedHabit;
import com.davwards.elementals.habits.persistence.HabitRepository;
import com.davwards.elementals.players.models.PlayerId;

public class CreateHabit {
    public interface Outcome<T> {
        T successfullyCreatedHabit(SavedHabit createdHabit);
    }

    public enum Sides {
        UPSIDE, DOWNSIDE, BOTH_SIDES;
    }

    public <T> T perform(PlayerId playerId, String title, Sides sides, Outcome<T> outcome) {
        return outcome.successfullyCreatedHabit(habitRepository.save(
                ImmutableUnsavedHabit.builder()
                        .playerId(playerId)
                        .title(title)
                        .hasUpside(sides == Sides.UPSIDE || sides == Sides.BOTH_SIDES)
                        .hasDownside(sides == Sides.DOWNSIDE || sides == Sides.BOTH_SIDES)
                        .build()
        ));
    }

    private final HabitRepository habitRepository;

    public CreateHabit(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }
}
