package com.redpup.bracketbuster.model;

import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LineupMetadataTest {

  @Test
  public void empty_initializesArraysWithLength() {
    LineupMetadata metadata = new LineupMetadata(5);
    assertThat(metadata.getPlayedAgainst()).isEqualTo(new int[]{0, 0, 0, 0, 0});
    assertThat(metadata.getBanned()).isEqualTo(new int[]{0, 0, 0, 0, 0});
  }

  @Test
  public void incrementPlayedAgainst_incrementsValues() {
    LineupMetadata metadata = new LineupMetadata(5);
    metadata.incrementPlayedAgainst(0);
    metadata.incrementPlayedAgainst(1);
    metadata.incrementPlayedAgainst(1);
    assertThat(metadata.getPlayedAgainst()).isEqualTo(new int[]{1, 2, 0, 0, 0});
  }


  @Test
  public void incrementPlayedAgainst_throwsIfOOB() {
    LineupMetadata metadata = new LineupMetadata(5);
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> metadata.incrementPlayedAgainst(-1));
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> metadata.incrementPlayedAgainst(10));
    assertThat(metadata.getPlayedAgainst()).isEqualTo(new int[]{0, 0, 0, 0, 0});
  }

  @Test
  public void incrementBanned_incrementsValues() {
    LineupMetadata metadata = new LineupMetadata(5);
    metadata.incrementBanned(0);
    metadata.incrementBanned(1);
    metadata.incrementBanned(1);
    assertThat(metadata.getBanned()).isEqualTo(new int[]{1, 2, 0, 0, 0});
  }

  @Test
  public void incrementBanned_throwsIfOOB() {
    LineupMetadata metadata = new LineupMetadata(5);
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> metadata.incrementBanned(-1));
    assertThrows(ArrayIndexOutOfBoundsException.class, () -> metadata.incrementBanned(10));
    assertThat(metadata.getBanned()).isEqualTo(new int[]{0, 0, 0, 0, 0});
  }
}