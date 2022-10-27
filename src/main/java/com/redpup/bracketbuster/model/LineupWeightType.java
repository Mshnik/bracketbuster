package com.redpup.bracketbuster.model;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.reducing;

import com.redpup.bracketbuster.util.Constants;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * Ways of computing a lineup's weight from deck weights.
 */
public enum LineupWeightType {
  /**
   * A lineup's weight is the (summed) average of its deck weights.
   */
  AVERAGE {
    @Override
    double identity() {
      return 0.0;
    }

    @Override
    double combine(double d1, double d2) {
      return d1 + d2;
    }

    @Override
    double finish(double d) {
      return d / Constants.PLAYER_DECK_COUNT;
    }
  },

  /**
   * A lineup's weight is the geometric average of its deck weights.
   */
  GEOMETRIC {
    @Override
    double identity() {
      return 1.0;
    }

    @Override
    double combine(double d1, double d2) {
      return d1 * d2;
    }

    @Override
    double finish(double d) {
      return Math.pow(d, 1.0 / Constants.PLAYER_DECK_COUNT);
    }
  };

  /**
   * Returns the identity value for this type of combining.
   */
  abstract double identity();

  /**
   * Combines {@code d1} and {@code d2} and returns the new working value.
   */
  abstract double combine(double d1, double d2);

  /**
   * Finishes {@code d} for any post processing.
   */
  abstract double finish(double d);

  /**
   * Collects {@code stream} to a value based on this weight type. API is inverted to avoid boxing
   * all doubles.
   */
  final double collect(DoubleStream stream) {
    return finish(stream.reduce(identity(), this::combine));
  }

  /**
   * Returns a {@link Collector} that applies this weight type to a stream.
   */
  final Collector<Double, ?, Double> collector() {
    return collectingAndThen(reducing(identity(), this::combine), this::finish);
  }
}