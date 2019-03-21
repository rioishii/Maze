package datastructures.sorting;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import misc.BaseTest;
import misc.Sorter;
import org.junit.Test;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestArrayHeapAndSorterStress extends BaseTest {
    @Test(timeout=10*SECOND)
    public void testHeapInsertMany() {
        IPriorityQueue<Integer> heap = new ArrayHeap<>();
        int maxSize = 900000;
        for (int i = 0; i < maxSize; i++) {
            heap.insert(i);
        }

        assertEquals(maxSize, heap.size());
    }

    @Test(timeout=10*SECOND)
    public void testHeapInsertAndRemoveMany() {
        IPriorityQueue<Integer> heap = new ArrayHeap<>();
        int maxSize = 200000;
        for (int i = 0; i < maxSize; i++) {
            heap.insert(i);
        }

        assertEquals(maxSize, heap.size());
        for (int i = 0; i < maxSize; i++) {
            int min = heap.removeMin();
            assertEquals(i, min);
        }

        assertEquals(0, heap.size());
    }

    @Test(timeout=10*SECOND)
    public void testSorterStress() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 500000; i++) {
            list.add(i);
        }
        IList<Integer> top = Sorter.topKSort(50000, list);
        assertEquals(50000, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(i + 500000 - 50000, top.get(i));
        }
    }
}

