package com.davwards.elementals.habits;

import com.davwards.elementals.habits.models.HabitId;
import com.davwards.elementals.habits.models.SavedHabit;
import com.davwards.elementals.habits.persistence.HabitRepository;

import static com.davwards.elementals.support.language.StrictOptional.strict;

public class FetchHabit {
    public interface Outcome<T> {
        T successfullyFetchedHabit(SavedHabit habit);
        T noSuchHabit();
    }

    public <T> T perform(HabitId id, Outcome<T> outcome) {
        return strict(habitRepository.find(id))
                .map(outcome::successfullyFetchedHabit)
                .orElseGet(outcome::noSuchHabit);
    }

    private final HabitRepository habitRepository;

    public FetchHabit(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }
}
