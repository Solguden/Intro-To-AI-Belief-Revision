# TODO

1. design and implementation of belief base
	- AtomicSentence := True | False | <Symbol>
	- ComplexSentece := Combination of AtomicSentence, connected with logical operators
	- LogicalOperators := !, ^, v, =>, <=>
	- Truth table

2. design and implementation of a method for checking logical entailment (e.g., resolution-
based), you should implement it yourself, without using any existing packages

3. implementation of contraction of belief base (based on a priority order on formulas in the
belief base)

4. implementation of expansion of belief base.
The output should be the resulting/new belief base.
