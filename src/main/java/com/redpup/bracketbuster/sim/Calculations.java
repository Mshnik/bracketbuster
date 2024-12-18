package com.redpup.bracketbuster.sim;

import static com.google.common.base.Preconditions.checkArgument;
import static com.redpup.bracketbuster.util.Constants.PLAYER_DECK_COUNT;
import static java.util.Objects.requireNonNull;

import com.google.common.annotations.VisibleForTesting;
import com.redpup.bracketbuster.model.Lineup;
import com.redpup.bracketbuster.model.MatchupMatrix;
import lib.princeton.TwoPersonZeroSumGame;

/**
 * Math class for calculating win rates from {@link Lineup}s referencing a given {@link
 * MatchupMatrix}.
 */
public final class Calculations {

  private Calculations() {
  }

  /**
   * Ways of computing a matchup between players.
   */
  enum CalculationType {
    /**
     * Players ban the deck against them with overall best odds.
     */
    NAIVE,
    /**
     * Players ban the deck against them according to nash equilibrium.
     */
    NASH
  }

  /**
   * Returns the chance that {@code player} wins against {@code opponent}, given that each is
   * allowed to ban one deck from the other.
   *
   * <p>Each player bans the best deck to ban given a two player zero sum analysis done by {@link
   * lib.princeton.TwoPersonZeroSumGame}.
   *
   * <p>After banning one deck each, the resulting win rate for {@code player} is the chance they
   * win two matches in a best of three, where the winner of a game cannot repeat the same winning
   * deck again in the match. This effectively means that {@code player} must win with both
   * un-banned decks to win the match (before their opponent does the same.)
   */
  public static double winRateBestTwoOfThreeOneBanNash(Lineup player, Lineup opponent,
      MatchupMatrix matchups) {
    double[][] winRates = checkDecksIncrementPlayedAndComputeWinRates(player, opponent, matchups);
    double[][] winRatesAfterBans = computeExpectedWinRatesWithBans(winRates);

    TwoPersonZeroSumGame zeroSumGame = new TwoPersonZeroSumGame(winRatesAfterBans);
    double[] banProbabilities = zeroSumGame.column();

    for (int i = 0; i < PLAYER_DECK_COUNT; i++) {
      if (banProbabilities[i] > 0) {
        player.metadata().incrementBanned(opponent.getDeck(i), banProbabilities[i]);
      }
    }

    return zeroSumGame.value();
  }

  /**
   * Returns the chance that {@code player} wins against {@code opponent}, given that each is
   * allowed to ban one deck from the other.
   *
   * <p>Uses a somewhat simplistic banning algorithm in which each player bans the overall best
   * deck against them. This is a rough approximation but could be improved into a more
   * game-theoretic analysis if one could be done quickly.
   *
   * <p>After banning one deck each, the resulting win rate for {@code player} is the chance they
   * win two matches in a best of three, where the winner of a game cannot repeat the same winning
   * deck again in the match. This effectively means that {@code player} must win with both
   * un-banned decks to win the match (before their opponent does the same.)
   */
  public static double winRateBestTwoOfThreeOneBanNaive(Lineup player, Lineup opponent,
      MatchupMatrix matchups) {
    double[][] winRates = checkDecksIncrementPlayedAndComputeWinRates(player, opponent, matchups);

    int bestPlayerDeckToBan = banPlayerDeckNaive(winRates);
    int bestOpponentDeckToBan = banOpponentDeckNaive(winRates);
    player.metadata().incrementBanned(opponent.getDeck(bestOpponentDeckToBan));

    return winRateBestTwoOfThree(
        dropBannedDecksAndFlatten(winRates, bestPlayerDeckToBan, bestOpponentDeckToBan));
  }

