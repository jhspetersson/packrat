package io.github.jhspetersson.packrat;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

/**
 * A fixed-size deque implementation that automatically removes the oldest element
 * when the maximum size is reached and a new element is added.
 *
 * @param <E> the type of elements held in this deque
 * @author jhspetersson
 */
public class FixedSizeDeque<E> implements Deque<E> {
    private final Deque<E> delegate;
    private final int maxSize;

    /**
     * Creates a new fixed-size deque with the specified maximum size.
     *
     * @param maxSize the maximum size of the deque
     */
    public FixedSizeDeque(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive");
        }

        this.maxSize = maxSize;
        this.delegate = new ArrayDeque<>(maxSize);
    }

    @Override
    public boolean add(E e) {
        if (delegate.size() >= maxSize) {
            delegate.removeFirst();
        }

        return delegate.add(e);
    }

    @Override
    public void addFirst(E e) {
        if (delegate.size() >= maxSize) {
            delegate.removeLast();
        }

        delegate.addFirst(e);
    }

    @Override
    public void addLast(E e) {
        if (delegate.size() >= maxSize) {
            delegate.removeFirst();
        }

        delegate.addLast(e);
    }

    @Override
    public boolean offer(E e) {
        if (delegate.size() >= maxSize) {
            delegate.removeFirst();
        }

        return delegate.offer(e);
    }

    @Override
    public boolean offerFirst(E e) {
        if (delegate.size() >= maxSize) {
            delegate.removeLast();
        }

        return delegate.offerFirst(e);
    }

    @Override
    public boolean offerLast(E e) {
        if (delegate.size() >= maxSize) {
            delegate.removeFirst();
        }

        return delegate.offerLast(e);
    }

    @Override
    public E removeFirst() {
        return delegate.removeFirst();
    }

    @Override
    public E removeLast() {
        return delegate.removeLast();
    }

    @Override
    public E pollFirst() {
        return delegate.pollFirst();
    }

    @Override
    public E pollLast() {
        return delegate.pollLast();
    }

    @Override
    public E getFirst() {
        return delegate.getFirst();
    }

    @Override
    public E getLast() {
        return delegate.getLast();
    }

    @Override
    public E peekFirst() {
        return delegate.peekFirst();
    }

    @Override
    public E peekLast() {
        return delegate.peekLast();
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        return delegate.removeFirstOccurrence(o);
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        return delegate.removeLastOccurrence(o);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public void push(E e) {
        addFirst(e);
    }

    @Override
    public E pop() {
        return removeFirst();
    }

    @Override
    public boolean remove(Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public Iterator<E> iterator() {
        return delegate.iterator();
    }

    @Override
    public Iterator<E> descendingIterator() {
        return delegate.descendingIterator();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public E remove() {
        return delegate.remove();
    }

    @Override
    public E poll() {
        return delegate.poll();
    }

    @Override
    public E element() {
        return delegate.element();
    }

    @Override
    public E peek() {
        return delegate.peek();
    }
}