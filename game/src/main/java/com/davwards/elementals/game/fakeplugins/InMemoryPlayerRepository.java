package com.davwards.elementals.game.fakeplugins;

import com.davwards.elementals.game.entities.players.PlayerId;
import com.davwards.elementals.game.entities.players.PlayerRepository;
import com.davwards.elementals.game.entities.players.SavedPlayer;
import com.davwards.elementals.game.entities.players.UnsavedPlayer;

import java.util.*;

public class InMemoryPlayerRepository implements PlayerRepository {
    private Map<PlayerId, SavedPlayer> contents = new HashMap<>();

    @Override
    public SavedPlayer save(UnsavedPlayer player) {
        PlayerId id = new PlayerId(UUID.randomUUID().toString());
        SavedPlayer savedPlayer = new SavedPlayer(
                id,
                player.getName(),
                player.getExperience(),
                player.getHealth()
        );
        contents.put(id, savedPlayer);

        return SavedPlayer.clone(savedPlayer);
    }

    @Override
    public Optional<SavedPlayer> find(PlayerId id) {
        return Optional.ofNullable(contents.get(id))
                .map(SavedPlayer::clone);
    }

    @Override
    public List<SavedPlayer> all() {
        return new ArrayList<>(contents.values());
    }

    @Override
    public SavedPlayer update(SavedPlayer player) {
        contents.put(player.getId(), SavedPlayer.clone(player));
        return SavedPlayer.clone(player);
    }
}
