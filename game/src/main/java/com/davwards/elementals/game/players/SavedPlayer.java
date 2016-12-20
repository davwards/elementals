package com.davwards.elementals.game.players;

import com.davwards.elementals.game.SavedEntity;

public class SavedPlayer extends Player implements SavedEntity<PlayerId> {
    private final PlayerId id;

    public SavedPlayer(PlayerId id, String name, Integer experience) {
        super(name);
        this.id = id;
        this.experience = experience;
    }

    public PlayerId getId() {
        return id;
    }

    public static SavedPlayer clone(SavedPlayer savedPlayer) {
        return new SavedPlayer(
                savedPlayer.getId(),
                savedPlayer.getName(),
                savedPlayer.getExperience()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SavedPlayer that = (SavedPlayer) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
