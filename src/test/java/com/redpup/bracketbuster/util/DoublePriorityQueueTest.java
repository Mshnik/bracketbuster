package com.redpup.bracketbuster.util;

import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

import com.google.common.collect.ImmutableList;
import com.redpup.bracketbuster.util.DoublePriorityQueue.DoubleComparator;
import java.util.NoSuchElementException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DoublePriorityQueueTest {

  @Test
  public void empty() {
    DoublePriorityQueue queue = new DoublePriorityQueue();

    assertThat(queue.isEmpty()).isTrue();
    assertThat(queue.size()).isEqualTo(0);
    assertThat(queue.drainToList()).isEmpty();
  }

  @Test
  public void oneElement() {
    DoublePriorityQueue queue = new DoublePriorityQueue();

    queue.add(1.0);

    assertThat(queue.isEmpty()).isFalse();
    assertThat(queue.size()).isEqualTo(1);
    assertThat(queue.drainToList()).containsExactly(1.0);
  }

  @Test
  public void twoElements_naturalOrder_inOrder() {
    DoublePriorityQueue queue = new DoublePriorityQueue();

    queue.add(1.0);
    queue.add(2.0);

    assertThat(queue.isEmpty()).isFalse();
    assertThat(queue.size()).isEqualTo(2);
    assertThat(queue.drainToList()).containsExactly(1.0, 2.0).inOrder();
  }


  @Test
  public void twoElements_naturalOrder_reverseOrder() {
    DoublePriorityQueue queue = new DoublePriorityQueue();

    queue.add(2.0);
    queue.add(1.0);

    assertThat(queue.isEmpty()).isFalse();
    assertThat(queue.size()).isEqualTo(2);
    assertThat(queue.drainToList()).containsExactly(1.0, 2.0).inOrder();
  }

  @Test
  public void twoElements_descendingOrder_inOrder() {
    DoublePriorityQueue queue = new DoublePriorityQueue(DoubleComparator.descending());

    queue.add(2.0);
    queue.add(1.0);

    assertThat(queue.isEmpty()).isFalse();
    assertThat(queue.size()).isEqualTo(2);
    assertThat(queue.drainToList()).containsExactly(2.0, 1.0).inOrder();
  }


  @Test
  public void twoElements_descendingOrder_reverseOrder() {
    DoublePriorityQueue queue = new DoublePriorityQueue(DoubleComparator.descending());

    queue.add(1.0);
    queue.add(2.0);

    assertThat(queue.isEmpty()).isFalse();
    assertThat(queue.size()).isEqualTo(2);
    assertThat(queue.drainToList()).containsExactly(2.0, 1.0).inOrder();
  }

  @Test
  public void manyElements() {
    DoublePriorityQueue queue = new DoublePriorityQueue();

    queue.add(2.0);
    queue.add(1.0);
    queue.add(3.0);
    queue.add(7.0);
    queue.add(5.0);
    queue.add(4.0);
    queue.add(6.0);

    assertThat(queue.isEmpty()).isFalse();
    assertThat(queue.size()).isEqualTo(7);
    assertThat(queue.drainToList()).containsExactly(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0).inOrder();
  }

  @Test
  public void addAll() {
    DoublePriorityQueue queue = new DoublePriorityQueue();

    queue.addAll(ImmutableList.of(2.0, 1.0, 3.0, 7.0, 5.0, 4.0, 6.0));

    assertThat(queue.isEmpty()).isFalse();
    assertThat(queue.size()).isEqualTo(7);
    assertThat(queue.drainToList()).containsExactly(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0).inOrder();
  }

  @Test
  public void peek_returnsFront() {
    DoublePriorityQueue queue = new DoublePriorityQueue();

    queue.add(1.0);

    assertThat(queue.peek()).isEqualTo(1.0);
    assertThat(queue.peek()).isEqualTo(1.0);

    assertThat(queue.isEmpty()).isFalse();
    assertThat(queue.size()).isEqualTo(1);
  }

  @Test
  public void peek_throwsOnEmpty() {
    DoublePriorityQueue queue = new DoublePriorityQueue();

    assertThrows(NoSuchElementException.class, queue::peek);
  }

  @Test
  public void poll_returnsFrontAndRemoves() {
    DoublePriorityQueue queue = new DoublePriorityQueue();

    queue.add(2.0);
    queue.add(1.0);

    assertThat(queue.poll()).isEqualTo(1.0);
    assertThat(queue.poll()).isEqualTo(2.0);

    assertThat(queue.isEmpty()).isTrue();
    assertThat(queue.size()).isEqualTo(0);
  }

  @Test
  public void poll_throwsOnEmpty() {
    DoublePriorityQueue queue = new DoublePriorityQueue();

    assertThrows(NoSuchElementException.class, queue::poll);
  }

  @Test
  public void clear_makesQueueEmpty() {
    DoublePriorityQueue queue = new DoublePriorityQueue();

    queue.add(2.0);
    queue.add(1.0);
    queue.clear();

    assertThat(queue.isEmpty()).isTrue();
    assertThat(queue.size()).isEqualTo(0);
  }
}