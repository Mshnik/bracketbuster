package com.redpup.bracketbuster.util;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Bracketbuster specific string methods.
 */
public final class Strings {

  private Strings() {
  }

  /**
   * Sanitizes the {@code value} string so it is consistent. This includes alphabetizing and sorting
   * so different representations of the same string are consistent.
   *
   * <pre>
   *   "Foo/Bar (AA/BB)" --> "Bar/Foo (AA/BB)"
   *   "Bar/Foo (AA/BB)" --> "Bar/Foo (AA/BB)"
   *   "Bar/Foo (BB/AA)" --> "Bar/Foo (AA/BB)"
   * </pre>
   */
  public static String sanitize(String value) {
    String[] arr = value.split(" \\(");

    String parenComponent = arr[1].trim();

    return String.format("%s (%s)",
        sortSlashedValue(arr[0]),
        sortSlashedValue(parenComponent.substring(0, parenComponent.length() - 1)));
  }

  /**
   * Sorts the given string by its slashed components.
   *
   * <pre>
   *   "AA/BB/CC" --> "AA/BB/CC"
   *   "BB/AA/CC" --> "AA/BB/CC"
   * </pre>
   */
  private static String sortSlashedValue(String value) {
    return Arrays.stream(value.trim().split("/"))
        .map(String::trim)
        .sorted()
        .collect(Collectors.joining("/"));
  }
}
