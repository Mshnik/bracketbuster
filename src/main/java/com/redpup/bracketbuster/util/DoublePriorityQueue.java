package com.redpup.bracketbuster.util;

/*
 * Copyright (c) 2003, 2018, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.DoublePredicate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * A branch of {@link java.util.PriorityQueue} specialized for primitive doubles.
 */
public final class DoublePriorityQueue {

  private static final int DEFAULT_INITIAL_CAPACITY = 11;

  /**
   * Priority queue represented as a balanced binary heap: the two children of queue[n] are
   * queue[2*n+1] and queue[2*(n+1)].  The priority queue is ordered by comparator, or by the
   * elements' natural ordering, if comparator is null: For each node n in the heap and each
   * descendant d of n, n <= d.  The element with the lowest value is in queue[0], assuming the
   * queue is nonempty.
   */
  transient double[] queue; // non-private to simplify nested class access

  /**
   * Whether a value at queue[i] is valid. Set to false when a value is cleared. (This replaces
   * setting the value to null.)
   */
  transient boolean[] hasValue; // non-private to simplify nested class access

  /**
   * The number of elements in the priority queue.
   */
  int size;

  /**
   * A comparator on doubles.
   */
  @FunctionalInterface
  public interface DoubleComparator {

    /**
     * Compares the two doubles.
     *
     * @return < 0 if {@code d1 < d2}, > 0 if {@code d1 > d2}, or 0 if {@code d1 == d2}.
     */
    public int compare(double d1, double d2);

    /**
     * Returns a {@link DoubleComparator} that compares doubles in natural order (Ascending).
     */
    public static DoubleComparator natural() {
      return Double::compare;
    }

    /**
     * Returns a {@link DoubleComparator} that compares doubles in ascending order.
     */
    public static DoubleComparator ascending() {
      return natural();
    }

    /**
     * Returns a {@link DoubleComparator} that compares doubles in descending order.
     */
    public static DoubleComparator descending() {
      return (d1, d2) -> Double.compare(d2, d1);
    }
  }

  /**
   * The comparator, or null if priority queue uses elements' natural ordering.
   */
  private final DoubleComparator comparator;

  /**
   * The number of times this priority queue has been
   * <i>structurally modified</i>.  See AbstractList for gory details.
   */
  transient int modCount;     // non-private to simplify nested class access

  /**
   * Creates a {@code DoublePriorityQueue} with the default initial capacity (11) that orders its
   * elements according to their {@linkplain Comparable natural ordering}.
   */
  public DoublePriorityQueue() {
    this(DEFAULT_INITIAL_CAPACITY, DoubleComparator.natural());
  }

  /**
   * Creates a {@code DoublePriorityQueue} with the specified initial capacity that orders its
   * elements according to their {@linkplain Comparable natural ordering}.
   *
   * @param initialCapacity the initial capacity for this priority queue
   * @throws IllegalArgumentException if {@code initialCapacity} is less than 1
   */
  public DoublePriorityQueue(int initialCapacity) {
    this(initialCapacity, DoubleComparator.natural());
  }

  /**
   * Creates a {@code DoublePriorityQueue} with the default initial capacity and whose elements are
   * ordered according to the specified comparator.
   *
   * @param comparator the comparator that will be used to order this priority queue.  If {@code
   * null}, the {@linkplain Comparable natural ordering} of the elements will be used.
   * @since 1.8
   */
  public DoublePriorityQueue(DoubleComparator comparator) {
    this(DEFAULT_INITIAL_CAPACITY, comparator);
  }

  /**
   * Creates a {@code DoublePriorityQueue} with the specified initial capacity that orders its
   * elements according to the specified comparator.
   *
   * @param initialCapacity the initial capacity for this priority queue
   * @param comparator the comparator that will be used to order this priority queue.  If {@code
   * null}, the {@linkplain Comparable natural ordering} of the elements will be used.
   * @throws IllegalArgumentException if {@code initialCapacity} is less than 1
   */
  public DoublePriorityQueue(int initialCapacity,
      DoubleComparator comparator) {
    // Note: This restriction of at least one is not actually needed,
    // but continues for 1.5 compatibility
    if (initialCapacity < 1) {
      throw new IllegalArgumentException();
    }
    this.queue = new double[initialCapacity];
    this.hasValue = new boolean[initialCapacity];
    this.comparator = comparator;
  }

