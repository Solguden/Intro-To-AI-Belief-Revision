# Belief Revision Agent

A Java implementation of belief revision using AGM postulates. The system parses propositional logic formulas, converts them to CNF, and uses resolution theorem proving for entailment checking.

## Dependencies

- **Java 17+** (Java Development Kit)
- **Maven 3.6+** (build tool)

## Installation

No additional dependencies are required beyond Java and Maven, which are needed to build the project.

## Running the Application

### Quick Start

Use the provided bash script to build and run the application:

```bash
./belief-revision.sh <command> --belief-base <input-file> --formula "<formula>" [--out <output-file>] [--agmchecks]
```

### Commands

#### `revise`
Revises the belief base with a new formula. Requires `--out` option.

```bash
./belief-revision.sh revise --belief-base input.txt --formula "p" --out output.txt
```

#### `expand`
Expands the belief base with a new formula. Requires `--out` option.

```bash
./belief-revision.sh expand --belief-base input.txt --formula "q" --out output.txt
```

#### `contract`
Contracts the belief base by removing a formula. Requires `--out` option.

```bash
./belief-revision.sh contract --belief-base input.txt --formula "p & q" --out output.txt
```

#### `entails`
Checks if the belief base entails the given formula. Prints `true` or `false`.

```bash
./belief-revision.sh entails --belief-base input.txt --formula "p"
```

### Optional Flags

- `--agmchecks`: Performs and prints AGM postulate checks (success, inclusion, vacuity, consistency, extensionality)

```bash
./belief-revision.sh revise --belief-base input.txt --formula "p" --out output.txt --agmchecks
```

## Belief Base File Format

Each belief is represented on a single line with the formula and priority separated by a colon:

```
formula:priority
p & q:1
r | s:2
!t:3
```

Higher priority numbers indicate higher-priority beliefs. These are removed first during contraction operations.

## Formula Syntax

- **Atoms**: single letters or underscores (e.g., `p`, `q`, `atom_name`)
- **Negation**: `!a` or `not a`
- **Conjunction**: `a & b` or `a and b`
- **Disjunction**: `a | b` or `a or b`
- **Implication**: `a -> b`
- **Biconditional**: `a <-> b`
- **Parentheses**: for grouping (e.g., `(p & q) | r`)

## Examples

### Revision Example

Input belief base (`base.txt`):
```
p:1
q:2
```

Revise with formula `!p`:
```bash
./belief-revision.sh revise --belief-base base.txt --formula "!p" --out revised.txt
```

Check entailment:
```bash
./belief-revision.sh entails --belief-base revised.txt --formula "!p"
```

Output: `true`

### With AGM Checks

```bash
./belief-revision.sh revise --belief-base base.txt --formula "p" --out output.txt --agmchecks
```

Output will include:
```
output.txt (saved)
success: true
inclusion: true
vacuity: false
consistency: true
extensionality: true
```

## Building Manually

If you prefer to build without the script:

```bash
mvn package -DskipTests
java -jar target/belief-revision-1.0-SNAPSHOT.jar <command> [options]
```

## Project Structure

- `src/main/java/dk/group42/` - Main source code
  - `App.java` - CLI entry point
  - `BeliefRevisionAgent.java` - Belief revision operations
  - `BeliefBase.java` - Belief base management
  - `Resolution.java` - Theorem prover
  - `CNFConverter.java` - Converts to conjunctive normal form
- `src/test/java/dk/group42/` - Test suite
- `pom.xml` - Maven configuration

