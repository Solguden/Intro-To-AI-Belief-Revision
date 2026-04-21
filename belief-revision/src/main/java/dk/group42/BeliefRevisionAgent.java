package dk.group42;

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


    public BeliefBase revise(String formula) {
        return revise(formula, null);
    }


    public BeliefBase revise(String formula, Integer priority) {
        BeliefBase contracted = base.contract("!(" + formula + ")");
        BeliefBase revised    = contracted.expand(formula, priority);
        this.base = revised;
        return revised;
    }

    public BeliefBase revise(Belief newBelief) {
        BeliefBase contracted = base.contract("!(" + newBelief.formula() + ")");
        BeliefBase revised    = contracted.expand(newBelief.formula(), newBelief.priority());
        this.base = revised;
        return revised;
    }


    public BeliefBase contract(String formula) {
        this.base = base.contract(formula);
        return this.base;
    }


    public BeliefBase expand(String formula) {
        return expand(formula, null);
    }

    public BeliefBase expand(String formula, Integer priority) {
        this.base = base.expand(formula, priority);
        return this.base;
    }
}
