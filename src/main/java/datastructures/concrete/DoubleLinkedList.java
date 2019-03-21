package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods:
 * @see datastructures.interfaces.IList
 * (You should be able to control/command+click "IList" above to open the file from IntelliJ.)
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        if (this.front == null) {
            this.back = new Node<>(item);
            this.front = this.back;
        } else {
            this.back.next = new Node<>(item);
            this.back.next.prev = this.back;
            this.back = this.back.next;
        }
        this.size++;
    }

    @Override
    public T remove() {
        if (this.front == null) {
            throw new EmptyContainerException();
        }

        T value = this.back.data;
        this.back = this.back.prev;
        this.size--;
        if (this.size != 0) {
            this.back.next = null;
        } else {
            this.front = null;
        }
        return value;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }

        int counter = 0;
        Node<T> curr = this.front;
        while (counter != index) {
            counter++;
            curr = curr.next;
        }

        return curr.data;
    }

    @Override
    public void set(int index, T item) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException();
        }

        if (index == 0) {
            Node<T> temp = this.front.next;
            Node<T> newNode = new Node<>(item);
            newNode.next = temp;
            if (temp == null) {
                this.back = newNode;
            } else {
                temp.prev = newNode;
            }
            this.front = newNode;
        } else if (index == this.size - 1) {
            Node<T> temp = this.back.prev;
            Node<T> newNode = new Node<>(item);
            newNode.prev = temp;
            temp.next = newNode;
            this.back = newNode;
        } else {
            Node<T> curr = this.front;
            for (int i = 0; i < index - 1; i++) {
                curr = curr.next;
            }

            Node<T> newNode = new Node<>(item);
            newNode.next = curr.next.next;
            curr.next.next.prev = newNode;
            newNode.prev = curr;
            curr.next = newNode;
        }
    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 || index >= this.size + 1) {
            throw new IndexOutOfBoundsException();
        }

        if (index == this.size) {
            this.add(item);
        } else if (index == 0) {
            Node<T> temp = this.front;
            this.front = new Node<>(item);
            this.front.next = temp;
            temp.prev = this.front;
            this.size++;
        } else {
            Node<T> curr;
            if (index > this.size / 2) {
                curr = this.back;
                for (int i = this.size - 1; i > index - 1; i--) {
                    curr = curr.prev;
                }
            } else {
                curr = this.front;
                for (int i = 0; i < index - 1; i++) {
                    curr = curr.next;
                }
            }
            Node<T> newNode = new Node<>(item);
            Node<T> temp = curr.next;
            newNode.next = temp;
            newNode.prev = curr;
            temp.prev = newNode;
            curr.next = newNode;
            this.size++;
        }
    }

    @Override
    public T delete(int index) {
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }

        if (index == this.size - 1) {
            return this.remove();
        } else if (index == 0) {
            T value = this.front.data;
            this.front = this.front.next;
            this.front.prev = null;
            this.size--;
            return value;
        } else {
            Node<T> curr;
            if (index > this.size / 2) {
                curr = this.back;
                for (int i = this.size - 1; i > index; i--) {
                    curr = curr.prev;
                }
            } else {
                curr = this.front;
                for (int i = 0; i < index; i++) {
                    curr = curr.next;
                }
            }
            curr.prev.next = curr.next;
            curr.next.prev = curr.prev;
            this.size--;
            return curr.data;
        }
    }

    @Override
    public int indexOf(T item) {
        int index = 0;
        Node<T> curr = this.front;
        while (curr != null) {
            if ((curr.data == null && item == null) ||(curr.data != null && curr.data.equals(item))) {
                return index;
            }
            index++;
            curr = curr.next;
        }

        return -1;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(T other) {
        Node<T> curr = this.front;
        while (curr != null) {
            if ((curr.data == null && other == null) ||(curr.data != null && curr.data.equals(other))) {
                return true;
            }
            curr = curr.next;
        }

        return false;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return this.current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (this.hasNext()) {
                T value = current.data;
                this.current = current.next;
                return value;
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}