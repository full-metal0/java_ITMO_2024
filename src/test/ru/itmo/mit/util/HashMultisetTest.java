package ru.itmo.mit.util;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("Convert2MethodRef")
public class HashMultisetTest {

    private static final int RANDOM_SET_SIZE = 100;

    @Test
    public void testSimple() {
        Multiset<Integer> multiset = createMultiset(1, 2, 1);

        assertThat(multiset.size(), is(3));
        assertThat(multiset.count(1), is(2));
        assertThat(multiset.count(2), is(1));
        assertThat(multiset.count(3), is(0));

        assertTrue(multiset.contains(1));
        assertTrue(multiset.contains(2));
        assertFalse(multiset.contains(3));
    }

    @Test
    public void testRemove() {
        Multiset<Integer> multiset = createMultiset(1, 2, 1);
        assertThat(multiset.remove(1), is(true));

        assertThat(multiset.size(), is(2));
        assertThat(multiset.count(1), is(1));
        assertThat(multiset.count(2), is(1));

        assertTrue(multiset.contains(1));
        assertTrue(multiset.contains(2));

        assertFalse(multiset.contains(3));
    }

    @Test
    public void testSimpleIterator() {
        Multiset<Integer> multiset = createMultiset(1, 2, 1);
        assertMultisetState(multiset, 1, 1, 2);
    }

    private static void assertMultisetState(Multiset<Integer> multiset, Integer... integers) {
        assertThat(multiset.size(), is(integers.length));
        assertThat(new ArrayList<>(multiset), is(Arrays.asList(integers)));
    }

    @Test
    public void testSimpleEntrySet() {
        Multiset<Integer> multiset = createMultiset(1, 2, 1);

        Set<Multiset.Entry<Integer>> entries = multiset.entrySet();

        assertThat(entries.size(), is(2));
    }

    @Test
    public void testSimpleEntryIterator() {
        Multiset<Integer> multiset = createMultiset(3, 1, 2, 1, 2, 1);

        @SuppressWarnings("unchecked")
        Multiset.Entry<Integer>[] entries = new ArrayList<>(multiset.entrySet()).toArray(new Multiset.Entry[2]);

        assertThat(entries.length, is(3));

        assertThat(entries[0].getElement(), is(3));
        assertThat(entries[0].getCount(), is(1));

        assertThat(entries[1].getElement(), is(1));
        assertThat(entries[1].getCount(), is(3));

        assertThat(entries[2].getElement(), is(2));
        assertThat(entries[2].getCount(), is(2));
    }

    @Test
    public void testEntryIteratorRemove() {
        Multiset<Integer> multiset = createMultiset(3, 1, 2, 1, 2, 1);

        Iterator<Multiset.Entry<Integer>> iterator = multiset.entrySet().iterator();

        assertTrue(iterator.hasNext());
        Multiset.Entry<Integer> next = iterator.next();
        assertThat(next.getElement(), is(3));
        assertThat(next.getCount(), is(1));
        iterator.remove();
        assertMultisetState(multiset, 1, 1, 1, 2, 2);

        next = iterator.next();
        assertThat(next.getElement(), is(1));
        assertThat(next.getCount(), is(3));
        iterator.remove();
        assertMultisetState(multiset, 2, 2);

        next = iterator.next();
        assertThat(next.getElement(), is(2));
        assertThat(next.getCount(), is(2));
        iterator.remove();
        assertMultisetState(multiset);
    }

    @Test
    public void testIteratorRemove() {
        Multiset<Integer> multiset = createMultiset();
        multiset.add(1);
        multiset.add(2);
        multiset.add(1);

        Iterator<Integer> iterator = multiset.iterator();

        assertMultisetState(multiset, 1, 1, 2);
        removeAndAssertState(iterator, 1, multiset, 1, 2);
        removeAndAssertState(iterator, 1, multiset, 2);
        removeAndAssertState(iterator, 2, multiset);
    }

