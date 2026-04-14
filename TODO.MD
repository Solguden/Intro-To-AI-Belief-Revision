# TODO

1. design and implementation of belief base
	- AtomicSentence := True | False | <Symbol>
	- ComplexSentece := Combination of AtomicSentence, connected with logical operators
	- LogicalOperators := !, ^, v, =>, <=>
	- Truth table

2. design and implementation of a method for checking logical entailment (e.g., resolution-
based), you should implement it yourself, without using any existing packages;
    - pseudocode for entailment on p239
    - pseudocode for resolution on p246
    - pseudocode for forward chaining on p249
    - pseudocode for CNF satisfiable on p252
    - CNF definition and CYK-parse algorithm on pp887-888
    - belief revision definition p353

3. implementation of contraction of belief base (based on a priority order on formulas in the
belief base);
    - contraction lecture 10 p12

4. implementation of expansion of belief base 
    - expansion lecture 10 p12

