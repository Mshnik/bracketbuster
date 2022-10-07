package lib.princeton;


import static com.google.common.truth.Truth.assertThat;
import static com.redpup.bracketbuster.util.AssertExt.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public final class LinearProgrammingTest {

  private static final double ERROR = 1.0e-8;

  @Test
  public void test2() {
    double[] c = {13.0, 23.0};
    double[] b = {480.0, 160.0, 1190.0};
    double[][] A = {
        {5.0, 15.0},
        {4.0, 4.0},
        {35.0, 20.0},
    };
    LinearProgramming lp = new LinearProgramming(A, b, c);

    assertThat(lp.value()).isWithin(ERROR).of(800.0);

    assertThat(lp.primal()).usingTolerance(ERROR)
        .containsExactly(12.0, 28.0).inOrder();

    assertThat(lp.dual()).usingTolerance(ERROR)
        .containsExactly(1.0, 2.0, 0.0).inOrder();
  }


  @Test
  public void test3() {
    double[] c = {2.0, 3.0, -1.0, -12.0};
    double[] b = {3.0, 2.0};
    double[][] A = {
        {-2.0, -9.0, 1.0, 9.0},
        {1.0, 1.0, -1.0, -2.0},
    };

    assertThrows(ArithmeticException.class, () -> new LinearProgramming(A, b, c));
  }

  @Test
  public void test4() {
    double[] c = {10.0, -57.0, -9.0, -24.0};
    double[] b = {0.0, 0.0, 1.0};
    double[][] A = {
        {0.5, -5.5, -2.5, 9.0},
        {0.5, -1.5, -0.5, 1.0},
        {1.0, 0.0, 0.0, 0.0},
    };
    LinearProgramming lp = new LinearProgramming(A, b, c);

    assertThat(lp.value()).isWithin(ERROR).of(1.0);

    assertThat(lp.primal()).usingTolerance(ERROR)
        .containsExactly(1.0, 0.0, 1.0, 0.0).inOrder();

    assertThat(lp.dual()).usingTolerance(ERROR)
        .containsExactly(0.0, 18.0, 1.0).inOrder();
  }
}
