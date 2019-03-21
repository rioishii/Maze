package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;

/**
 * @see IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.
    private IDictionary<T, Integer> itemMap;
    private int rep;

    public ArrayDisjointSet() {
        this.rep = 0;
        this.pointers = new int[0];
        this.itemMap = new ChainedHashDictionary<>();
    }

    @Override
    public void makeSet(T item) {
        if (this.itemMap.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        itemMap.put(item, this.rep);
        resize();
        this.pointers[this.rep] = -1;
        this.rep++;
    }

    private void resize() {
        int newLength = this.pointers.length + 1;
        int[] newPointers = new int[newLength];
        for (int i = 0; i < this.rep; i++) {
            newPointers[i] = this.pointers[i];
        }
        this.pointers = newPointers;
    }

    @Override
    public int findSet(T item) {
        if (!this.itemMap.containsKey(item)) {
            throw new IllegalArgumentException();
        }

        int rootRep = this.itemMap.get(item);
        while (this.pointers[rootRep] >= 0) {
            rootRep = this.pointers[rootRep];
        }

        int id = this.itemMap.get(item);
        while (this.pointers[id] >= 0) {
            this.pointers[id] = rootRep;
            id = this.pointers[id];
        }

        return rootRep;
    }

    @Override
    public void union(T item1, T item2) {
        if (!this.itemMap.containsKey(item1) || !this.itemMap.containsKey(item2)) {
            throw new IllegalArgumentException();
        }

        int rep1 = findSet(item1);
        int rep2 = findSet(item2);

        if (rep1 != rep2) {
            // item1 has higher rank than item2
            if (this.pointers[rep1] < this.pointers[rep2]) {
                this.pointers[rep2] = rep1;
            // item2 has higher rank than item1
            } else if (this.pointers[rep1] > this.pointers[rep2]) {
                this.pointers[rep1] = rep2;
            // equal rank
            } else {
                this.pointers[rep2] = rep1;
                this.pointers[rep1] -= 1;
            }
        }
    }
}
