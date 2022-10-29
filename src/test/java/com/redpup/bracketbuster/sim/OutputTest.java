package com.redpup.bracketbuster.sim;

import static com.google.common.collect.MoreCollectors.onlyElement;
import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.sim.Output.computeMetaCompPercentMap;
import static com.redpup.bracketbuster.sim.Output.limitAndCopyTopLineups;

import com.google.common.collect.ImmutableMap;
import com.redpup.bracketbuster.model.Lineup;
import com.redpup.bracketbuster.model.MatchupMatrix;
import com.redpup.bracketbuster.model.proto.MatchupMessage;
import com.redpup.bracketbuster.util.WeightedDoubleMetric;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class OutputTest {

  private static final MatchupMessage MATCHUP_MESSAGE_A_A
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("A")
      .setWins(2)
      .setGames(4)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_B
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("B")
      .setWins(2)
      .setGames(3)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_C
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("C")
      .setWins(2)
      .setGames(5)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_D
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("D")
      .setWins(2)
      .setGames(5)
      .build();
  private static final MatchupMessage MATCHUP_MESSAGE_A_E
      = MatchupMessage.newBuilder()
      .setPlayer("A")
      .setOpponent("E")
      .setWins(2)
      .setGames(9)
      .build();

  private static final MatchupMatrix MATCHUP_MATRIX =
      MatchupMatrix.from(
          MATCHUP_MESSAGE_A_A,
          MATCHUP_MESSAGE_A_B,
          MATCHUP_MESSAGE_A_C,
          MATCHUP_MESSAGE_A_D,
          MATCHUP_MESSAGE_A_E);

  private static final Lineup LINEUP_1 = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1, 2);
  private static final Lineup LINEUP_2 = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1, 3);
  private static final Lineup LINEUP_3 = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 1, 4);
  private static final Lineup LINEUP_4 = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 2, 3);
  private static final Lineup LINEUP_5 = Lineup.ofDeckIndices(MATCHUP_MATRIX, 0, 2, 4);

  @Test
  public void limitAndCopyTopLineups_notLimited() {
    assertThat(limitAndCopyTopLineups(
        ImmutableMap.of(
            LINEUP_1, WeightedDoubleMetric.builder().add(0.1).build(),
            LINEUP_2, WeightedDoubleMetric.builder().add(0.2).build(),
            LINEUP_3, WeightedDoubleMetric.builder().add(0.3).build(),
            LINEUP_4, WeightedDoubleMetric.builder().add(0.4).build(),
            LINEUP_5, WeightedDoubleMetric.builder().add(0.5).build()
        ),
        SortType.UNWEIGHTED_MEAN_WIN_RATE, 10))
        .containsExactly(
            LINEUP_5, WeightedDoubleMetric.builder().add(0.5).build(),
            LINEUP_4, WeightedDoubleMetric.builder().add(0.4).build(),
            LINEUP_3, WeightedDoubleMetric.builder().add(0.3).build(),
            LINEUP_2, WeightedDoubleMetric.builder().add(0.2).build(),
            LINEUP_1, WeightedDoubleMetric.builder().add(0.1).build());
  }

  @Test
  public void limitAndCopyTopLineups_limited() {
    assertThat(limitAndCopyTopLineups(
        ImmutableMap.of(
            LINEUP_1, WeightedDoubleMetric.builder().add(0.1).build(),
            LINEUP_2, WeightedDoubleMetric.builder().add(0.2).build(),
            LINEUP_3, WeightedDoubleMetric.builder().add(0.3).build(),
            LINEUP_4, WeightedDoubleMetric.builder().add(0.4).build(),
            LINEUP_5, WeightedDoubleMetric.builder().add(0.5).build()
        ), SortType.UNWEIGHTED_MEAN_WIN_RATE, 2))
        .containsExactly(
            LINEUP_5, WeightedDoubleMetric.builder().add(0.5).build(),
            LINEUP_4, WeightedDoubleMetric.builder().add(0.4).build());
  }

  @Test
  public void limitAndCopyTopLineups_mapKeysGet() {
    ImmutableMap<Lineup, WeightedDoubleMetric> map =
        limitAndCopyTopLineups(
            ImmutableMap.of(
                LINEUP_1, WeightedDoubleMetric.builder().add(0.1).build(),
                LINEUP_2, WeightedDoubleMetric.builder().add(0.2).build(),
                LINEUP_3, WeightedDoubleMetric.builder().add(0.3).build(),
                LINEUP_4, WeightedDoubleMetric.builder().add(0.4).build(),
                LINEUP_5, WeightedDoubleMetric.builder().add(0.5).build()),
            SortType.UNWEIGHTED_MEAN_WIN_RATE, 5);

    assertThat(map.get(LINEUP_1)).isEqualTo(WeightedDoubleMetric.builder().add(0.1).build());
    assertThat(map.get(LINEUP_2)).isEqualTo(WeightedDoubleMetric.builder().add(0.2).build());
    assertThat(map.get(LINEUP_3)).isEqualTo(WeightedDoubleMetric.builder().add(0.3).build());
    assertThat(map.get(LINEUP_4)).isEqualTo(WeightedDoubleMetric.builder().add(0.4).build());
    assertThat(map.get(LINEUP_5)).isEqualTo(WeightedDoubleMetric.builder().add(0.5).build());
  }

  @Test
  public void limitAndCopyTopLineups_copiesKeys() {
    ImmutableMap<Lineup, WeightedDoubleMetric> map =
        limitAndCopyTopLineups(
            ImmutableMap.of(
                LINEUP_1, WeightedDoubleMetric.builder().add(0.1).build(),
                LINEUP_2, WeightedDoubleMetric.builder().add(0.2).build(),
                LINEUP_3, WeightedDoubleMetric.builder().add(0.3).build(),
                LINEUP_4, WeightedDoubleMetric.builder().add(0.4).build(),
                LINEUP_5, WeightedDoubleMetric.builder().add(0.5).build()),
            SortType.UNWEIGHTED_MEAN_WIN_RATE, 5);

    assertThat(map.keySet().stream().filter(l -> l.equals(LINEUP_1)).collect(onlyElement()))
        .isNotSameInstanceAs(LINEUP_1);
  }

  @Test
  public void computeMetaCompPercentMap_countsDecks() {
    ImmutableMap<String, Double> map =
        computeMetaCompPercentMap(
            ImmutableMap.of(
                LINEUP_1, 0.1,
                LINEUP_2, 0.2,
                LINEUP_3, 0.3,
                LINEUP_4, 0.4,
                LINEUP_5, 0.5),
            MATCHUP_MATRIX);

    assertThat(map).containsExactly(
        "A", 1.0,
        "B", 0.6,
        "C", 0.6,
        "D", 0.4,
        "E", 0.4);
  }

  @Test
  public void buildOutput() {
    assertThat(
        Output.buildOutput(
            ImmutableMap.of(
                LINEUP_1, WeightedDoubleMetric.builder().add(0.1).build(),
                LINEUP_2, WeightedDoubleMetric.builder().add(0.2).build(),
                LINEUP_3, WeightedDoubleMetric.builder().add(0.3).build(),
                LINEUP_4, WeightedDoubleMetric.builder().add(0.4).build(),
                LINEUP_5, WeightedDoubleMetric.builder().add(0.5).build()),
            MATCHUP_MATRIX,
            SortType.UNWEIGHTED_MEAN_WIN_RATE,
            3))
        .isEqualTo(
            new Output(
                ImmutableMap.of(
                    LINEUP_5, WeightedDoubleMetric.builder().add(0.5).build(),
                    LINEUP_4, WeightedDoubleMetric.builder().add(0.4).build(),
                    LINEUP_3, WeightedDoubleMetric.builder().add(0.3).build()),
                ImmutableMap.of("A", 1.0,
                    "B", 0.6,
                    "C", 0.6,
                    "D", 0.4,
                    "E", 0.4)));
  }
}