package com.redpup.bracketbuster.util;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * A simple wrapper on a type, for effective finality in lambdas.
 */
public final class Mutable<T> {

  /**
   * Returns a new empty {@link Mutable}.
   */
  public static <T> Mutable<T> create() {
    return new Mutable<>(null);
  }

  /**
   * Returns a new {@link Mutable} wrapping {@code value}.
   */
  public static <T> Mutable<T> create(T value) {
    return new Mutable<>(value);
  }

  private T value;

  private Mutable(T initial) {
    this.value = initial;
  }

  /**
   * Returns the current value of this.
   */
  public T get() {
    return value;
  }

  /**
   * Sets the current value of this. Returns this.
   */
  @CanIgnoreReturnValue
  public Mutable<T> set(T value) {
    this.value = value;
    return this;
  }

  /**
   * Mutates this by applying {@code operator} to this. Returns this.
   */
  @CanIgnoreReturnValue
  public Mutable<T> compute(UnaryOperator<T> operator) {
    this.value = operator.apply(value);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Mutable<?> mutable = (Mutable<?>) o;
    return Objects.equals(value, mutable.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "Mutable{" +
        "value=" + value +
        '}';
  }
}
