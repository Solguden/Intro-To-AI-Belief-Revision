# AI Coding Agent Guidelines for Belief Revision Project

## Project Overview
This is a Java implementation of belief revision using AGM postulates. The system parses propositional logic formulas, converts them to CNF, and uses resolution theorem proving for entailment checking. Belief bases maintain prioritized beliefs with operations for expansion, contraction, and revision.

## Architecture

### Core Components
- **BeliefBase**: Immutable container for prioritized beliefs. Handles entailment via resolution and supports expansion/contraction operations.
- **BeliefRevisionAgent**: Manages belief revision by contracting negations then expanding new beliefs.
- **Logical Engine**: FormulaParser → Sentence AST → CNFConverter → Clauses → Resolution for theorem proving.
- **AGMChecks**: Validates AGM postulates (success, inclusion, vacuity, consistency, extensionality).

### Data Flow
Formulas (strings) → Parsed AST (Sentence) → CNF clauses (List<Clause>) → Resolution entailment checking.

### Key Classes
- `src/main/java/dk/group42/App.java`: Main entry point demonstrating revision and AGM checks.
- `src/main/java/dk/group42/BeliefBase.java`: Core belief base with entailment and consistency.
- `src/main/java/dk/group42/Resolution.java`: Resolution-based theorem prover.
- `src/main/java/dk/group42/CNFConverter.java`: Converts formulas to conjunctive normal form.

## Development Workflows

### Building
```bash
mvn compile
```

### Testing
```bash
mvn test
```
Uses JUnit 5 with AssertJ assertions. Tests in `src/test/java/dk/group42/BeliefRevisionTest.java`.

### Running
```bash
mvn exec:java -Dexec.mainClass=dk.group42.App
```

### Debugging
- Entailment issues: Check CNF conversion in `CNFConverter.toClauses()`.
- Resolution failures: Examine clause generation and resolution steps in `Resolution.entails()`.
- Parsing errors: Verify formula syntax against `FormulaParser` grammar.

## Project Conventions

### Code Style
- Immutable classes with records-like structure (final fields, no setters).
- Fluent method chaining (e.g., `base.expand(formula).contract(other)`).
- Null-safe with `Objects.requireNonNull()` in constructors.
- Package: `dk.group42` for all classes.
- Use stream API for collections processing (e.g., `beliefs.stream().filter(...)`).

### Formula Syntax
- Atoms: single letters or underscores (e.g., `p`, `q`, `atom_name`).
- Connectives: `!` (not), `&` (and), `|` (or), `->` (implies), `<->` (iff).
- Word operators: `not`, `and`, `or`.
- Parentheses for grouping.

### Belief Priorities
- Higher numbers = higher priority.
- Contraction removes lowest-priority beliefs first.
- Expansion assigns priority = max existing + 1 (or specified).

### Testing Patterns
- Use `BeliefBase` for KB setup with `List.of(new Belief(formula, priority))`.
- Assert entailment with `assertThat(base.entails(query)).isTrue()`.
- Check consistency with `assertThat(base.isConsistent()).isTrue()`.

## Integration Points

### Dependencies
- JUnit 5 (testing)
- AssertJ (fluent assertions)

### External Interfaces
- Console output in `App.main()` for demonstration.
- No external APIs or databases.

### Cross-Component Communication
- Beliefs passed as strings, parsed internally.
- Entailment queries use resolution on CNF clauses.
- Revision modifies belief base immutably, returns new instance.</content>
<parameter name="filePath">/home/primesoup/Documents/Uni/MSc/IntroAI/Intro-To-AI-Belief-Revision/belief-revision/AGENTS.md
