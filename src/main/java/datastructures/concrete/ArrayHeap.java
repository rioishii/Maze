package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;

/**
 * @see IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private int filledSize;
    private int size;

    // Feel free to add more fields and constants.

    public ArrayHeap() {
        this.heap = makeArrayOfT(10);
        this.size = 10;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int arraySize) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[arraySize]);
    }

    @Override
    public T removeMin() {
        if (this.filledSize == 0) {
            throw new EmptyContainerException();
        }
        T value = heap[0];
        heap[0] = heap[this.filledSize - 1];
        int parentIndex = 0;
        int childIndex = parentIndex + 1;
        int min = parentIndex;
        this.filledSize--;
        min = findMin(childIndex, min);
        while (parentIndex != min) {
            swap(parentIndex, min);
            parentIndex = min;
            childIndex = parentIndex * NUM_CHILDREN + 1;
            min = findMin(childIndex, min);
        }
        return value;
    }

    private int findMin(int childIndex, int min) {
        for (int i = childIndex; i < childIndex + NUM_CHILDREN && i < this.filledSize; i++) {
            if (heap[min].compareTo(heap[i]) > 0) {
                min = i;
            }
        }
        return min;
    }

    @Override
    public T peekMin() {
        if (this.isEmpty()) {
            throw new EmptyContainerException();
        }

        return heap[0];
    }

    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }

        if (this.filledSize == this.size) {
            this.size *= 2;
            T[] newHeap = makeArrayOfT(this.size);
            for (int i = 0; i < this.filledSize; i++) {
                newHeap[i] = this.heap[i];
            }

            this.heap = newHeap;
        }

        int childIndex = this.filledSize;
        int parentIndex = (this.filledSize - 1) / NUM_CHILDREN;
        this.heap[childIndex] = item;
        while (parentIndex >= 0 && this.heap[parentIndex].compareTo(this.heap[childIndex]) > 0) {
            swap(parentIndex, childIndex);
            childIndex = parentIndex;
            parentIndex = ((parentIndex - 1) / NUM_CHILDREN);
        }

        this.filledSize++;
    }

    @Override
    public int size() {
        return this.filledSize;
    }

    private void swap(int index1, int index2) {
        T temp = this.heap[index1];
        this.heap[index1] = this.heap[index2];
        this.heap[index2] = temp;
    }
}
