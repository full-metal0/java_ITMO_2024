package ru.itmo.mit.trie;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TrieTest {
    Trie trie = new JustTrie();

    @Test
    public void testAdd() {
        assertTrue(trie.add("hello"));
        assertTrue(trie.add("world"));
    }

    @Test
    public void testContains() {
        trie.add("world");
        assertFalse(trie.contains("hello"));
        assertTrue(trie.contains("world"));
    }

    @Test
    public void testRemove() {
        trie.add("some");
        assertTrue(trie.remove("some"));
        assertFalse(trie.remove("some"));
        assertFalse(trie.contains("some"));
    }

    @Test
    public void testSize() {
        assertEquals(0, trie.size());
        trie.add("hello");
        assertEquals(1, trie.size());
        trie.add("world");
        assertEquals(2, trie.size());
        trie.remove("world");
        assertEquals(1, trie.size());
    }
}

