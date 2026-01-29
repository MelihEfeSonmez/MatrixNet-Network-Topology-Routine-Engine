import java.util.ArrayList;

public class MyHashMap<K, V> {

    // ---INNER CLASS---
    // Represents single hash table entry
    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;
        final int hash; // Cached hash value

        Entry(int hash, K key, V value, Entry<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    // ---DATA FIELDS---
    private Entry<K, V>[] table;
    private int size;
    private int threshold; // capacity * loadFactor

    // Constants
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    // ---CONSTRUCTORS---
    @SuppressWarnings("unchecked") // Prevents compiler warnings
    public MyHashMap() {
        int capacity = DEFAULT_INITIAL_CAPACITY;
        this.threshold = (int) (capacity * LOAD_FACTOR);
        this.table = new Entry[capacity];
    }

    // ---METHODS---
    // Checks if key exists in the map
    public boolean containsKey(K key) {
        return getEntry(key) != null;
    }

    // Inserts an entry to hash map
    public void put(K key, V value) {
        // Hash computation
        int hash = (key == null) ? 0 : key.hashCode();
        hash = hash ^ (hash >>> 16);

        int i = (table.length - 1) & hash;

        // Iterates bucket
        for (Entry<K, V> entry = table[i]; entry != null; entry = entry.next) {
            if (entry.hash == hash && ((entry.key == key) || (key != null && key.equals(entry.key)))) {
                entry.value = value;
                return;
            }
        }

        // Adds entry to head of bucket
        table[i] = new Entry<>(hash, key, value, table[i]);

        // Checks if it should be resized
        if (++size > threshold) {
            resize();
        }
    }

    // Returns value of the key
    public V get(K key) {
        Entry<K, V> entry = getEntry(key);
        return (entry == null) ? null : entry.value;
    }

    // Removes the entry
    public V remove(K key) {
        int hash = (key == null) ? 0 : key.hashCode();
        hash = hash ^ (hash >>> 16);

        int i = (table.length - 1) & hash;

        Entry<K, V> prev = null;
        Entry<K, V> entry = table[i];

        // Searches for key in this bucket
        while (entry != null) {
            if (entry.hash == hash && ((entry.key == key) || (key != null && key.equals(entry.key)))) {
                V value = entry.value;
                if (prev == null)
                    table[i] = entry.next;
                else
                    prev.next = entry.next;
                size--;
                return value;
            }
            prev = entry;
            entry = entry.next;
        }
        return null;
    }

    // Returns all keys
    public ArrayList<K> keySet() {
        ArrayList<K> keys = new ArrayList<>(size);
        if (size > 0) {
            // Traverses all buckets
            for (int i = 0; i < table.length; i++) {
                for (Entry<K, V> entry = table[i]; entry != null; entry = entry.next) {
                    keys.add(entry.key); // Adds keys to list
                }
            }
        }
        return keys;
    }

    // Returns all values
    public ArrayList<V> valueSet() {
        ArrayList<V> values = new ArrayList<>(size);
        if (size > 0) {
            // Traverses all buckets
            for (int i = 0; i < table.length; i++) {
                for (Entry<K, V> entry = table[i]; entry != null; entry = entry.next) {
                    values.add(entry.value); // Adds values to list
                }
            }
        }
        return values;
    }

    // ---GENERAL HELPERS---
    // Finds entry object
    private Entry<K, V> getEntry(K key) {
        if (size == 0) return null;

        int hash = (key == null) ? 0 : key.hashCode();
        hash = hash ^ (hash >>> 16);

        int i = (table.length - 1) & hash;

        for (Entry<K, V> entry = table[i]; entry != null; entry = entry.next) {
            if (entry.hash == hash && ((entry.key == key) || (key != null && key.equals(entry.key)))) {
                return entry;
            }
        }
        return null;
    }

    // Resizes the table and rehashes
    @SuppressWarnings("unchecked") // Prevents compiler warnings
    private void resize() {
        // Stores old data
        Entry<K, V>[] oldTable = table;
        int oldCapacity = oldTable.length;

        // Bit shift and creates new table
        int newCapacity = oldCapacity << 1;
        Entry<K, V>[] newTable = new Entry[newCapacity];
        this.threshold = (int) (newCapacity * LOAD_FACTOR);
        this.table = newTable;

        // Rehashes and moves all entries
        for (int j = 0; j < oldCapacity; j++) {
            Entry<K, V> entry = oldTable[j];
            if (entry != null) {
                oldTable[j] = null; // GC yardımı

                do {
                    Entry<K, V> next = entry.next; // Saves next entry

                    // // Computes bucket index
                    int i = (newCapacity - 1) & entry.hash;

                    entry.next = newTable[i]; // Inserts head of new bucket
                    newTable[i] = entry;

                    entry = next;
                } while (entry != null);
            }
        }
    }

}
