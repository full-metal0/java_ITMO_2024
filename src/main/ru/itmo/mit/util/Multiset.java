package ru.itmo.mit.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public interface Multiset<E> extends Collection<E> {

    /**
     * Returns the number of occurrences of an element in this multiset.
     * <p>
     * Expected complexity: Same as `contains`
     */
    int count(Object element);

    /**
     * Returns the set of distinct elements contained in this multiset.
     * <p>
     * Expected complexity: O(1)
     */
    Set<E> elementSet();

    /**
     * Returns the set of entries representing the data of this multiset.
     * <p>
     * Expected complexity: O(1)
     */
    Set<Entry<E>> entrySet();

    /**
     * Elements that occur multiple times in the multiset will appear multiple times in this iterator.
     * <p>
     * Expected complexity: O(1)
     */
    @Override
    @NotNull
    Iterator<E> iterator();

    @Override
    boolean add(E e);

    @Override
    boolean remove(Object o);

    @Override
    int size();

    @Override
    boolean contains(Object o);

    @Override
    Object[] toArray();

    @Override
    <T> T[] toArray(T[] a);

    interface Entry<E> {

        E getElement();

        int getCount();
    }
}

