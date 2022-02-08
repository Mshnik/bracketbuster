package com.redpup.bracketbuster.sim;


import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

import com.google.common.collect.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class CalculationsTest {

  @Test
  public void maxIndex() {
    assertThat(Calculations.maxIndex()).isEqualTo(-1);
    assertThat(Calculations.maxIndex(0.0)).isEqualTo(0);
    assertThat(Calculations.maxIndex(0.0, 1.0)).isEqualTo(1);
    assertThat(Calculations.maxIndex(1.0, 0.0)).isEqualTo(0);
    assertThat(Calculations.maxIndex(0.0, -1.0, 1.0)).isEqualTo(2);
  }

  @Test
  public void minIndex() {
    assertThat(Calculations.minIndex()).isEqualTo(-1);
    assertThat(Calculations.minIndex(0.0)).isEqualTo(0);
    assertThat(Calculations.minIndex(0.0, 1.0)).isEqualTo(0);
    assertThat(Calculations.minIndex(1.0, 0.0)).isEqualTo(1);
    assertThat(Calculations.minIndex(0.0, -1.0, 1.0)).isEqualTo(1);
  }

  @Test
  public void winBestTwoOfThree_throwsOnBadArrayLength() {
    assertThrows(IllegalArgumentException.class, Calculations::winRateBestTwoOfThree);
    assertThrows(IllegalArgumentException.class, () -> Calculations.winRateBestTwoOfThree(0.0));
    assertThrows(IllegalArgumentException.class,
        () -> Calculations.winRateBestTwoOfThree(0.0, 0.0, 0.0, 0.0, 0.0));
  }

  @Test
  public void winBestTwoOfThree_throwsOnValueOOB() {
    assertThrows(IllegalArgumentException.class,
        () -> Calculations.winRateBestTwoOfThree(0.0, 1.0, 1.0, 1.1));
    assertThrows(IllegalArgumentException.class,
        () -> Calculations.winRateBestTwoOfThree(0.0, -0.1, 1.0, 1.1));
  }

  @Test
  public void winBestTwoOfThree_allZeroes() {
    assertThat(Calculations.winRateBestTwoOfThree(0.0, 0.0, 0.0, 0.0))
        .isEqualTo(0.0);
  }

  @Test
  public void winBestTwoOfThree_lowOdds() {
    assertThat(Calculations.winRateBestTwoOfThree(0.25, 0.25, 0.25, 0.25))
        .isIn(Range.openClosed(0.0, 0.5));
  }

  @Test
  public void winBestTwoOfThree_allPointFive() {
    assertThat(Calculations.winRateBestTwoOfThree(0.5, 0.5, 0.5, 0.5))
        .isEqualTo(0.5);
  }

  @Test
  public void winBestTwoOfThree_highOdds() {
    assertThat(Calculations.winRateBestTwoOfThree(0.75, 0.75, 0.75, 0.75))
        .isIn(Range.closedOpen(0.5, 1.0));
  }

  @Test
  public void winBestTwoOfThree_allOnes() {
    assertThat(Calculations.winRateBestTwoOfThree(1.0, 1.0, 1.0, 1.0))
        .isEqualTo(1.0);
  }

  @Test
  public void banPlayerDeck_oneDeck() {
    assertThat(Calculations.banPlayerDeck(new double[][]{
        {0.0}
    })).isEqualTo(0);
  }

  @Test
  public void banPlayerDeck_allDecksEqual_bansFirstIndex() {
    assertThat(Calculations.banPlayerDeck(new double[][]{
        {0.5, 0.5, 0.5},
        {0.5, 0.5, 0.5},
        {0.5, 0.5, 0.5}
    })).isEqualTo(0);
  }

  @Test
  public void banPlayerDeck_bansBestDeck() {
    assertThat(Calculations.banPlayerDeck(new double[][]{
        {0.5, 0.5, 0.5},
        {0.9, 0.9, 0.9},
        {0.1, 0.1, 0.1}
    })).isEqualTo(1);
  }

  @Test
  public void banPlayerDeck_bansBestDeckOverall() {
    assertThat(Calculations.banPlayerDeck(new double[][]{
        {0.5, 0.5, 0.5},
        {0.9, 0.5, 0.1},
        {0.5, 0.9, 0.5}
    })).isEqualTo(2);
  }


  @Test
  public void banOpponentDeck_oneDeck() {
    assertThat(Calculations.banOpponentDeck(new double[][]{
        {0.0}
    })).isEqualTo(0);
  }

  @Test
  public void banOpponentDeck_allDecksEqual_bansFirstIndex() {
    assertThat(Calculations.banOpponentDeck(new double[][]{
        {0.5, 0.5, 0.5},
        {0.5, 0.5, 0.5},
        {0.5, 0.5, 0.5}
    })).isEqualTo(0);
  }

  @Test
  public void banOpponentDeck_bansWorstDeckForPlayer() {
    assertThat(Calculations.banOpponentDeck(new double[][]{
        {0.5, 0.5, 0.1},
        {0.5, 0.9, 0.1},
        {0.2, 0.5, 0.1}
    })).isEqualTo(2);
  }

  @Test
  public void banOpponentDeck_bansWorstDeckForPlayerOverall() {
    assertThat(Calculations.banOpponentDeck(new double[][]{
        {0.5, 0.5, 0.1},
        {0.9, 0.5, 0.2},
        {0.5, 0.3, 0.5}
    })).isEqualTo(2);
  }

  @Test
  public void dropBannedDecksAndFlatten() {
    assertThat(Calculations.dropBannedDecksAndFlatten(
        new double[][]{
            {0.1, 0.2, 0.3},
            {0.4, 0.5, 0.6},
            {0.7, 0.8, 0.9}},
        1, 2
    )).isEqualTo(new double[]{0.1, 0.2, 0.7, 0.8});
  }

}