package dk.group42;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Resolution {

  private Resolution() { /* no instances */ }

  public static Set<Clause> resolve(Clause c1, Clause c2) {
    Set<Clause> out = new HashSet<>();
    for (Literal l : c1.literals()) {
      Literal comp = l.complement();
      if (c2.literals().contains(comp)) {
        Set<Literal> merged = new HashSet<>(c1.literals());
        merged.remove(l);
        for (Literal l2 : c2.literals()) {
          if (!l2.equals(comp)) {
            merged.add(l2);
          }
        }
        Clause candidate = new Clause(merged);
        if (!candidate.isTautology()) {
          out.add(candidate);
        }
      }
    }
    return out;
  }


  public static boolean isUnsatisfiable(Collection<Clause> clauses) {
    Set<Clause> current = new HashSet<>(clauses);

    // A literal empty clause in the input is already falsum.
    for (Clause c : current) {
        if (c.isEmpty()) {
            return true;
        }
    }

    while (true) {
      List<Clause> snapshot = new ArrayList<>(current);
      Set<Clause> produced = new HashSet<>();
      int n = snapshot.size();

      for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
          Set<Clause> resolvents = resolve(snapshot.get(i), snapshot.get(j));
          for (Clause r : resolvents) {
              if (r.isEmpty()) {
                  return true;
              }
          }
          produced.addAll(resolvents);
        }
      }

        if (current.containsAll(produced)) {
            return false; // saturation reached
        }
      current.addAll(produced);
    }
  }


  public static boolean entails(Iterable<String> kb, String query) {
    List<Clause> clauses = new ArrayList<>();
    for (String f : kb) {
      clauses.addAll(CNFConverter.toClauses(f));
    }
    clauses.addAll(CNFConverter.toClauses("!(" + query + ")"));
    return isUnsatisfiable(clauses);
  }


  public static boolean isConsistent(Iterable<String> formulas) {
    List<Clause> clauses = new ArrayList<>();
    for (String f : formulas) {
      clauses.addAll(CNFConverter.toClauses(f));
    }
    return !isUnsatisfiable(clauses);
  }


  public static boolean equivalent(String a, String b) {
    return entails(List.of(), "(" + a + ") -> (" + b + ")")
        && entails(List.of(), "(" + b + ") -> (" + a + ")");
  }
}
