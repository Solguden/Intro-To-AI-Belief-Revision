package dk.group42;

import java.util.ArrayList;
import java.util.List;

/// The parser recognizes the following symbols
/// any literal:   `'p', 'q', 'this_is_a_literal'`
/// biimplication: `<->`
/// implication:   `->`
/// negation:      `!`
/// disjunction:   `|`
/// conjunction:   `&`
public final class Parser {

  private final List<String> tokens;
  private int pos = 0;

  public Parser(String text) {
    this.tokens = tokenize(text);
  }

  /// Directly parse a String rather
  /// @return {@link Sentence} object representing input formula
  /// @param text String with a propositional logic sentence following the syntax described in {@link Parser}
  public static Sentence parse(String text) {
    return new Parser(text).parseFormula();
  }

  public Sentence parseFormula() {
    Sentence s = parseEquivalence();
    if (pos < tokens.size()) {
      throw new IllegalArgumentException("Unexpected token '" + tokens.get(pos) + "'");
    }
    return s;
  }

  /// Tokenize an input String
  /// @return a token stream as a List String
  /// @param text String with a propositional logic sentence following the syntax described in {@link Parser}
  private List<String> tokenize(String text) {
    List<String> out = new ArrayList<>();
    int i = 0;
    while (i < text.length()) {
      char c = text.charAt(i);

      if (Character.isWhitespace(c)) {
        i++;
        continue;
      }

      if (text.startsWith("<->", i)) {
        out.add("<->");
        i += 3;
        continue;
      }

      if (text.startsWith("->", i)) {
        out.add("->");
        i += 2;
        continue;
      }

      if ("()!&|".indexOf(c) >= 0) {
        out.add(String.valueOf(c));
        i++;
        continue;
      }

      if (Character.isLetter(c) || c == '_') {
        int j = i + 1;
        while (j < text.length()
            && (Character.isLetterOrDigit(text.charAt(j)) || text.charAt(j) == '_')) {
          j++;
        }
        String word = text.substring(i, j);
        out.add(word);
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
