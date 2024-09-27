package ru.itmo.mit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Map;
import java.util.Set;

public class DictionaryTest {

    @Test
    public void testSize() {
        Dictionary<String, Integer> dictionary = new JustDictionary<>();
        assertEquals(0, dictionary.size());
        dictionary.put("some1", 1);
        assertEquals(1, dictionary.size());
        dictionary.put("some2", 2);
        assertEquals(2, dictionary.size());
        dictionary.remove("some1");
        assertEquals(1, dictionary.size());
    }

    @Test
    public void testContainsKey() {
        Dictionary<String, Integer> dictionary = new JustDictionary<>();
        dictionary.put("some1", 1);
        assertTrue(dictionary.containsKey("some1"));
        assertFalse(dictionary.containsKey("some2"));
    }

    @Test
    public void testPutAndGet() {
        Dictionary<String, Integer> dictionary = new JustDictionary<>();
        assertNull(dictionary.put("some1", 1));
        assertEquals(Integer.valueOf(1), dictionary.get("some1"));
        assertEquals(Integer.valueOf(1), dictionary.put("some1", 2));
        assertEquals(Integer.valueOf(2), dictionary.get("some1"));
    }

    @Test
    public void testRemove() {
        Dictionary<String, Integer> dictionary = new JustDictionary<>();
        dictionary.put("some1", 1);
        assertEquals(Integer.valueOf(1), dictionary.remove("some1"));
        assertNull(dictionary.remove("some1"));
    }

    @Test
    public void testClear() {
        Dictionary<String, Integer> dictionary = new JustDictionary<>();
        dictionary.put("some1", 1);
        dictionary.clear();
        assertEquals(0, dictionary.size());
        assertFalse(dictionary.containsKey("some1"));
    }

    @Test
    public void testKeySet() {
        Dictionary<String, Integer> dictionary = new JustDictionary<>();
        dictionary.put("some1", 1);
        dictionary.put("some2", 2);
        assertTrue(dictionary.keySet().contains("some1"));
        assertTrue(dictionary.keySet().contains("some2"));
    }

    @Test
    public void testValues() {
        Dictionary<String, Integer> dictionary = new JustDictionary<>();
        dictionary.put("some1", 1);
        dictionary.put("some2", 2);
        assertTrue(dictionary.values().contains(1));
        assertTrue(dictionary.values().contains(2));
    }

    @Test
    public void testEntrySet() {
        Dictionary<String, Integer> dictionary = new JustDictionary<>();
        dictionary.put("some1", 1);
        dictionary.put("some2", 2);
        Set<Map.Entry<String, Integer>> entries = dictionary.entrySet();
        boolean key1Found = false, key2Found = false;

        for (Map.Entry<String, Integer> entry : entries) {
            if (entry.getKey().equals("some1") && entry.getValue().equals(1)) {
                key1Found = true;
            }
            if (entry.getKey().equals("some2") && entry.getValue().equals(2)) {
                key2Found = true;
            }
        }
        assertTrue(key1Found);
        assertTrue(key2Found);
    }
}
