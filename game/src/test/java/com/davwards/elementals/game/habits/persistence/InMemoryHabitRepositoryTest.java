package com.davwards.elementals.game.habits.persistence;

public class InMemoryHabitRepositoryTest extends HabitRepositoryTest {
    private HabitRepository repository = new InMemoryHabitRepository();

    @Override
    protected HabitRepository repository() {
        return repository;
    }
}
