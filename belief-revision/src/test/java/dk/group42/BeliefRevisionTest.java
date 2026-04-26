package dk.group42;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BeliefRevisionTest {

  @BeforeAll
  static void setUp() {

  }

  //From slides9
  //Bob believes {p,q}
  //learns !q
  //new belief should be {p,!q}
  @Test
  void revisionTest1() {
    Belief p = new Belief("p", 1);
    Belief q = new Belief("q", 1);

    Belief notQ = new Belief("!q", 3);

    BeliefBase base = new BeliefBase(List.of(p, q));
    BeliefRevisionAgent agent = new BeliefRevisionAgent(base);
    BeliefBase revised = agent.revise(notQ);

    assertThat(revised).isNotNull();
    assertThat(revised.beliefs()).contains(notQ);
    assertThat(revised.beliefs()).doesNotContain(q);
    assertThat(revised.isConsistent());
  }

  //From slides9
  //Assume Bob believes: Cn({p, q, r })
  //He learns, from a reliable source: ¬(q ∨ r )
  //Should be Cn({p, ¬(q ∨ r )})
  @Test
  void revisionTest2() {
    Belief p = new Belief("p", 1);
    Belief q = new Belief("q", 1);
    Belief r = new Belief("r", 1);

    Belief notQorR = new Belief("!(q | r)", 3);

    BeliefBase base = new BeliefBase(List.of(p, q, r));
    BeliefRevisionAgent agent = new BeliefRevisionAgent(base);
    BeliefBase revised = agent.revise(notQorR);

    assertThat(revised).isNotNull();
    assertThat(revised.beliefs()).contains(notQorR);
    assertThat(revised.beliefs()).doesNotContain(q);
    assertThat(revised.beliefs()).doesNotContain(r);
    assertThat(revised.isConsistent());
  }

  //from exe_week09
  //should follow from kb
  @Test
  void entailsTest1() {
    var kb = new BeliefBase(List.of(
        new Belief("!p -> q", 1),
        new Belief("q -> p", 1),
        new Belief("p -> r | s", 1)
    ));

    assertThat(kb.entails("p | r | s")).isTrue();
  }

  @Test
  void contractionTest() {
  }

  @Test
  void expansionTest() {

  }


}
