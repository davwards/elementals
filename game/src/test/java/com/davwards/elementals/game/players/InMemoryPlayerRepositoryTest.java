package com.davwards.elementals.game.players;

import com.davwards.elementals.game.fakes.InMemoryPlayerRepository;

public class InMemoryPlayerRepositoryTest extends PlayerRepositoryTest {

    private InMemoryPlayerRepository repository = new InMemoryPlayerRepository();

    @Override
    protected PlayerRepository repository() {
        return repository;
    }
}