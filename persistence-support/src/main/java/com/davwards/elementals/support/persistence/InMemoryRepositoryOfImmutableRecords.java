package com.davwards.elementals.support.persistence;

import java.util.*;

public abstract class InMemoryRepositoryOfImmutableRecords<U, S extends SavedEntity<I>, I> implements
        CrudRepository<U, S, I> {
    protected Map<I, S> contents = new HashMap<>();

    protected abstract I createId(String value);
    protected abstract S buildSavedRecord(U record, I id);

    @Override
    public S save(U record) {
        I id = createId(UUID.randomUUID().toString());
        S savedPlayer = buildSavedRecord(record, id);

        contents.put(id, savedPlayer);

        return savedPlayer;
    }

    @Override
    public Optional<S> find(I id) {
        return Optional.ofNullable(contents.get(id));
    }

    @Override
    public List<S> all() {
        return new ArrayList<>(contents.values());
    }

    @Override
    public S update(S record) {
        contents.put(record.getId(), record);
        return record;
    }
}
