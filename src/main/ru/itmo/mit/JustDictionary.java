package ru.itmo.mit;

import java.util.*;

public class JustDictionary<K, V> implements Dictionary<K, V> {
    static int initialCapacity = 16;
    static float defaultLoad = 0.5f;

    ArrayList<Entry<K, V>>[] table;
    int size;
    float load;

    public JustDictionary() {
        this(defaultLoad);
    }

    public JustDictionary(float load) {
        if (load <= 0 || load > 1) {
            throw new IllegalArgumentException("");
        }
        this.load = load;
        this.table = new ArrayList[initialCapacity];
        this.size = 0;
    }

    private int hash(Object key) {
        return key == null ? 0 : key.hashCode() & (table.length - 1);
    }

    private void resize() {
        if (size >= table.length * load) {
            ArrayList<Entry<K, V>>[] oldTable = table;
            table = new ArrayList[table.length * 2];
            size = 0;

            for (ArrayList<Entry<K, V>> bucket : oldTable) {
                if (bucket != null) {
                    for (Entry<K, V> entry : bucket) {
                        put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean containsKey(Object key) {
        int index = hash(key);
        ArrayList<Entry<K, V>> bucket = table[index];

        if (bucket == null) return false;

        for (Entry<K, V> entry : bucket) {
            if (Objects.equals(entry.getKey(), key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        int index = hash(key);
        ArrayList<Entry<K, V>> bucket = table[index];

        if (bucket == null) return null;

        for (Entry<K, V> entry : bucket) {
            if (Objects.equals(entry.getKey(), key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        resize();
        int index = hash(key);

        if (table[index] == null) {
            table[index] = new ArrayList<>();
        }

        for (Entry<K, V> entry : table[index]) {
            if (Objects.equals(entry.getKey(), key)) {
                V oldValue = entry.getValue();
                entry.setValue(value);
                return oldValue;
            }
        }
        table[index].add(new AbstractMap.SimpleEntry<>(key, value));
        size++;
        return null;
    }

    @Override
    public V remove(Object key) {
        int index = hash(key);
        ArrayList<Entry<K, V>> bucket = table[index];

        if (bucket == null) return null;

        Iterator<Entry<K, V>> iterator = bucket.iterator();

        while (iterator.hasNext()) {
            Entry<K, V> entry = iterator.next();
            if (Objects.equals(entry.getKey(), key)) {
                V oldValue = entry.getValue();
                iterator.remove();
                size--;
                return oldValue;
            }
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {
        table = new ArrayList[initialCapacity];
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keySet = new HashSet<>();

        for (ArrayList<Entry<K, V>> bucket : table) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    keySet.add(entry.getKey());
                }
            }
        }
        return keySet;
    }

    @Override
    public Collection<V> values() {
        Collection<V> values = new ArrayList<>();

        for (ArrayList<Entry<K, V>> bucket : table) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    values.add(entry.getValue());
                }
            }
        }
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entrySet = new HashSet<>();

        for (ArrayList<Entry<K, V>> bucket : table) {
            if (bucket != null) {
                for (Entry<K, V> entry : bucket) {
                    entrySet.add(entry);
                }
            }
        }
        return entrySet;
    }
}

