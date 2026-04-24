package dk.group42;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class FormulaParser {

  private static final Set<String> WORD_OPS = Set.of("not", "and", "or");

  private final List<String> tokens;
  private int pos = 0;

  public FormulaParser(String text) {
    this.tokens = tokenize(text);
  }

  /**
   * Convenience: parse and return the AST root.
   */
  public static Sentence parse(String text) {
    return new FormulaParser(text).parseFormula();
  }

  public Sentence parseFormula() {
    Sentence s = parseEquivalence();
    if (pos < tokens.size()) {
      throw new IllegalArgumentException("Unexpected token '" + tokens.get(pos) + "'");
    }
    return s;
  }

  // tokenizer

  private static List<String> tokenize(String text) {
    List<String> out = new ArrayList<>();
    int i = 0;
    while (i < text.length()) {
      char c = text.charAt(i);

      if (Character.isWhitespace(c)) {
        i++;
        continue;
      }

      // Multi-character operators: check longest first.
      if (text.startsWith("<->", i)) {
        out.add("<->");
        i += 3;
        continue;
      }
      if (text.startsWith("<=>", i)) {
        out.add("<->");
        i += 3;
        continue;
      }
      if (text.startsWith("->", i)) {
        out.add("->");
        i += 2;
        continue;
      }
      if (text.startsWith("=>", i)) {
        out.add("->");
        i += 2;
        continue;
      }

      // Single-character punctuation / symbolic operators.
      if ("()!~&|^".indexOf(c) >= 0) {
        out.add(String.valueOf(c));
        i++;
        continue;
      }

      // Identifiers (possibly reserved words).
      if (Character.isLetter(c) || c == '_') {
        int j = i + 1;
        while (j < text.length()
            && (Character.isLetterOrDigit(text.charAt(j)) || text.charAt(j) == '_')) {
          j++;
        }
        String word = text.substring(i, j);
        String lower = word.toLowerCase();
        out.add(WORD_OPS.contains(lower) ? lower : word);
        i = j;
        continue;
      }

      throw new IllegalArgumentException("Unexpected character '" + c + "' in formula: " + text);
    }
    return out;
  }

  // helpers

  private String peek() {
    return pos >= tokens.size() ? null : tokens.get(pos);
  }

  private void consume(String expected) {
    String t = peek();
    if (!expected.equals(t)) {
      throw new IllegalArgumentException("Expected '" + expected + "', got '" + t + "'");
    }
    pos++;
  }

  private String consumeAny() {
    String t = peek();
    if (t == null) {
      throw new IllegalArgumentException("Unexpected end of formula");
    }
    pos++;
    return t;
  }

  // ----- grammar -----

  // equivalence := implication ('<->' implication)*
  private Sentence parseEquivalence() {
    Sentence node = parseImplication();
    while ("<->".equals(peek())) {
      consume("<->");
      node = ComplexSentence.binary(Connective.IFF, node, parseImplication());
    }
    return node;
  }

  // implication := or ('->' implication)?      // right-associative
  private Sentence parseImplication() {
    Sentence left = parseOr();
    if ("->".equals(peek())) {
      consume("->");
      Sentence right = parseImplication();
      return ComplexSentence.binary(Connective.IMPLIES, left, right);
    }
    return left;
  }

  // or := and (('|' | 'or') and)*
  private Sentence parseOr() {
    Sentence node = parseAnd();
    while ("|".equals(peek()) || "or".equals(peek())) {
      consumeAny();
      node = ComplexSentence.binary(Connective.OR, node, parseAnd());
    }
    return node;
  }

  // and := not (('&' | '^' | 'and') not)*
  private Sentence parseAnd() {
    Sentence node = parseNot();
    while ("&".equals(peek()) || "^".equals(peek()) || "and".equals(peek())) {
      consumeAny();
      node = ComplexSentence.binary(Connective.AND, node, parseNot());
    }
    return node;
  }

  // not := ('!' | '~' | 'not') not | atom
  private Sentence parseNot() {
    String t = peek();
    if ("!".equals(t) || "~".equals(t) || "not".equals(t)) {
      consumeAny();
      return ComplexSentence.not(parseNot());
    }
    return parseAtom();
  }

  // atom := '(' equivalence ')' | IDENTIFIER
  private Sentence parseAtom() {
    String t = peek();
    if (t == null) {
      throw new IllegalArgumentException("Unexpected end of formula");
    }
    if ("(".equals(t)) {
      consume("(");
      Sentence node = parseEquivalence();
      consume(")");
      return node;
    }
    if (Character.isLetter(t.charAt(0)) || t.charAt(0) == '_') {
      consumeAny();
      return new AtomicSentence(t);
    }
    throw new IllegalArgumentException("Expected atom or '(', got '" + t + "'");
  }
}