    private static void removeAndAssertState(
            Iterator<Integer> iterator,
            Integer elementToBeRemoved,
            Multiset<Integer> multiset,
            Integer... state
    ) {
        assertTrue(iterator.hasNext());
        assertThat(iterator.next(), is(elementToBeRemoved));
        iterator.remove();

        assertMultisetState(multiset, state);
    }

    @Test
    public void testIteratorRemoveComplex() {
        Multiset<Integer> multiset = createMultiset(1, 2, 3, 2, 1, 1);

        Iterator<Integer> iterator = multiset.iterator();

        assertMultisetState(multiset, 1, 1, 1, 2, 2, 3);
        removeAndAssertState(iterator, 1, multiset, 1, 1, 2, 2, 3);
        removeAndAssertState(iterator, 1, multiset, 1, 2, 2, 3);
        removeAndAssertState(iterator, 1, multiset, 2, 2, 3);
        removeAndAssertState(iterator, 2, multiset, 2, 3);
        removeAndAssertState(iterator, 2, multiset, 3);
        removeAndAssertState(iterator, 3, multiset);
    }

    @Test
    public void testRandomSet() {
        Multiset<Integer> multiset = createMultiset();
        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random(123);

        for (int i = 0; i < RANDOM_SET_SIZE; i++) {
            int anInt = random.nextInt();
            assertTrue(multiset.add(anInt));
            list.add(anInt);
        }

        assertThat(list.size(), is(RANDOM_SET_SIZE));
        for (Integer integer : list) {
            assertTrue(multiset.contains(integer));
            assertThat(multiset.count(integer), greaterThanOrEqualTo(1));

            assertTrue(multiset.remove(integer));
        }

        assertTrue(multiset.isEmpty());
        assertMultisetState(multiset);
    }

    @Test
    public void testRandomEntrySet() {
        Multiset<Integer> multiset = createMultiset();
        ArrayList<Integer> list = new ArrayList<>();
        Random random = new Random(123);

        for (int i = 0; i < RANDOM_SET_SIZE; i++) {
            int anInt = random.nextInt();
            assertTrue(multiset.add(anInt));
            list.add(anInt);
        }

        assertThat(list.size(), is(RANDOM_SET_SIZE));
        Set<Multiset.Entry<Integer>> entries = multiset.entrySet();
        assertThrows(UnsupportedOperationException.class, () -> entries.add(entries.iterator().next()));

        for (Integer integer : list) {
            Multiset.Entry<Integer> integerEntry = findEntry(entries, integer);
            assertThat(integerEntry.getCount(), is(countEqualsTo(list, integer)));
        }

        for (Multiset.Entry<Integer> entry : entries) {
            assertThat(entry.getCount(), is(countEqualsTo(list, entry.getElement())));
        }
    }

    @Test
    public void testIteratorRemoveWithoutNext() {
        final Multiset<Integer> multiset = createMultiset(1, 2);

        final Iterator<Integer> iterator = multiset.iterator();

        iterator.next();
        iterator.remove();
        assertThrows(IllegalStateException.class, () -> iterator.remove());
    }

    @Test
    public void testIteratorSameRemoveWithoutNext() {
        final Multiset<Integer> multiset = createMultiset(1, 1);

        final Iterator<Integer> iterator = multiset.iterator();

        iterator.next();
        iterator.remove();
        assertThrows(IllegalStateException.class, () -> iterator.remove());
    }

