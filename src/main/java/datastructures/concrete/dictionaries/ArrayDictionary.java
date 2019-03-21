package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see datastructures.interfaces.IDictionary
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field.
    // We will be inspecting it in our private tests.
    private Pair<K, V>[] pairs;
    private int totalSize;
    private int filledSize;

    // You may add extra fields or helper methods though!

    public ArrayDictionary() {
        this.pairs = makeArrayOfPairs(10);
        this.totalSize = 10;
        this.filledSize = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    @Override
    public V get(K key) {
        for (int i = 0; i < this.filledSize; i++) {
            if (this.pairs[i].key == key || this.pairs[i].key.equals(key)) {
                return this.pairs[i].value;
            }
        }

        throw new NoSuchKeyException();
    }

    @Override
    public void put(K key, V value) {
        if (this.containsKey(key)) { // if key already exists
            for (int i = 0; i < this.filledSize; i++) {
                if ((key == null && this.pairs[i].key == null) || this.pairs[i].key.equals(key)) {
                    this.pairs[i] = new Pair<>(key, value);
                }
            }
        } else {
            if (this.filledSize == this.totalSize) { // if array is full
                this.totalSize *= 2; // make array two times bigger and fill it with old array
                Pair<K, V>[] newArray = makeArrayOfPairs(this.totalSize);
                for (int i = 0; i < filledSize; i++) {
                    newArray[i] = this.pairs[i];
                }

                this.pairs = newArray;
            }

            this.pairs[filledSize] = new Pair<>(key, value);
            this.filledSize++;
        }
    }

    @Override
    public V remove(K key) {
        for (int i = 0; i < this.filledSize; i++) {
            if (this.pairs[i].key == key || this.pairs[i].key.equals(key)) {
                V value = this.pairs[i].value;
                this.pairs[i] = this.pairs[this.filledSize - 1];
                this.filledSize--;
                return value;
            }
        }

        throw new NoSuchKeyException();
    }

    @Override
    public boolean containsKey(K key) {
        for (int i = 0; i < this.filledSize; i++) {
            if (this.pairs[i].key != null ? this.pairs[i].key.equals(key) : key == null) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int size() {
        return this.filledSize;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<>(this.pairs, 0, this.filledSize);
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        // Add any fields you need to store state information
        private Pair<K, V>[] pairs;
        private int index;
        private int size;

        public ArrayDictionaryIterator(Pair<K, V>[] pairs, int index, int size) {
            this.pairs = pairs;
            this.index = index;
            this.size = size;
        }

        public boolean hasNext() {
            return index < this.size;
        }

        public KVPair<K, V> next() {
            if (this.hasNext()) {
                KVPair<K, V> newPair = new KVPair<>(this.pairs[index].key, this.pairs[index].value);
                this.index++;
                return newPair;
            }

            throw new NoSuchElementException();
        }
    }
}

