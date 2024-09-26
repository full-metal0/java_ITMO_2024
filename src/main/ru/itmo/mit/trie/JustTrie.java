package ru.itmo.mit.trie;

public class JustTrie implements Trie {
    TrieNode root;
    int size;

    JustTrie() {
        root = new TrieNode();
        size = 0;
    }

    @Override
    public boolean add(String element) {
        TrieNode current = root;
        boolean isNewWord = false;

        for(char ch : element.toCharArray()) {
            current.size++;
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }

        if (!current.isWordEnd) {
            current.isWordEnd = true;
            size++;
            isNewWord = true;
        }

        return isNewWord;
    }

    @Override
    public boolean contains(String element) {
        TrieNode current = root;

        for(char ch : element.toCharArray()) {
            current = current.children.get(ch);

            if (current == null) {
                return false;
            }
        }

        return current.isWordEnd;
    }

    @Override
    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }

        TrieNode current = root;
        for(char ch : element.toCharArray()) {
            TrieNode next = current.children.get(ch);

            if (next.size == 1) {
                current.children.remove(ch);
                size--;

                return true;
            }

            current.size--;
            current = next;
        }

        current.isWordEnd = false;
        size--;

        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        return 0;
    }

    @Override
    public String nextString(String element, int k) {
        return "";
    }
}
