package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;
import java.util.NoSuchElementException;

import java.util.Iterator;

/**
 * @see IDictionary and the assignment page for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    private int numPairs;

    // You're encouraged to add extra fields (and helper methods) though!

    public ChainedHashDictionary() {
        this.chains = makeArrayOfChains(10);
        this.numPairs = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    private int customHash(K key, int length) {
        int keyHash;
        if (key == null) {
            keyHash = 0;
        } else {
            keyHash = key.hashCode() % length;
            if (keyHash < 0) {
                keyHash = -keyHash;
            }
        }

        return keyHash;
    }

    @Override
    public V get(K key) {
        int keyHash = customHash(key, this.chains.length);
        if (this.chains[keyHash] != null) {
            return this.chains[keyHash].get(key);
        }

        throw new NoSuchKeyException();
    }

    @Override
    public void put(K key, V value) {
        boolean increasedPairs = false;
        int keyHash = customHash(key, this.chains.length);
        if (this.chains[keyHash] == null) {
            this.numPairs++;
            increasedPairs = true;
        } else if (!this.chains[keyHash].containsKey(key)) {
            this.numPairs++;
            increasedPairs = true;
        }
        if (increasedPairs) {
            double lambda = 1.0 * this.numPairs / this.chains.length;
            if (lambda >= 0.5) {
                resize();
                keyHash = customHash(key, this.chains.length);
            }
        }
        if (this.chains[keyHash] == null) {
            this.chains[keyHash] = new ArrayDictionary<>();
        }

        this.chains[keyHash].put(key, value);
    }

    private void resize() {
        int newLength = this.chains.length * 2;
        IDictionary<K, V>[] newChains = makeArrayOfChains(newLength);
        for (KVPair<K, V> pair: this) {
            int hashKey = customHash(pair.getKey(), newLength);
            if (newChains[hashKey] == null) {
                newChains[hashKey] = new ArrayDictionary<>();
            }
            newChains[hashKey].put(pair.getKey(), pair.getValue());
        }
        this.chains = newChains;
    }

    @Override
    public V remove(K key) {
        if (this.containsKey(key)) {
            int keyHash = customHash(key, this.chains.length);
            if (this.chains[keyHash] != null) {
                this.numPairs--;
                return this.chains[keyHash].remove(key);
            }
        }

        throw new NoSuchKeyException();
    }

    @Override
    public boolean containsKey(K key) {
        int keyHash = customHash(key, this.chains.length);
        if (this.chains[keyHash] != null) {
            return this.chains[keyHash].containsKey(key);
        }

        return false;
    }

    @Override
    public int size() {
        return this.numPairs;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be
     *    true once the constructor is done setting up the class AND
     *    must *always* be true both before and after you call any
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int index;
        private int size;
        private Iterator<KVPair<K, V>> itr;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            this.index = -1;
            this.size = this.chains.length;
            itr = null;
        }

        @Override
        public boolean hasNext() {
            while (itr == null || (!itr.hasNext() && this.index < this.size - 1)) {
                if (itr == null && this.index == this.size - 1) { // case where everything is empty
                    return false;
                }

                setItrHelper();
            }

            return itr.hasNext();
        }

        private void setItrHelper() {
            // set iterator to start at a chain that isn't null
            while (itr == null && this.index < this.size - 1) {
                this.index++;
                if (this.chains[index] != null) {
                    itr = this.chains[index].iterator();
                }
            }

            // if itr is at the end of a chain, set itr to be the iterator of the next non-null chain
            while ((itr != null && !itr.hasNext()) && this.index < this.size - 1) {
                index++;
                if (this.chains[index] != null) {
                    itr = this.chains[index].iterator();
                }
            }
        }

        @Override
        public KVPair<K, V> next() {
            if (this.hasNext()) {
                return itr.next();
            }

            throw new NoSuchElementException();
        }
    }
}
