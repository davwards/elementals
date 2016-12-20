package com.davwards.elementals.game.players;

public class InMemoryPlayerRepositoryTest extends PlayerRepositoryTest {

    InMemoryPlayerRepository repository = new InMemoryPlayerRepository();

    @Override
    protected PlayerRepository repository() {
        return repository;
    }
}