  /**
   * Ensures that queue[0] exists, helping peek() and poll().
   */
  private static double[] ensureNonEmpty(double[] es) {
    return (es.length > 0) ? es : new double[1];
  }

  /**
   * The maximum size of array to allocate. Some VMs reserve some header words in an array. Attempts
   * to allocate larger arrays may result in OutOfMemoryError: Requested array size exceeds VM
   * limit
   */
  private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

  /**
   * Increases the capacity of the array.
   *
   * @param minCapacity the desired minimum capacity
   */
  private void grow(int minCapacity) {
    int oldCapacity = queue.length;
    // Double size if small; else grow by 50%
    int newCapacity = oldCapacity + ((oldCapacity < 64) ?
        (oldCapacity + 2) :
        (oldCapacity >> 1));
    // overflow-conscious code
    if (newCapacity - MAX_ARRAY_SIZE > 0) {
      newCapacity = hugeCapacity(minCapacity);
    }
    queue = Arrays.copyOf(queue, newCapacity);
    hasValue = Arrays.copyOf(hasValue, newCapacity);
  }

  private static int hugeCapacity(int minCapacity) {
    if (minCapacity < 0) // overflow
    {
      throw new OutOfMemoryError();
    }
    return (minCapacity > MAX_ARRAY_SIZE) ?
        Integer.MAX_VALUE :
        MAX_ARRAY_SIZE;
  }

  /**
   * Inserts the specified element into this priority queue.
   *
   * @return {@code true} (as specified by {@link Collection#add})
   * @throws ClassCastException if the specified element cannot be compared with elements currently
   * in this priority queue according to the priority queue's ordering
   * @throws NullPointerException if the specified element is null
   */
  public boolean add(double e) {
    return offer(e);
  }

  /**
   * Adds all elements in {@code c} to this queue.
   */
  public void addAll(Collection<Double> c) {
    c.forEach(this::add);
  }

  /**
   * Inserts the specified element into this priority queue.
   *
   * @return {@code true} (as specified by {@link Queue#offer})
   */
  public boolean offer(double e) {
    modCount++;
    int i = size;
    if (i >= queue.length) {
      grow(i + 1);
    }
    siftUp(i, e);
    size = i + 1;
    return true;
  }

  /**
   * Returns the first value in the queue, without removing it.
   */
  public double peek() {
    if (!hasValue[0]) {
      throw new NoSuchElementException();
    }
    return queue[0];
  }