    @Test
    public void testIteratorNextIfHasNotNext() {
        final Multiset<Integer> multiset = createMultiset(1);

        final Iterator<Integer> iterator = multiset.iterator();

        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    public void testEntrySetIteratorRemoveWithoutNext() {
        final Set<Multiset.Entry<Integer>> entries = createMultiset(1, 1).entrySet();

        final Iterator<Multiset.Entry<Integer>> iterator = entries.iterator();

        iterator.next();
        iterator.remove();
        assertThrows(IllegalStateException.class, () -> iterator.remove());
    }

    @Test
    public void testEntrySetIteratorSameRemoveWithoutNext() {
        final Set<Multiset.Entry<Integer>> entries = createMultiset(1, 2).entrySet();

        final Iterator<Multiset.Entry<Integer>> iterator = entries.iterator();

        iterator.next();
        iterator.remove();
        assertThrows(IllegalStateException.class, () -> iterator.remove());
    }

    @Test
    public void testEntrySetIteratorNextIfHasNotNext() {
        final Set<Multiset.Entry<Integer>> entries = createMultiset(1).entrySet();

        final Iterator<Multiset.Entry<Integer>> iterator = entries.iterator();

        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    public void testIteratorNextNextRemoveNext() {
        final Multiset<Integer> ms = createMultiset(1, 1, 1);
        final Iterator<Integer> iterator = ms.iterator();
        final ArrayList<Integer> result = new ArrayList<>();
        result.add(iterator.next());
        result.add(iterator.next());
        iterator.remove();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }

        assertEquals(Arrays.asList(1, 1, 1), result);
    }

    @Test
    public void testNull() {
        Multiset<Integer> multiset = createMultiset(null, null, null);
        assertThat(multiset.count(null), is(3));

        Iterator<Integer> iterator = multiset.iterator();

        assertMultisetState(multiset, null, null, null);
        removeAndAssertState(iterator, null, multiset, null, null);
        removeAndAssertState(iterator, null, multiset, new Integer[]{null});
        removeAndAssertState(iterator, null, multiset);
    }

    @Test
    public void testIteratorNextRemoveSameElementBalance() {
        final Iterator<Integer> iterator = createMultiset(1, 1, 1, 1, 1, 1).iterator();

        iterator.next();
        iterator.next();
        iterator.next();
        iterator.next();

        iterator.remove();
        assertThrows(IllegalStateException.class, () -> iterator.remove());
    }

    @Test
    public void testComplexElementSet() {
        Multiset<Integer> multiset = createMultiset(1, 2, 3, 1, 3, 1);
        Set<Integer> elementSet = multiset.elementSet();
        assertThat(elementSet.size(), is(3));
        assertEquals(Arrays.asList(1, 2, 3), new ArrayList<>(elementSet));

        Iterator<Integer> iterator = elementSet.iterator();
        assertTrue(iterator.hasNext());
        assertThat(iterator.next(), is(1));
        iterator.remove();
        assertMultisetState(multiset, 2, 3, 3);
        assertThat(elementSet.size(), is(2));

        assertTrue(iterator.hasNext());
        assertThat(iterator.next(), is(2));
        iterator.remove();
        assertMultisetState(multiset, 3, 3);
        assertThat(elementSet.size(), is(1));

        assertTrue(iterator.hasNext());
        assertThat(iterator.next(), is(3));
        iterator.remove();
        assertMultisetState(multiset);
        assertThat(elementSet.size(), is(0));

        assertFalse(iterator.hasNext());
    }

    private Multiset.Entry<Integer> findEntry(Set<Multiset.Entry<Integer>> entries, Integer integer) {
        for (Multiset.Entry<Integer> entry : entries) {
            if (entry.getElement().equals(integer)) return entry;
        }

        fail("Can't find entry for " + integer);
        throw new IllegalStateException();
    }

    private static int countEqualsTo(Collection<Integer> collection, int element) {
        int result = 0;
        for (Integer integer : collection) {
            if (integer.equals(element)) {
                result++;
            }
        }

        return result;
    }

    @SafeVarargs
    private static <E> Multiset<E> createMultiset(E... data) {
        Multiset<E> multiset = createHashMultiset();

        for (E e : data) {
            assertTrue(multiset.add(e));
        }

        return multiset;
    }

    private static <E> Multiset<E> createHashMultiset() {
        throw new UnsupportedOperationException();
        // TODO: uncomment
        // return new HashMultiset<>();
    }
}

