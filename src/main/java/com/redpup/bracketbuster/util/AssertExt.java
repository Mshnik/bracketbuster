package com.redpup.bracketbuster.util;

/**
 * Assertion methods. TODO - add maven dep to replace this.
 */
public final class AssertExt {

  private AssertExt() {
  }

  @FunctionalInterface
  public static interface ThrowingRunnable<E extends Exception> {

    public void run() throws E;
  }

  public static <E extends Exception> E assertThrows(
      Class<E> exceptionClass,
      ThrowingRunnable<E> throwingRunnable) {
    try {
      throwingRunnable.run();
    } catch (Exception e) {
      if (exceptionClass.isInstance(e)) {
        return exceptionClass.cast(e);
      }
      throw new AssertionError(String
          .format("Expected %s but got %s", exceptionClass.getCanonicalName(),
              e.getClass().getCanonicalName()));
    }

    throw new AssertionError(String.format("Expected %s to be thrown but nothing was thrown.",
        exceptionClass.getCanonicalName()));
  }

}
