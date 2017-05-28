package com.davwards.elementals.game.players;

import com.davwards.elementals.game.players.persistence.InMemoryPlayerRepository;
import com.davwards.elementals.game.players.persistence.PlayerRepository;

public class InMemoryPlayerRepositoryTest extends PlayerRepositoryTest {

    private InMemoryPlayerRepository repository = new InMemoryPlayerRepository();

    @Override
    protected PlayerRepository repository() {
        return repository;
    }
}