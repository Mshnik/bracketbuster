package com.redpup.bracketbuster.util;


import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class AssertExtTest {

  @Test
  public void assertThrows_nothingThrown() {
    boolean assertThrowsRun = false;
    try {
      assertThrows(Exception.class, () -> {
      });
      assertThrowsRun = true;
    } catch (AssertionError error) {
      assertThat(error).hasMessageThat().contains("but nothing was thrown");
    }

    if (assertThrowsRun) {
      fail("Expected an AssertionError when nothing is thrown.");
    }
  }

  @Test
  public void assertThrows_exceptionThrown() {
    assertThat(assertThrows(Exception.class, () -> {
      throw new RuntimeException("Foo");
    })).hasMessageThat().contains("Foo");
  }

  @Test
  public void assertThrows_wrongExceptionThrown() {
    boolean assertThrowsRun = false;
    try {
      assertThrows(IllegalArgumentException.class, () -> {
        throw new NoSuchElementException("Foo");
      });
      assertThrowsRun = true;
    } catch (AssertionError error) {
      assertThat(error).hasMessageThat().contains("but got");
    }

    if (assertThrowsRun) {
      fail("Expected an AssertionError when nothing is thrown.");
    }
  }

}