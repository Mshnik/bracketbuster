package com.redpup.bracketbuster.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
   * Returns true iff all '/' separated components are unique. Components in parens are not split.
   *
   * <pre>
   *   "AA/AA (BB/CC)" --> False
   *   "AA/BB (CC/DD)" --> True
   * </pre>
   */
  public static boolean allComponentsUnique(String value) {
    String[] arr = value.split(" \\(");

    List<String> components = new ArrayList<>();
    splitOnSlashes(arr[0]).forEach(components::add);
    components.add(arr[1].trim());

    return components.size() == new HashSet<>(components).size();
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
    return splitOnSlashes(value).sorted().collect(Collectors.joining("/"));
  }

  /**
   * Splits input strings on slashes and trims each element.
   */
  private static Stream<String> splitOnSlashes(String value) {
    return Arrays.stream(value.trim().split("/"))
        .map(String::trim);
  }

}
