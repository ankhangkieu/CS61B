package lab9;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

public class MyHashMap<K, V> implements Map61B<K, V> {
    private class Entry {
        K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private int numOfBuckets = 16;
    private double loadFactor = .75;
    private int size = 0;
    private ArrayList<ArrayList<Entry>> buckets;
    private HashSet<K> keySet;

    public MyHashMap() {
        this.buckets = new ArrayList<>();
        for (int i = 0; i < numOfBuckets; i++) {
            this.buckets.add(new ArrayList<Entry>());
        }
        this.keySet = new HashSet<K>();
    }

    public MyHashMap(int initialSize) {
        this.numOfBuckets = initialSize;
        this.buckets = new ArrayList<>();
        for (int i = 0; i < numOfBuckets; i++) {
            this.buckets.add(new ArrayList<Entry>());
        }
        this.keySet = new HashSet<K>();
    }

    public MyHashMap(int initialSize, double loadFactor) {
        this.loadFactor = loadFactor;
        this.numOfBuckets = initialSize;
        this.buckets = new ArrayList<>();
        for (int i = 0; i < numOfBuckets; i++) {
            this.buckets.add(new ArrayList<Entry>());
        }
        this.keySet = new HashSet<K>();
    }

    @Override
    public void clear() {
        this.buckets = new ArrayList<>();
        this.numOfBuckets = 16;
        this.keySet = new HashSet<K>();
        this.size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return this.keySet.contains(key);
    }

    @Override
    public V get(K key) {
        if (!containsKey(key)) {
            return null;
        }
        int bucketNum = getHash(key, numOfBuckets);
        for (Entry entry : this.buckets.get(bucketNum)) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }

    private int getHash(K key, int capacity) {
        return Math.floorMod(key.hashCode(), capacity);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (containsKey(key)) {
            modifyValue(key, value);
            return;
        }
        if (size() >= (loadFactor * this.numOfBuckets)) {
            resize(numOfBuckets * 2);
        }
        int rightBucket = getHash(key, this.numOfBuckets);
        this.buckets.get(rightBucket).add(new Entry(key, value));
        this.keySet.add(key);
        this.size += 1;
    }

    private void modifyValue(K key, V value) {
        int bucketNum = getHash(key, this.numOfBuckets);
        for (Entry entry : this.buckets.get(bucketNum)) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }
    }

    private void resize(int capacity) {
        ArrayList<ArrayList<Entry>> newBuckets = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            newBuckets.add(new ArrayList<Entry>());
        }
        Iterator<K> iter = iterator();
        while (iter.hasNext()) {
            K key = iter.next();
            int hash = getHash(key, capacity);
            Entry newEntry = new Entry(key, get(key));
            newBuckets.get(hash).add(newEntry);
        }
        this.numOfBuckets = capacity;
        this.buckets = newBuckets;
    }

    @Override
    public Set<K> keySet() {
        return keySet;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return keySet.iterator();
    }
}
