package dk.group42;

/**
 * Belief-revision agent applying the Levi identity:
 * <pre>
 *     K &lowast; &alpha;  =  (K &minus; &not;&alpha;) + &alpha;
 * </pre>
 *
 * <p>The agent keeps a mutable reference to the current belief base,
 * so successive operations (revise, contract, expand) stack naturally.
 * Each operation also returns the resulting {@link BeliefBase} for
 * convenience.
 */
public final class BeliefRevisionAgent {

    private BeliefBase base;

    public BeliefRevisionAgent() {
        this(new BeliefBase());
    }

    public BeliefRevisionAgent(BeliefBase base) {
        this.base = base;
    }

    public BeliefBase base() {
        return base;
    }

    // ----- revise -----

    public BeliefBase revise(String formula) {
        return revise(formula, null);
    }

    /**
     * K &lowast; &alpha;: first contract by &not;&alpha; to make room,
     * then expand by &alpha;.
     */
    public BeliefBase revise(String formula, Integer priority) {
        BeliefBase contracted = base.contract("!(" + formula + ")");
        BeliefBase revised    = contracted.expand(formula, priority);
        this.base = revised;
        return revised;
    }

    // ----- contract -----

    public BeliefBase contract(String formula) {
        this.base = base.contract(formula);
        return this.base;
    }

    // ----- expand -----

    public BeliefBase expand(String formula) {
        return expand(formula, null);
    }

    public BeliefBase expand(String formula, Integer priority) {
        this.base = base.expand(formula, priority);
        return this.base;
    }
}
