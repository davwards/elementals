package com.davwards.elementals.loot.models;

public class LootId {
    private final String value;

    public LootId(String value) {
        assert(value != null);
        this.value = value;
    }

    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LootId lootId = (LootId) o;

        return value.equals(lootId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