  /**
   * Asserts that {@code player} and {@code opponent} have the expected number of decks set,
   * increments {@code player}'s played against stat, and computes win rates of every deck into
   * every deck.
   */
  private static double[][] checkDecksIncrementPlayedAndComputeWinRates(Lineup player,
      Lineup opponent,
      MatchupMatrix matchups) {
    checkArgument(player.getDecks().size() == PLAYER_DECK_COUNT,
        "Expected %s decks, found %s",
        PLAYER_DECK_COUNT,
        player.getDecks());
    checkArgument(opponent.getDecks().size() == PLAYER_DECK_COUNT,
        "Expected %s decks, found %s",
        PLAYER_DECK_COUNT,
        opponent.getDecks());

    for (int opponentDeck : opponent.getDecks()) {
      player.metadata().incrementPlayedAgainst(opponentDeck);
    }

    double[][] winRates = new double[PLAYER_DECK_COUNT][PLAYER_DECK_COUNT];
    for (int i = 0; i < PLAYER_DECK_COUNT; i++) {
      for (int j = 0; j < PLAYER_DECK_COUNT; j++) {
        winRates[i][j] = requireNonNull(
            matchups.getMatchup(player.getDeck(i), opponent.getDeck(j))).getWinRate();
      }
    }

    return winRates;
  }

  /**
   * Picks the best player deck the opponent can ban from the given {@code winRates}.
   *
   * <p>Assumes {@code winRates} has the following format. {@code Ai} are player decks
   * {@code A1 ... An}, {@code Bi} are opponent decks {@code B1 ... Bn}, and AjBk is the odds the
   * player wins the game {@code Aj vs Bk}.
   *
   * <pre>
   *   [ [ A1B1, A1B2, .... , A1Bn ]
   *     [ A2B1, A2B2, .... , A2Bn ]
   *     ....
   *     [ AnB1, AnB2, .... AnBn ] ]
   * </pre>
   */
  @VisibleForTesting
  static int banPlayerDeckNaive(double[][] winRates) {
    double[] playerWinSums = new double[winRates.length];
    for (int row = 0; row < winRates.length; row++) {
      for (int col = 0; col < winRates[row].length; col++) {
        playerWinSums[row] += winRates[row][col];
      }
    }

    return maxIndex(playerWinSums);
  }

  /**
   * Picks the best opponent deck the player can ban from the given {@code winRates}.
   *
   * <p>Assumes {@code winRates} has the following format. {@code Ai} are player decks
   * {@code A1 ... An}, {@code Bi} are opponent decks {@code B1 ... Bn}, and AjBk is the odds the
   * player wins the game {@code Aj vs Bk}.
   *
   * <pre>
   *   [ [ A1B1, A1B2, .... , A1Bn ]
   *     [ A2B1, A2B2, .... , A2Bn ]
   *     ....
   *     [ AnB1, AnB2, .... AnBn ] ]
   * </pre>
   */
  @VisibleForTesting
  static int banOpponentDeckNaive(double[][] winRates) {
    double[] opponentWinSums = new double[winRates[0].length];
    for (int row = 0; row < winRates.length; row++) {
      for (int col = 0; col < winRates[row].length; col++) {
        opponentWinSums[col] += winRates[row][col];
      }
    }

    return minIndex(opponentWinSums);
  }

  /**
   * Computes a winRate matrix where each element {@code [i, j]} is the overall win rate for the
   * player given the banning player's deck i and opponent's deck j.
   */
  @VisibleForTesting
  static double[][] computeExpectedWinRatesWithBans(double[][] winRates) {
    double[][] postBanWinRates = new double[PLAYER_DECK_COUNT][PLAYER_DECK_COUNT];
    for (int i = 0; i < PLAYER_DECK_COUNT; i++) {
      for (int j = 0; j < PLAYER_DECK_COUNT; j++) {
        postBanWinRates[i][j] = winRateBestTwoOfThree(dropBannedDecksAndFlatten(winRates, i, j));
      }
    }
    return postBanWinRates;
  }

