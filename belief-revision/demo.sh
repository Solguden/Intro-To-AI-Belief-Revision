#!/bin/bash

# Demo script for belief-revision
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BELIEF_REVISION="$PROJECT_DIR/belief-revision.sh"
INPUT_BASE="$PROJECT_DIR/demobeliefbase.txt"

# Check if input file exists
if [ ! -f "$INPUT_BASE" ]; then
    echo "Error: $INPUT_BASE not found"
    exit 1
fi

echo "=== Belief Revision Demo ==="
echo ""
echo "Initial belief base:"
cat "$INPUT_BASE"
echo ""
echo "======================================"
echo ""

# Demo 1: Entailment Check
echo "1. ENTAILMENT CHECK: Does the belief base entail 'p'?"
echo "Command: ./belief-revision.sh entails --belief-base demobeliefbase.txt --formula \"p\""
$BELIEF_REVISION entails --belief-base "$INPUT_BASE" --formula "p"
echo ""
echo "======================================"
echo ""

# Demo 2: Expansion
echo "2. EXPANSION: Expand belief base with 'u & v'"
echo "Command: ./belief-revision.sh expand --belief-base demobeliefbase.txt --formula \"u & v\" --out demo_expanded.txt"
$BELIEF_REVISION expand --belief-base "$INPUT_BASE" --formula "u & v" --out "$PROJECT_DIR/demo_expanded.txt"
echo "Result written to: demo_expanded.txt"
cat "$PROJECT_DIR/demo_expanded.txt"
echo ""
echo "======================================"
echo ""

# Demo 3: Contraction
echo "3. CONTRACTION: Contract belief base by removing 'p'"
echo "Command: ./belief-revision.sh contract --belief-base demobeliefbase.txt --formula \"p\" --out demo_contracted.txt"
$BELIEF_REVISION contract --belief-base "$INPUT_BASE" --formula "p" --out "$PROJECT_DIR/demo_contracted.txt"
echo "Result written to: demo_contracted.txt"
cat "$PROJECT_DIR/demo_contracted.txt"
echo ""
echo "======================================"
echo ""

# Demo 4: Revision
echo "4. REVISION: Revise belief base with '!p'"
echo "Command: ./belief-revision.sh revise --belief-base demobeliefbase.txt --formula \"!p\" --out demo_revised.txt"
$BELIEF_REVISION revise --belief-base "$INPUT_BASE" --formula "!p" --out "$PROJECT_DIR/demo_revised.txt"
echo "Result written to: demo_revised.txt"
cat "$PROJECT_DIR/demo_revised.txt"
echo ""
echo "======================================"
echo ""

# Demo 5: Revision with AGM Checks
echo "5. REVISION WITH AGM CHECKS: Revise with 'q' and perform AGM postulate checks"
echo "Command: ./belief-revision.sh revise --belief-base demobeliefbase.txt --formula \"q\" --out demo_revised_agm.txt --agmchecks"
$BELIEF_REVISION revise --belief-base "$INPUT_BASE" --formula "q" --out "$PROJECT_DIR/demo_revised_agm.txt" --agmchecks
echo ""
echo "======================================"
echo ""

echo "Demo completed! Output files created:"
echo "  - demo_expanded.txt"
echo "  - demo_contracted.txt"
echo "  - demo_revised.txt"
echo "  - demo_revised_agm.txt"

