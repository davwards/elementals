package com.davwards.elementals.players.persistence;

public class InMemoryPlayerRepositoryTest extends PlayerRepositoryTest {

    private InMemoryPlayerRepository repository = new InMemoryPlayerRepository();

    @Override
    protected PlayerRepository repository() {
        return repository;
    }
}