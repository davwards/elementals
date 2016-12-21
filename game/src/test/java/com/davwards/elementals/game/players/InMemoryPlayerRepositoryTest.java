package com.davwards.elementals.game.players;

import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.fakeplugins.InMemoryPlayerRepository;

public class InMemoryPlayerRepositoryTest extends PlayerRepositoryTest {

    InMemoryPlayerRepository repository = new InMemoryPlayerRepository();

    @Override
    protected PlayerRepository repository() {
        return repository;
    }
}