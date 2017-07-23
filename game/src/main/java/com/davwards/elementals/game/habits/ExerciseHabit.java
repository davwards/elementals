package com.davwards.elementals.game.habits;

import com.davwards.elementals.game.GameConstants;
import com.davwards.elementals.game.habits.models.Habit;
import com.davwards.elementals.game.habits.models.HabitId;
import com.davwards.elementals.game.habits.models.SavedHabit;
import com.davwards.elementals.game.habits.persistence.HabitRepository;
import com.davwards.elementals.game.players.models.ImmutableSavedPlayer;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.persistence.PlayerRepository;

import static com.davwards.elementals.game.habits.ExerciseHabit.Sides.DOWNSIDE;
import static com.davwards.elementals.game.habits.ExerciseHabit.Sides.UPSIDE;
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
        return playerRepository.find(habit.playerId())
                .map(player -> playerRepository.update(
                        side == UPSIDE
                                ? rewardedPlayer(player)
                                : damagedPlayer(player)
                ))
                .orElseThrow(() -> new RuntimeException("Habit " + habit + " referenced nonexistant player!"));
    }

    private SavedPlayer rewardedPlayer(SavedPlayer player) {
        return ImmutableSavedPlayer
                .copyOf(player)
                .withCoin(player.coin() + GameConstants.TASK_COMPLETION_COIN_PRIZE)
                .withExperience(player.experience() + GameConstants.TASK_COMPLETION_EXPERIENCE_PRIZE);
    }

    private ImmutableSavedPlayer damagedPlayer(SavedPlayer player) {
        return ImmutableSavedPlayer
                .copyOf(player)
                .withHealth(player.health() - GameConstants.EXPIRED_TASK_PENALTY);
    }

    private boolean habitHasSpecifiedSide(Habit habit, Sides side) {
        return (side == UPSIDE && habit.hasUpside()) ||
                (side == DOWNSIDE && habit.hasDownside());
    }
}
