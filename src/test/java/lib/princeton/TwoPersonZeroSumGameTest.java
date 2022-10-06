package lib.princeton;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class TwoPersonZeroSumGameTest {

  private static final double ERROR = 1.0e-8;

  private static final Range<Double> ZERO_TO_ONE = Range.closed(-0.0, 1.0);

  @Test
  public void onceChoice_alwaysWin() {
    double[][] payoff = {
        {5}
    };

    TwoPersonZeroSumGame zeroSumGame = new TwoPersonZeroSumGame(payoff);

    assertThat(zeroSumGame.value()).isWithin(ERROR).of(5.0);

    double[] x = zeroSumGame.row();
    assertThat(x).hasLength(payoff[0].length);
    assertValidChoiceArray(x);

    double[] y = zeroSumGame.column();
    assertThat(y).hasLength(payoff.length);
    assertValidChoiceArray(y);

    assertThat(x[0]).isWithin(ERROR).of(1.0);

    assertThat(y[0]).isWithin(ERROR).of(1.0);
  }

  @Test
  public void onceChoice_alwaysLose() {
    double[][] payoff = {
        {-5}
    };

    TwoPersonZeroSumGame zeroSumGame = new TwoPersonZeroSumGame(payoff);

    assertThat(zeroSumGame.value()).isWithin(ERROR).of(-5.0);

    double[] x = zeroSumGame.row();
    assertThat(x).hasLength(payoff[0].length);
    assertValidChoiceArray(x);

    double[] y = zeroSumGame.column();
    assertThat(y).hasLength(payoff.length);
    assertValidChoiceArray(y);

    assertThat(x[0]).isWithin(ERROR).of(1.0);

    assertThat(y[0]).isWithin(ERROR).of(1.0);
  }

  @Test
  public void rock_paper_scissors() {
    double[][] payoff = {
        {0, -1, 1},
        {1, 0, -1},
        {-1, 1, 0}
    };

    TwoPersonZeroSumGame zeroSumGame = new TwoPersonZeroSumGame(payoff);

    assertThat(zeroSumGame.value()).isWithin(ERROR).of(0.0);

    double[] x = zeroSumGame.row();
    assertThat(x).hasLength(payoff[0].length);
    assertValidChoiceArray(x);

    double[] y = zeroSumGame.column();
    assertThat(y).hasLength(payoff.length);
    assertValidChoiceArray(y);

    assertThat(x[0]).isWithin(ERROR).of(1.0 / 3.0);
    assertThat(x[1]).isWithin(ERROR).of(1.0 / 3.0);
    assertThat(x[2]).isWithin(ERROR).of(1.0 / 3.0);

    assertThat(y[0]).isWithin(ERROR).of(1.0 / 3.0);
    assertThat(y[1]).isWithin(ERROR).of(1.0 / 3.0);
    assertThat(y[2]).isWithin(ERROR).of(1.0 / 3.0);
  }

  @Test
  public void wikipedia() {
    double[][] payoff = {
        {30, -10, 20},
        {10, 20, -20}
    };

    TwoPersonZeroSumGame zeroSumGame = new TwoPersonZeroSumGame(payoff);

    assertThat(zeroSumGame.value()).isWithin(ERROR).of(20.0 / 7.0);

    double[] x = zeroSumGame.row();
    assertThat(x).hasLength(payoff[0].length);
    assertValidChoiceArray(x);

    double[] y = zeroSumGame.column();
    assertThat(y).hasLength(payoff.length);
    assertValidChoiceArray(y);

    assertThat(x[0]).isWithin(ERROR).of(0.0 / 7.0);
    assertThat(x[1]).isWithin(ERROR).of(4.0 / 7.0);
    assertThat(x[2]).isWithin(ERROR).of(3.0 / 7.0);

    assertThat(y[0]).isWithin(ERROR).of(4.0 / 7.0);
    assertThat(y[1]).isWithin(ERROR).of(3.0 / 7.0);
  }

  @Test
  public void chvatal_p230() {
    double[][] payoff = {
        {0, 2, -3, 0},
        {-2, 0, 0, 3},
        {3, 0, 0, -4},
        {0, -3, 4, 0}
    };

    TwoPersonZeroSumGame zeroSumGame = new TwoPersonZeroSumGame(payoff);

    assertThat(zeroSumGame.value()).isWithin(ERROR).of(0.0);

    double[] x = zeroSumGame.row();
    assertThat(x).hasLength(payoff[0].length);
    assertValidChoiceArray(x);

    double[] y = zeroSumGame.column();
    assertThat(y).hasLength(payoff.length);
    assertValidChoiceArray(y);
  }

  @Test
  public void chvatal_p234() {
    double[][] payoff = {
        {0, 2, -3, 0},
        {-2, 0, 0, 3},
        {3, 0, 0, -4},
        {0, -3, 4, 0},
        {0, 0, -3, 3},
        {-2, 2, 0, 0},
        {3, -3, 0, 0},
        {0, 0, 4, -4}
    };

    TwoPersonZeroSumGame zeroSumGame = new TwoPersonZeroSumGame(payoff);

    assertThat(zeroSumGame.value()).isWithin(ERROR).of(4.0 / 99.0);

    double[] x = zeroSumGame.row();
    assertThat(x).hasLength(payoff[0].length);
    assertValidChoiceArray(x);

    double[] y = zeroSumGame.column();
    assertThat(y).hasLength(payoff.length);
    assertValidChoiceArray(y);
  }

  @Test
  public void chvatal_p236() {
    double[][] payoff = {
        {0, 2, -1, -1},
        {0, 1, -2, -1},
        {-1, -1, 1, 1},
        {-1, 0, 0, 1},
        {1, -2, 0, -3},
        {1, -1, -1, -3},
        {0, -3, 2, -1},
        {0, -2, 1, -1},
    };

    TwoPersonZeroSumGame zeroSumGame = new TwoPersonZeroSumGame(payoff);

    assertThat(zeroSumGame.value()).isWithin(ERROR).of(-1.0 / 3.0);

    double[] x = zeroSumGame.row();
    assertThat(x).hasLength(payoff[0].length);
    assertValidChoiceArray(x);

    double[] y = zeroSumGame.column();
    assertThat(y).hasLength(payoff.length);
    assertValidChoiceArray(y);
  }


  private static void assertValidChoiceArray(double[] arr) {
    double sum = 0.0;
    for (double d : arr) {
      assertThat(d).isIn(ZERO_TO_ONE);
      sum += d;
    }
    assertThat(sum).isWithin(ERROR).of(1.0);
  }
}