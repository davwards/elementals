package com.davwards.elementals.game.entities.players;

public class PlayerId {
    private final String value;

    public PlayerId(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerId playerId = (PlayerId) o;

        return value.equals(playerId.value);

    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
