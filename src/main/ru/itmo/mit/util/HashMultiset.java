package ru.itmo.mit.util;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.AbstractSet;

public class HashMultiset<E> implements Multiset<E> {
    private final Map<E, Integer> map = new LinkedHashMap<>();
    private int size = 0;

    public HashMultiset() {}

    public HashMultiset(Collection<? extends E> collection) {
        addAll(collection);
    }

    @Override
    public int count(Object element) {
        return map.getOrDefault(element, 0);
    }

    @Override
    public Set<E> elementSet() {
        return new AbstractSet<E>() {
            @Override
            public Iterator<E> iterator() {
                return new Iterator<E>() {
                    private final Iterator<Map.Entry<E, Integer>> iterator = map.entrySet().iterator();
                    private Map.Entry<E, Integer> current = null;

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public E next() {
                        current = iterator.next();
                        return current.getKey();
                    }

                    @Override
                    public void remove() {
                        if (current == null) {
                            throw new IllegalStateException();
                        }
                        if (current.getValue() > 1) {
                            map.put(current.getKey(), current.getValue() - 1);
                            size--;
                        } else {
                            iterator.remove();
                            size--;
                        }
                        current = null;
                    }
                };
            }

            @Override
            public int size() {
                return map.size();
            }
        };
    }

    @Override
    public Set<Entry<E>> entrySet() {
        return new AbstractSet<Entry<E>>() {
            @Override
            public Iterator<Entry<E>> iterator() {
                return new Iterator<Entry<E>>() {
                    private final Iterator<Map.Entry<E, Integer>> iterator = map.entrySet().iterator();
                    private E currentKey = null;

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Entry<E> next() {
                        Map.Entry<E, Integer> entry = iterator.next();
                        currentKey = entry.getKey();
                        return new Entry<E>() {
                            @Override
                            public E getElement() {
                                return entry.getKey();
                            }

                            @Override
                            public int getCount() {
                                return entry.getValue();
                            }
                        };
                    }

                    @Override
                    public void remove() {
                        if (currentKey == null) {
                            throw new IllegalStateException();
                        }
                        int currentCount = map.get(currentKey);
                        if (currentCount > 0) {
                            size -= currentCount;
                            iterator.remove();
                            currentKey = null;
                        } else {
                            throw new IllegalStateException();
                        }
                    }
                };
            }

            @Override
            public int size() {
                return map.size();
            }
        };
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<Map.Entry<E, Integer>> entryIterator = map.entrySet().iterator();
            private Map.Entry<E, Integer> currentEntry = null;
            private int currentEntryCount = 0;

            @Override
            public boolean hasNext() {
                return currentEntryCount > 0 || entryIterator.hasNext();
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                if (currentEntryCount == 0) {
                    currentEntry = entryIterator.next();
                    currentEntryCount = currentEntry.getValue();
                }
                currentEntryCount--;
                return currentEntry.getKey();
            }

            @Override
            public void remove() {
                if (currentEntry == null || currentEntryCount == currentEntry.getValue() - 1) {
                    throw new IllegalStateException();
                }
                int count = currentEntry.getValue();
                if (count > 1) {
                    map.put(currentEntry.getKey(), count - 1);
                } else {
                    entryIterator.remove();
                }
                size--;
            }
        };
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @Override
    public Object[] toArray() {
        Object[] array = new Object[size];
        int index = 0;
        for (Map.Entry<E, Integer> entry : map.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                array[index++] = entry.getKey();
            }
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            @SuppressWarnings("unchecked")
            T[] newArray = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
            a = newArray;
        }
        int index = 0;
        for (Map.Entry<E, Integer> entry : map.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                @SuppressWarnings("unchecked")
                T item = (T) entry.getKey();
                a[index++] = item;
            }
        }
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(E e) {
        map.put(e, map.getOrDefault(e, 0) + 1);
        size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (!map.containsKey(o)) {
            return false;
        }
        int count = map.get(o);
        if (count > 1) {
            map.put((E)o, count - 1);
        } else {
            map.remove(o);
        }
        size--;
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object e : c) {
            if (remove(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void clear() {
        map.clear();
        size = 0;
    }
}
