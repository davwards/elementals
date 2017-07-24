package com.davwards.elementals.support.persistence;

import java.util.List;
import java.util.Optional;

public interface CrudRepository<U, S, I> {
    S save(U record);
    S update(S record);
    Optional<S> find(I id);
    List<S> all();
}
