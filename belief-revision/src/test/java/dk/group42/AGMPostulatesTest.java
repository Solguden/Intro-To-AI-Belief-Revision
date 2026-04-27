package dk.group42;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

// AGM postulate tests, driven through AGMChecks
// Required by the assignment (belief_revision_2026 §3)
class AGMPostulatesTest {

  // (label, KB, alpha) — for the four single-alpha postulates
  static Stream<Arguments> scenarios() {
    return Stream.of(
            // Bob: {p,q} learns !q
            Arguments.of("Bob: {p,q} learns !q",        kb("p", "q"),       "!q"),
            // KB derives q via modus ponens, then we revise by !q
            Arguments.of("modus ponens revised by !q",  kb("p -> q", "p"),  "!q"),
            // Bob: {p,q,r} learns !(q | r)
            Arguments.of("{p,q,r} learns !(q | r)",     kb("p", "q", "r"),  "!(q | r)"),
            // alpha unrelated to KB — should behave like expansion (vacuity bites)
            Arguments.of("independent revision",        kb("p", "r"),       "s"),
            // alpha already entailed by KB
            Arguments.of("alpha already in KB",         kb("p", "p -> q"),  "q"),
            // empty KB
            Arguments.of("empty KB",                    kb(),               "p")
    );
  }

  // (label, KB, alpha, beta) — beta is logically equivalent to alpha, used by extensionality
  static Stream<Arguments> equivPairs() {
    return Stream.of(
            Arguments.of("Bob: {p,q} learns !q",        kb("p", "q"),       "!q",        "q -> (a & !a)"),
            Arguments.of("modus ponens revised by !q",  kb("p -> q", "p"),  "!q",        "!q & (r | !r)"),
            Arguments.of("{p,q,r} learns !(q | r)",     kb("p", "q", "r"),  "!(q | r)",  "!q & !r"),
            Arguments.of("independent revision",        kb("p", "r"),       "s",         "!!s"),
            Arguments.of("alpha already in KB",         kb("p", "p -> q"),  "q",         "q | (a & !a)"),
            Arguments.of("empty KB",                    kb(),               "p",         "!!p")
    );
  }

  // K * alpha entails alpha
  @ParameterizedTest(name = "Success — {0}")
  @MethodSource("scenarios")
  void success(String label, BeliefBase k, String alpha) {
    assertThat(AGMChecks.success(k, alpha)).as(label).isTrue();
  }

  // K * alpha is a subset of K + alpha
  @ParameterizedTest(name = "Inclusion — {0}")
  @MethodSource("scenarios")
  void inclusion(String label, BeliefBase k, String alpha) {
    assertThat(AGMChecks.inclusion(k, alpha)).as(label).isTrue();
  }

  // if !alpha not in K then K * alpha = K + alpha
  @ParameterizedTest(name = "Vacuity — {0}")
  @MethodSource("scenarios")
  void vacuity(String label, BeliefBase k, String alpha) {
    assertThat(AGMChecks.vacuity(k, alpha)).as(label).isTrue();
  }

  // K * alpha is consistent when alpha is
  @ParameterizedTest(name = "Consistency — {0}")
  @MethodSource("scenarios")
  void consistency(String label, BeliefBase k, String alpha) {
    assertThat(AGMChecks.consistency(k, alpha)).as(label).isTrue();
  }

  // alpha <-> beta tautology => K * alpha = K * beta
  @ParameterizedTest(name = "Extensionality — {0}")
  @MethodSource("equivPairs")
  void extensionality(String label, BeliefBase k, String alpha, String beta) {
    // sanity: scenario data must actually be equivalent
    assertThat(Resolution.equivalent(alpha, beta)).as(label).isTrue();
    assertThat(AGMChecks.extensionality(k, alpha, beta)).as(label).isTrue();
  }

  // postulates with antecedents short-circuit to true when the premise fails
  @Nested
  @DisplayName("Vacuous-antecedent handling")
  class Vacuous {

    // alpha inconsistent => Success vacuous
    @Test
    void successVacuousOnContradiction() {
      assertThat(AGMChecks.success(kb("p"), "q & !q")).isTrue();
    }

    // alpha inconsistent => Consistency vacuous
    @Test
    void consistencyVacuousOnContradiction() {
      assertThat(AGMChecks.consistency(kb("p"), "q & !q")).isTrue();
    }

    // alpha and beta not equivalent => Extensionality vacuous
    @Test
    void extensionalityVacuousOnNonEquivalent() {
      assertThat(AGMChecks.extensionality(kb("p"), "q", "r")).isTrue();
    }

    // !alpha already in K => Vacuity premise false, vacuously true
    @Test
    void vacuityVacuousWhenAlphaContradictsKb() {
      assertThat(AGMChecks.vacuity(kb("p", "q"), "!q")).isTrue();
    }
  }

  // mirrors what App.main() prints, but as an assertion
  @Test
  void runAllOnRunningExample() {
    BeliefBase k = kb("p -> q", "p");
    Map<String, Boolean> result = AGMChecks.runAll(k, "!q", "q -> (a & !a)");

    assertThat(result)
            .containsEntry("success",        true)
            .containsEntry("inclusion",      true)
            .containsEntry("vacuity",        true)
            .containsEntry("consistency",    true)
            .containsEntry("extensionality", true);
  }

  // build a BeliefBase with strictly increasing priorities (1, 2, 3, ...)
  private static BeliefBase kb(String... formulas) {
    var bs = new ArrayList<Belief>();
    for (int i = 0; i < formulas.length; i++) {
      bs.add(new Belief(formulas[i], i + 1));
    }
    return new BeliefBase(bs);
  }
}