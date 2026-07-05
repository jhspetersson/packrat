package io.github.jhspetersson.packrat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A FIFO buffer backed by a circular {@code Object} array with a fixed maximum size.
 * Permits {@code null} elements. The backing array grows lazily up to the maximum size.
 *
 * @param <E> the type of elements held in this buffer
 * @author jhspetersson
 */
class RingBuffer<E> implements Iterable<E> {
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    private static final int INITIAL_CAPACITY = 16;

    private final long maxSize;
    private Object[] elements;
    private int head;
    private int size;

    /**
     * Creates a new ring buffer with the specified maximum size.
     *
     * @param maxSize the maximum size of the buffer
     */
    RingBuffer(long maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }

        this.maxSize = maxSize;
        this.elements = new Object[(int) Math.min(maxSize, INITIAL_CAPACITY)];
    }

    boolean isFull() {
        return size >= maxSize;
    }

    int size() {
        return size;
    }

    /**
     * Appends an element to the end of the buffer.
     * The caller must remove the first element beforehand when the buffer is full.
     */
    void add(E element) {
        if (size == elements.length) {
            grow();
        }
        elements[(head + size) % elements.length] = element;
        size++;
    }

    @SuppressWarnings("unchecked")
    E removeFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        var element = (E) elements[head];
        elements[head] = null;
        head = (head + 1) % elements.length;
        size--;
        return element;
    }

    @SuppressWarnings("unchecked")
    List<E> toList() {
        var list = new ArrayList<E>(size);
        for (var i = 0; i < size; i++) {
            list.add((E) elements[(head + i) % elements.length]);
        }
        return list;
    }

    private void grow() {
        var limit = Math.min(maxSize, MAX_ARRAY_SIZE);
        if (elements.length >= limit) {
            throw new IllegalStateException("buffer is full");
        }
        var newCapacity = (int) Math.min(Math.max(elements.length * 2L, INITIAL_CAPACITY), limit);
        var newElements = new Object[newCapacity];
        var firstSegment = Math.min(size, elements.length - head);
        System.arraycopy(elements, head, newElements, 0, firstSegment);
        System.arraycopy(elements, 0, newElements, firstSegment, size - firstSegment);
        elements = newElements;
        head = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int position;

            @Override
            public boolean hasNext() {
                return position < size;
            }

            @Override
            @SuppressWarnings("unchecked")
            public E next() {
                if (position >= size) {
                    throw new NoSuchElementException();
                }
                return (E) elements[(head + position++) % elements.length];
            }
        };
    }
}
