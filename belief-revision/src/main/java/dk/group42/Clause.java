package dk.group42;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/// Clauses are sets of literals and negations of literals that are implicitly disjunct
public record Clause(Set<Literal> literals) {

  public Clause(Set<Literal> literals) {
    this.literals = Set.copyOf(literals);
  }

  public static Clause empty() {
    return new Clause(Collections.emptySet());
  }

  public static List<Clause> toClauses(String formula) {
    return toClauses(Parser.parse(formula));
  }

  /// Convert {@link Sentence} to CNF following the steps described on p76, M. Ben-Ari, Mathematical
  /// Logic for Computer Science
  ///
  /// 1. substitute implications
  /// 2. move negations inward using De Morgan's law
  /// 3. remove double negations
  /// 4. recursively verify that the result is CNF
  ///
  /// @param s Arbitrary logical proposition
  /// @return a list of clauses (formula) F equivalent to `s` in clausal form
  public static List<Clause> toClauses(Sentence s) {
    Sentence subst = substituteImplications(s);
    Sentence deMorgan = moveNegationInward(subst); //also handles double negations
    Sentence cnf = toCnfAst(deMorgan);
    return collectClauses(cnf);
  }

  private static Sentence substituteImplications(Sentence s) {
    if (s instanceof AtomicSentence) {
      return s;
    }
    ComplexSentence cs = (ComplexSentence) s;
    switch (cs.connective()) {
      case NOT:
        return ComplexSentence.not(substituteImplications(cs.left()));
      case IMPLIES: {
        Sentence a = substituteImplications(cs.left());
        Sentence b = substituteImplications(cs.right());
        return ComplexSentence.binary(Connective.OR, ComplexSentence.not(a), b);
      }
      case IFF: {
        Sentence a = substituteImplications(cs.left());
        Sentence b = substituteImplications(cs.right());
        Sentence l = ComplexSentence.binary(Connective.OR, ComplexSentence.not(a), b);
        Sentence r = ComplexSentence.binary(Connective.OR, ComplexSentence.not(b), a);
        return ComplexSentence.binary(Connective.AND, l, r);
      }
      case AND:
      case OR:
        return ComplexSentence.binary(cs.connective(), substituteImplications(cs.left()),
            substituteImplications(cs.right()));
      default:
        throw new IllegalStateException("Unknown connective: " + cs.connective());
    }
  }

  /// Recursively distribute negation (using De Morgan's law) (Step for conversion to CNF)
  ///
  /// @param s sentence to process
  /// @return new sentence `s'` which is equivalent to `s` but only literals are negated
  private static Sentence moveNegationInward(Sentence s) {
    if (s instanceof AtomicSentence) {
      return s;
    }
    ComplexSentence cs = (ComplexSentence) s;

    if (cs.connective() == Connective.NOT) {
      Sentence child = cs.left();
      if (child instanceof AtomicSentence) {
        return cs;
      }

      ComplexSentence cchild = (ComplexSentence) child;
      return switch (cchild.connective()) {
        case NOT -> moveNegationInward(cchild.left());                 // !!x -> x
        case AND ->                                         // !(a & b) -> !a | !b
            ComplexSentence.binary(Connective.OR,
                moveNegationInward(ComplexSentence.not(cchild.left())),
                moveNegationInward(ComplexSentence.not(cchild.right())));
        case OR ->                                          // !(a | b) -> !a & !b
            ComplexSentence.binary(Connective.AND,
                moveNegationInward(ComplexSentence.not(cchild.left())),
                moveNegationInward(ComplexSentence.not(cchild.right())));
        default -> throw new IllegalStateException(
            "Implications/IFF should already be eliminated, got: " + cchild.connective());
      };
    }
    return ComplexSentence.binary(cs.connective(), moveNegationInward(cs.left()),
        moveNegationInward(cs.right()));
  }

  private static Sentence distributeOr(Sentence a, Sentence b) {
    if (a instanceof ComplexSentence ca && ca.connective() == Connective.AND) {
      return ComplexSentence.binary(Connective.AND, distributeOr(ca.left(), b),
          distributeOr(ca.right(), b));
    }
    if (b instanceof ComplexSentence cb && cb.connective() == Connective.AND) {
      return ComplexSentence.binary(Connective.AND, distributeOr(a, cb.left()),
          distributeOr(a, cb.right()));
    }
    return ComplexSentence.binary(Connective.OR, a, b);
  }

  private static Sentence toCnfAst(Sentence s) {
    if (s instanceof AtomicSentence) {
      return s;
    }
    ComplexSentence cs = (ComplexSentence) s;

    if (cs.connective() == Connective.NOT) {
      if (cs.left() instanceof AtomicSentence) {
        return cs;
      }
      throw new IllegalStateException("Expected negated literal but got NOT over " + cs.left());
    }
    if (cs.connective() == Connective.AND) {
      return ComplexSentence.binary(Connective.AND, toCnfAst(cs.left()), toCnfAst(cs.right()));
    }
    if (cs.connective() == Connective.OR) {
      return distributeOr(toCnfAst(cs.left()), toCnfAst(cs.right()));
    }
    throw new IllegalStateException("Unexpected connective in CNF conversion: " + cs.connective());
  }

  private static List<Clause> collectClauses(Sentence s) {
    List<Clause> clauses = new ArrayList<>();
    collectAnd(s, clauses);
    return clauses;
  }

  private static void collectAnd(Sentence s, List<Clause> out) {
    if (s instanceof ComplexSentence cs && cs.connective() == Connective.AND) {
      collectAnd(cs.left(), out);
      collectAnd(cs.right(), out);
    } else {
      Set<Literal> literals = new HashSet<>();
      collectOrInto(s, literals);
      out.add(new Clause(literals));
    }
  }

  private static void collectOrInto(Sentence s, Set<Literal> out) {
    if (s instanceof ComplexSentence cs) {
      if (cs.connective() == Connective.OR) {
        collectOrInto(cs.left(), out);
        collectOrInto(cs.right(), out);
        return;
      }
      if (cs.connective() == Connective.NOT && cs.left() instanceof AtomicSentence) {
        out.add(new Literal(((AtomicSentence) cs.left()).name(), false));
        return;
      }
    }
    if (s instanceof AtomicSentence) {
      out.add(new Literal(((AtomicSentence) s).name(), true));
      return;
    }
    throw new IllegalStateException("Expected literal or disjunction, got " + s);
  }

  public boolean isEmpty() {
    return literals.isEmpty();
  }

  public boolean isTautology() {
    Set<String> positives = literals.stream()
        .filter(Literal::positive)
        .map(Literal::atomName)
        .collect(HashSet::new, HashSet::add, HashSet::addAll);

    Set<String> negatives = literals.stream()
        .filter(l -> !l.positive())
        .map(Literal::atomName)
        .collect(HashSet::new, HashSet::add, HashSet::addAll);

    return literals.stream()
        .anyMatch(l -> positives.contains(l.atomName()) && negatives.contains(l.atomName()));

  }

  public static List<Clause> fromFormulaString(String f) {
    return toClauses(f);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Clause)) {
      return false;
    }
    return literals.equals(((Clause) o).literals);
  }

  @Override
  public String toString() {
    if (literals.isEmpty()) {
      return "⊥";
    }
    return "{" + String.join(", ", literals.stream().map(Literal::toString).toList()) + "}";
  }
}
