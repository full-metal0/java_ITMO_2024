package ru.itmo.mit.trie;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    Map<Character, TrieNode> children;

    boolean isWordEnd;

    int size;

    TrieNode() {
        children = new HashMap<>();
        isWordEnd = false;
        size = 0;
    }
}
