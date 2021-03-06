package com.davwards.elementals.habits;

import com.davwards.elementals.habits.models.Habit;
import com.davwards.elementals.habits.models.HabitId;
import com.davwards.elementals.habits.models.SavedHabit;
import com.davwards.elementals.habits.persistence.HabitRepository;
import com.davwards.elementals.players.UpdatePlayerCurrencies;
import com.davwards.elementals.players.models.SavedPlayer;
import com.davwards.elementals.players.persistence.PlayerRepository;

import static com.davwards.elementals.habits.ExerciseHabit.Sides.DOWNSIDE;
import static com.davwards.elementals.habits.ExerciseHabit.Sides.UPSIDE;
import static com.davwards.elementals.support.language.StrictOptional.strict;

public class ExerciseHabit {
    public interface Outcome<T> {
        T successfullyExercisedHabit(SavedPlayer player);

        T noSuchHabit();

        T habitDoesNotHaveSpecifiedSide();
    }

    public enum Sides {
        UPSIDE, DOWNSIDE
    }

    public <T> T perform(HabitId id, Sides side, Outcome<T> outcome) {
        return strict(habitRepository.find(id))
                .map(habit -> habitHasSpecifiedSide(habit, side)
                        ? outcome.successfullyExercisedHabit(updatePlayer(habit, side))
                        : outcome.habitDoesNotHaveSpecifiedSide())
                .orElseGet(outcome::noSuchHabit);
    }

    private final HabitRepository habitRepository;

    private final PlayerRepository playerRepository;

    public ExerciseHabit(HabitRepository habitRepository, PlayerRepository playerRepository) {
        this.habitRepository = habitRepository;
        this.playerRepository = playerRepository;
    }

    private SavedPlayer updatePlayer(SavedHabit habit, Sides side) {
        return new UpdatePlayerCurrencies(playerRepository).perform(
                habit.playerId(),
                currencyChangesFor(side),
                new UpdatePlayerCurrencies.Outcome<SavedPlayer>() {
                    @Override
                    public SavedPlayer updatedPlayer(SavedPlayer player) {
                        return player;
                    }

                    @Override
                    public SavedPlayer noSuchPlayer() {
                        throw new RuntimeException("Habit " + habit + " referenced nonexistant player!");
                    }
                }
        );
    }

    private UpdatePlayerCurrencies.CurrencyChanges currencyChangesFor(Sides side) {
        if (side == UPSIDE) {
            return new UpdatePlayerCurrencies.CurrencyChanges()
                    .experience(HabitGameConstants.HABIT_UPSIDE_EXPERIENCE_PRIZE)
                    .coin(HabitGameConstants.HABIT_UPSIDE_COIN_PRIZE);
        } else {
            return new UpdatePlayerCurrencies.CurrencyChanges()
                    .health(HabitGameConstants.HABIT_DOWNSIDE_PENALTY);
        }
    }


    private boolean habitHasSpecifiedSide(Habit habit, Sides side) {
        return (side == UPSIDE && habit.hasUpside()) ||
                (side == DOWNSIDE && habit.hasDownside());
    }
}
