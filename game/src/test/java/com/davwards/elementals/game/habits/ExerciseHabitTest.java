package com.davwards.elementals.game.habits;

import com.davwards.elementals.game.habits.models.HabitId;
import com.davwards.elementals.game.habits.models.SavedHabit;
import com.davwards.elementals.game.habits.persistence.HabitRepository;
import com.davwards.elementals.game.habits.persistence.InMemoryHabitRepository;
import com.davwards.elementals.game.players.models.SavedPlayer;
import com.davwards.elementals.game.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.players.persistence.PlayerRepository;
import org.junit.Test;

import static com.davwards.elementals.game.habits.ExerciseHabit.Sides.DOWNSIDE;
import static com.davwards.elementals.game.habits.ExerciseHabit.Sides.UPSIDE;
import static com.davwards.elementals.game.support.test.Assertions.assertThatInteger;
import static com.davwards.elementals.game.support.test.Factories.randomString;
import static com.davwards.elementals.game.support.test.Factories.randomUnsavedHabit;
import static com.davwards.elementals.game.support.test.Factories.randomUnsavedPlayer;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExerciseHabitTest {

    private HabitRepository habitRepository = new InMemoryHabitRepository();
    private PlayerRepository playerRepository = new InMemoryPlayerRepository();
    private final ExerciseHabit exerciseHabit = new ExerciseHabit(
            habitRepository,
            playerRepository
    );

    private SavedPlayer player = playerRepository.save(randomUnsavedPlayer());
    private SavedHabit downsideHabit = habitRepository.save(
            randomUnsavedHabit()
                    .withPlayerId(player.getId())
                    .withHasUpside(false)
                    .withHasDownside(true)
    );
    private SavedHabit upsideHabit = habitRepository.save(
            randomUnsavedHabit()
                    .withPlayerId(player.getId())
                    .withHasUpside(true)
                    .withHasDownside(false)
    );
    private SavedHabit doublesidedHabit = habitRepository.save(
            randomUnsavedHabit()
                    .withPlayerId(player.getId())
                    .withHasUpside(true)
                    .withHasDownside(true)
    );

    @Test
    public void whenHabitDoesNotExist_returnsNoSuchHabitOutcome() {
        String noise = randomString(10);
        String result = exerciseHabit.perform(
                new HabitId("no-such-habit"),
                UPSIDE,
                outcome(noise)
        );

        assertThat(result, equalTo("no such habit - " + noise));
    }

    @Test
    public void whenHabitDoesExist_andDoesNotHaveSpecifiedSide_returnsNoSuchSideOutcome() throws Exception {
        String noise = randomString(10);

        assertThat(exerciseHabit.perform(
                upsideHabit.getId(),
                DOWNSIDE,
                outcome(noise)
        ), equalTo("no side - " + noise));

        assertThat(exerciseHabit.perform(
                downsideHabit.getId(),
                UPSIDE,
                outcome(noise)
        ), equalTo("no side - " + noise));
    }

    @Test
    public void whenHabitDoesExist_andDoesHaveSpecifiedSide_returnsSuccessOutcome() throws Exception {
        String noise = randomString(10);

        assertThat(exerciseHabit.perform(
                upsideHabit.getId(),
                UPSIDE,
                outcome(noise)
        ), equalTo("success: " + playerRepository.find(player.getId()).get() + " - " + noise));

        assertThat(exerciseHabit.perform(
                downsideHabit.getId(),
                DOWNSIDE,
                outcome(noise)
        ), equalTo("success: " + playerRepository.find(player.getId()).get() + " - " + noise));

        assertThat(exerciseHabit.perform(
                doublesidedHabit.getId(),
                UPSIDE,
                outcome(noise)
        ), equalTo("success: " + playerRepository.find(player.getId()).get() + " - " + noise));

        assertThat(exerciseHabit.perform(
                doublesidedHabit.getId(),
                DOWNSIDE,
                outcome(noise)
        ), equalTo("success: " + playerRepository.find(player.getId()).get() + " - " + noise));
    }

    @Test
    public void whenHabitDoesExist_andSideIsUpside_rewardsPlayerWithExperience() {
        assertThatInteger(() ->
                playerRepository
                        .find(player.getId()).get()
                        .experience()
        ).increasesWhen(() ->
                exerciseHabit.perform(
                        upsideHabit.getId(),
                        UPSIDE,
                        outcome("")
                )
        );
    }

    @Test
    public void whenhabitDoesExist_andSideIsUpside_rewardsPlayerWithCoin() {
        assertThatInteger(() ->
                playerRepository
                        .find(player.getId()).get()
                        .coin()
        ).increasesWhen(() ->
                exerciseHabit.perform(
                        upsideHabit.getId(),
                        UPSIDE,
                        outcome("")
                )
        );
    }

    @Test
    public void whenHabitDoesExist_andSideIsDownside_damagesPlayer() {
        assertThatInteger(() ->
                playerRepository
                        .find(player.getId()).get()
                        .health()
        ).decreasesWhen(() ->
                exerciseHabit.perform(
                        downsideHabit.getId(),
                        DOWNSIDE,
                        outcome("")
                )
        );
    }

    private ExerciseHabit.Outcome<String> outcome(final String noise) {
        return new ExerciseHabit.Outcome<String>() {
            @Override
            public String successfullyExercisedHabit(SavedPlayer player) {
                return "success: " + player + " - " + noise;
            }

            @Override
            public String noSuchHabit() {
                return "no such habit - " + noise;
            }

            @Override
            public String habitDoesNotHaveSpecifiedSide() {
                return "no side - " + noise;
            }
        };
    }
}