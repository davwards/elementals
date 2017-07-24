package com.davwards.elementals.support.language;

import java.util.HashMap;

public class BuildableMap<K, V> extends HashMap<K, V> {
    public BuildableMap<K, V> with(K key, V value) {
        this.put(key, value);
        return this;
    }

    public static <K, V> BuildableMap<K, V> mappingOf(Class<K> keyType, Class<V> valueType) {
        return new BuildableMap<>();
    }
}
