package com.redpup.bracketbuster.model;

import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

import com.google.common.testing.EqualsTester;
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

  @Test
  public void obeysEqualsAndHashcode() {
    new EqualsTester()
        .addEqualityGroup(new LineupMetadata(1), new LineupMetadata(1))
        .addEqualityGroup(new LineupMetadata(5), new LineupMetadata(5))
        .addEqualityGroup(new LineupMetadata(5).incrementPlayedAgainst(1),
            new LineupMetadata(5).incrementPlayedAgainst(1))
        .addEqualityGroup(new LineupMetadata(5).incrementBanned(1),
            new LineupMetadata(5).incrementBanned(1))
        .testEquals();
  }

  @Test
  public void copy_createsEqualMetadata() {
    LineupMetadata metadata = new LineupMetadata(2)
        .incrementPlayedAgainst(1)
        .incrementPlayedAgainst(1)
        .incrementBanned(1);

    LineupMetadata copy = metadata.copy();

    assertThat(copy).isNotSameInstanceAs(metadata);
    assertThat(copy).isEqualTo(metadata);
    assertThat(copy.hashCode()).isEqualTo(metadata.hashCode());
  }

  @Test
  public void copy_createsSeparateMetadata() {
    LineupMetadata metadata = new LineupMetadata(2)
        .incrementPlayedAgainst(1)
        .incrementPlayedAgainst(1)
        .incrementBanned(1);

    LineupMetadata copy = metadata.copy();
    metadata.incrementBanned(1);

    assertThat(copy).isNotEqualTo(metadata);
  }
}