  /**
   * Drops the requested {@code droppedPlayerDeck} and {@code droppedOpponentDeck} from {@code
   * winRates} and flattens it.
   *
   *
   * <pre>
   *   // From:
   *   [ [ AA, AB, AC ]
   *     [ BA, BB, BC ]
   *     [ CA, CB, CC ] ]
   *
   * // Banning Player B, Opponent C
   *
   *   // To:
   *   [ AA, AB, CA, CB ]
   * </pre>
   */
  @VisibleForTesting
  static double[] dropBannedDecksAndFlatten(double[][] winRates, int droppedPlayerDeck,
      int droppedOpponentDeck) {
    double[] postBanWinRates = new double[(PLAYER_DECK_COUNT - 1) * (
        PLAYER_DECK_COUNT - 1)];
    int k = 0;
    for (int i = 0; i < PLAYER_DECK_COUNT; i++) {
      if (i == droppedPlayerDeck) {
        continue;
      }
      for (int j = 0; j < PLAYER_DECK_COUNT; j++) {
        if (j == droppedOpponentDeck) {
          continue;
        }
        postBanWinRates[k++] = winRates[i][j];
      }
    }
    return postBanWinRates;
  }

  /**
   * Computes the probability that {@code player} wins two out of three of the given four winRates,
   * chosen at random.
   *
   * <p> Win rates are expected as a flattened 1d array. Specifically, the following transformation
   * is expected:
   *
   * <pre>
   *   // From:
   *   [ [ AA, AB ]
   *     [ BA, BB ] ]
   *
   *   // To:
   *   [ AA, AB, BA, BB ]
   * </pre>
   *
   * <p>The winner of a game cannot use the same deck again. This means that we explicitly do not
   * consider the cases of {@code winRates[0] + [1]}, {@code [2] + [3]}, as these would require
   * {@code player} to use the same deck twice.
   */
  @VisibleForTesting
  static double winRateBestTwoOfThree(double... winRates) {
    checkArgument(winRates.length == 4, "Expected arr of length 4, found %s", winRates);
    for (double winRate : winRates) {
      checkArgument(winRate >= 0 && winRate <= 1,
          "Expected winRate in range [0,1], found %s", winRates);
    }

    // playerRed, opponentRed.
    double redRed = winRates[0];
    // playerRed, opponentBlue.
    double redBlue = winRates[1];
    // playerBlue, opponentRed.
    double blueRed = winRates[2];
    // playerBlue, opponentBlue.
    double blueBlue = winRates[3];

    // Explicitly missing win 0+1, win 2+3, because these would use the same deck twice.
    double chanceWinEitherRed = 1 - ((1 - redRed) * (1 - redBlue));
    double chanceWinEitherBlue = 1 - ((1 - blueRed) * (1 - blueBlue));
    double chanceWinEitherAverage = (chanceWinEitherRed + chanceWinEitherBlue) / 2;

    double chanceWinBothAgainstRed = redRed * blueRed;
    double chanceWinBothAgainstBlue = redBlue * blueBlue;
    double chanceWinBothAgainstAverage = (chanceWinBothAgainstRed + chanceWinBothAgainstBlue) / 2;

    double chanceWinFirstGame = (redRed + redBlue + blueRed + blueBlue) / 4;
    double chanceLoseFirstGame = 1 - chanceWinFirstGame;

    double chanceWinFirstAndWinMatch = chanceWinFirstGame * chanceWinEitherAverage;
    double chanceLoseFirstAndWinMatch = chanceLoseFirstGame * chanceWinBothAgainstAverage;

    return chanceWinFirstAndWinMatch + chanceLoseFirstAndWinMatch;
  }

  /**
   * Returns the index of the highest value in {@code values}.
   */
  @VisibleForTesting
  static int maxIndex(double... values) {
    int maxIndex = -1;
    double maxValue = Double.MIN_VALUE;
    for (int i = 0; i < values.length; i++) {
      if (maxIndex == -1 || maxValue < values[i]) {
        maxIndex = i;
        maxValue = values[i];
      }
    }
    return maxIndex;
  }

  /**
   * Returns the index of the lowest value in {@code values}.
   */
  @VisibleForTesting
  static int minIndex(double... values) {
    int minIndex = -1;
    double minValue = Double.MAX_VALUE;
    for (int i = 0; i < values.length; i++) {
      if (minIndex == -1 || minValue > values[i]) {
        minIndex = i;
        minValue = values[i];
      }
    }
    return minIndex;
  }
}