  private int indexOf(double d) {
    final double[] es = queue;
    for (int i = 0, n = size; i < n; i++) {
      if (hasValue[i] && d == es[i]) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Removes a single instance of the specified element from this queue, if it is present.  More
   * formally, removes an element {@code e} such that {@code o.equals(e)}, if this queue contains
   * one or more such elements.  Returns {@code true} if and only if this queue contained the
   * specified element (or equivalently, if this queue changed as a result of the call).
   *
   * @param d element to be removed from this queue, if present
   * @return {@code true} if this queue changed as a result of the call
   */
  public boolean remove(double d) {
    int i = indexOf(d);
    if (i == -1) {
      return false;
    } else {
      removeAt(i);
      return true;
    }
  }

  /**
   * Identity-based version for use in Itr.remove.
   *
   * @param d element to be removed from this queue, if present
   */
  void removeEq(double d) {
    final double[] es = queue;
    for (int i = 0, n = size; i < n; i++) {
      if (hasValue[i] && d == es[i]) {
        removeAt(i);
        break;
      }
    }
  }

  /**
   * Returns {@code true} if this queue contains the specified element. More formally, returns
   * {@code true} if and only if this queue contains at least one element {@code e} such that {@code
   * o.equals(e)}.
   *
   * @param d double to be checked for containment in this queue
   * @return {@code true} if this queue contains the specified element
   */
  public boolean contains(double d) {
    return indexOf(d) >= 0;
  }

  /**
   * Returns true iff this queue is empty.
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Returns the size of this queue.
   */
  public int size() {
    return size;
  }

  /**
   * Removes all of the elements from this priority queue. The queue will be empty after this call
   * returns.
   */
  public void clear() {
    modCount++;
    final double[] es = queue;
    for (int i = 0, n = size; i < n; i++) {
      es[i] = 0;
      hasValue[i] = false;
    }
    size = 0;
  }

  /**
   * Drains the contents of this priority queue into the returned stream, in order. The queue will
   * be empty after this call.
   */
  public DoubleStream drain() {
    DoubleStream.Builder builder = DoubleStream.builder();
    while (!isEmpty()) {
      builder.add(poll());
    }
    return builder.build();
  }

  /** Drains the contents of this priority queue into the returned list, in order. The queue will be empty after this call. */
  public List<Double> drainToList() {
    return drain().boxed().collect(Collectors.toList());
  }

  /**
   * Returns the front value from the queue and removes it. Throws an exception if the queue is
   * empty.
   */
  public double poll() {
    if (!hasValue[0]) {
      throw new NoSuchElementException();
    }

    final double[] es = queue;
    final double result = es[0];
    modCount++;
    final int n;
    final double x = es[(n = --size)];
    es[n] = 0;
    hasValue[n] = false;
    if (n > 0) {
      siftDownUsingComparator(0, x, es, n, comparator);
    }
    return result;
  }

  /**
   * Removes the ith element from queue.
   *
   * Normally this method leaves the elements at up to i-1, inclusive, untouched.  Under these
   * circumstances, it returns null.  Occasionally, in order to maintain the heap invariant, it must
   * swap a later element of the list with one earlier than i.  Under these circumstances, this
   * method returns the element that was previously at the end of the list and is now at some
   * position before i. This fact is used by iterator.remove so as to avoid missing traversing
   * elements.
   */
  void removeAt(int i) {
    // assert i >= 0 && i < size;
    final double[] es = queue;
    modCount++;
    int s = --size;
    // removed last element
    if (s == i) {
      es[i] = 0;
      hasValue[i] = false;
    } else {
      double moved = es[s];
      es[s] = 0;
      hasValue[s] = false;
      siftDown(i, moved);
      if (es[i] == moved) {
        siftUp(i, moved);
      }
    }
  }

  /**
   * Inserts item x at position k, maintaining heap invariant by promoting x up the tree until it is
   * greater than or equal to its parent, or is the root.
   *
   * To simplify and speed up coercions and comparisons, the Comparable and Comparator versions are
   * separated into different methods that are otherwise identical. (Similarly for siftDown.)
   *
   * @param k the position to fill
   * @param x the item to insert
   */
  private void siftUp(int k, double x) {
    siftUpUsingComparator(k, x, queue, hasValue, comparator);
  }

  private static void siftUpUsingComparator(
      int k, double x, double[] es, boolean[] hasValue, DoubleComparator cmp) {
    while (k > 0) {
      int parent = (k - 1) >>> 1;
      double e = es[parent];
      if (cmp.compare(x, e) >= 0) {
        break;
      }
      es[k] = e;
      hasValue[k] = true;
      k = parent;
    }
    es[k] = x;
    hasValue[k] = true;
  }

  /**
   * Inserts item x at position k, maintaining heap invariant by demoting x down the tree repeatedly
   * until it is less than or equal to its children or is a leaf.
   *
   * @param k the position to fill
   * @param x the item to insert
   */
  private void siftDown(int k, double x) {
    siftDownUsingComparator(k, x, queue, size, comparator);
  }

  private static void siftDownUsingComparator(
      int k, double x, double[] es, int n, DoubleComparator cmp) {
    // assert n > 0;
    int half = n >>> 1;
    while (k < half) {
      int child = (k << 1) + 1;
      double c = es[child];
      int right = child + 1;
      if (right < n && cmp.compare(c, es[right]) > 0) {
        c = es[child = right];
      }
      if (cmp.compare(x, c) <= 0) {
        break;
      }
      es[k] = c;
      k = child;
    }
    es[k] = x;
  }

  /**
   * Establishes the heap invariant (described above) in the entire tree, assuming nothing about the
   * order of the elements prior to the call. This classic algorithm due to Floyd (1964) is known to
   * be O(size).
   */
  private void heapify() {
    final double[] es = queue;
    int n = size, i = (n >>> 1) - 1;
    for (; i >= 0; i--) {
      siftDownUsingComparator(i, (double) es[i], es, n, comparator);
    }
  }

  /**
   * Returns the comparator used to order the elements in this queue, or {@code null} if this queue
   * is sorted according to the {@linkplain Comparable natural ordering} of its elements.
   *
   * @return the comparator used to order this queue, or {@code null} if this queue is sorted
   * according to the natural ordering of its elements
   */
  public DoubleComparator comparator() {
    return comparator;
  }

  /**
   * @throws NullPointerException {@inheritDoc}
   */
  public boolean removeIf(DoublePredicate filter) {
    Objects.requireNonNull(filter);
    return bulkRemove(filter);
  }

  /**
   * @throws NullPointerException {@inheritDoc}
   */
  public boolean removeAll(Collection<Double> c) {
    Objects.requireNonNull(c);
    return bulkRemove(c::contains);
  }

  /**
   * @throws NullPointerException {@inheritDoc}
   */
  public boolean retainAll(Collection<Double> c) {
    Objects.requireNonNull(c);
    return bulkRemove(e -> !c.contains(e));
  }

  // A tiny bit set implementation

  private static long[] nBits(int n) {
    return new long[((n - 1) >> 6) + 1];
  }

  private static void setBit(long[] bits, int i) {
    bits[i >> 6] |= 1L << i;
  }

  private static boolean isClear(long[] bits, int i) {
    return (bits[i >> 6] & (1L << i)) == 0;
  }

  /**
   * Implementation of bulk remove methods.
   */
  private boolean bulkRemove(DoublePredicate filter) {
    final int expectedModCount = ++modCount;
    final double[] es = queue;
    final int end = size;
    int i;
    // Optimize for initial run of survivors
    for (i = 0; i < end && !filter.test((double) es[i]); i++) {
      ;
    }
    if (i >= end) {
      if (modCount != expectedModCount) {
        throw new ConcurrentModificationException();
      }
      return false;
    }
    // Tolerate predicates that reentrantly access the collection for
    // read (but writers still get CME), so traverse once to find
    // elements to delete, a second pass to physically expunge.
    final int beg = i;
    final long[] deathRow = nBits(end - beg);
    deathRow[0] = 1L;   // set bit 0
    for (i = beg + 1; i < end; i++) {
      if (filter.test(es[i])) {
        setBit(deathRow, i - beg);
      }
    }
    if (modCount != expectedModCount) {
      throw new ConcurrentModificationException();
    }
    int w = beg;
    for (i = beg; i < end; i++) {
      if (isClear(deathRow, i - beg)) {
        es[w++] = es[i];
      }
    }
    for (i = size = w; i < end; i++) {
      es[i] = 0;
      hasValue[i] = false;
    }
    heapify();
    return true;
  }

  /**
   * @throws NullPointerException {@inheritDoc}
   */
  public void forEach(DoubleConsumer action) {
    Objects.requireNonNull(action);
    final int expectedModCount = modCount;
    final double[] es = queue;
    for (int i = 0, n = size; i < n; i++) {
      action.accept(es[i]);
    }
    if (expectedModCount != modCount) {
      throw new ConcurrentModificationException();
    }
  }
}

