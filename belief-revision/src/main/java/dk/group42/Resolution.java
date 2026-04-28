package dk.group42;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// Collection of static methods used by the {@link BeliefRevisionAgent}
public final class Resolution {

  private Resolution() { /* no instances */ }

  /// Resolution according to Algorithm 4.18, p81, M. Ben-Ari, Mathematical Logic for Computer Science
  public static Set<Clause> resolve(Clause c1, Clause c2) {
    Set<Clause> out = new HashSet<>();
    for (Literal l : c1.literals()) {
      Literal comp = l.complement();
      if (c2.literals().contains(comp)) {
        Set<Literal> merged = new HashSet<>(c1.literals());
        merged.remove(l);

        merged.addAll(c2.literals().stream().filter(l2 -> !l2.equals(comp)).toList());

        Clause candidate = new Clause(merged);
        if (!candidate.isTautology()) {
          out.add(candidate);
        }
      }
    }
    return out;
  }

  /// @return true if input collection of {@link Clause}s (aka. a formula on clausal form :^) ) are unsatisfiable
  public static boolean isUnsatisfiable(Collection<Clause> clauses) {
    Set<Clause> current = new HashSet<>(clauses);

    if (clauses.stream().anyMatch(Clause::isEmpty)) {
      return true;
    }

    while (true) {
      List<Clause> snapshot = new ArrayList<>(current);
      Set<Clause> produced = new HashSet<>();
      int n = snapshot.size();

      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
          Set<Clause> resolvents = resolve(snapshot.get(i), snapshot.get(j));

          if (resolvents.stream().anyMatch(Clause::isEmpty)) {
            return true;
          }

          produced.addAll(resolvents);
        }
      }

      if (current.containsAll(produced)) {
        return false;
      }
      current.addAll(produced);
    }
  }


  /// @return `true` if `query` follows from {@link BeliefBase bb}
  /// @param bb The Collection of {@link Belief}s which shoul derive `query`
  /// @param query the query formula which the caller wants to know if derives form `bb`
  public static boolean entails(Collection<String> bb, String query) {
    List<Clause> clauses = new ArrayList<>(
        bb.stream().map(Clause::fromFormulaString)
            .flatMap(List::stream)
            .toList());

    clauses.addAll(Clause.fromFormulaString("!(" + query + ")"));
    return isUnsatisfiable(clauses);
  }

  /// @return true if input collection of `formulas` are consistent with eachother
  public static boolean isConsistent(Collection<String> formulas) {
    List<Clause> clauses = new ArrayList<>(
        formulas.stream().map(Clause::fromFormulaString)
            .flatMap(List::stream)
            .toList());

    return !isUnsatisfiable(clauses);
  }


  /// @return true if formula `a` and fromula `b` are equivalent
  public static boolean equivalent(String a, String b) {
    return entails(List.of(), "(" + a + ") -> (" + b + ")")
        && entails(List.of(), "(" + b + ") -> (" + a + ")");
  }
}
