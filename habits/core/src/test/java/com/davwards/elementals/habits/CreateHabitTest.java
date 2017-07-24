package com.davwards.elementals.habits;

import com.davwards.elementals.habits.CreateHabit;
import com.davwards.elementals.habits.models.SavedHabit;
import com.davwards.elementals.habits.persistence.HabitRepository;
import com.davwards.elementals.habits.persistence.InMemoryHabitRepository;
import com.davwards.elementals.players.models.PlayerId;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class CreateHabitTest {

    private HabitRepository habitRepository = new InMemoryHabitRepository();

    @Test
    public void forUpsideOnlyHabit_returnsSuccessOutcomeWithCreatedHabit() throws Exception {
        SavedHabit result = new CreateHabit(habitRepository).perform(
                new PlayerId("some-player-id"),
                "Title of habit",
                CreateHabit.Sides.UPSIDE,
                habit -> habit
        );

        assertThat(result, equalTo(habitRepository.find(result.getId()).get()));
        assertThat(result.title(), equalTo("Title of habit"));
        assertThat(result.playerId(), equalTo(new PlayerId("some-player-id")));
        assertThat(result.hasUpside(), is(true));
        assertThat(result.hasDownside(), is(false));
    }

    @Test
    public void forDownsideOnlyHabit_returnsSuccessOutcomeWithCreatedHabit() throws Exception {
        SavedHabit result = new CreateHabit(habitRepository).perform(
                new PlayerId("some-player-id"),
                "Title of habit",
                CreateHabit.Sides.DOWNSIDE,
                habit -> habit
        );

        assertThat(result, equalTo(habitRepository.find(result.getId()).get()));
        assertThat(result.title(), equalTo("Title of habit"));
        assertThat(result.playerId(), equalTo(new PlayerId("some-player-id")));
        assertThat(result.hasUpside(), is(false));
        assertThat(result.hasDownside(), is(true));
    }
    @Test

    public void forDoublesidedHabit_returnsSuccessOutcomeWithCreatedHabit() throws Exception {
        SavedHabit result = new CreateHabit(habitRepository).perform(
                new PlayerId("some-player-id"),
                "Title of habit",
                CreateHabit.Sides.BOTH_SIDES,
                habit -> habit
        );

        assertThat(result, equalTo(habitRepository.find(result.getId()).get()));
        assertThat(result.title(), equalTo("Title of habit"));
        assertThat(result.playerId(), equalTo(new PlayerId("some-player-id")));
        assertThat(result.hasUpside(), is(true));
        assertThat(result.hasDownside(), is(true));
    }
}