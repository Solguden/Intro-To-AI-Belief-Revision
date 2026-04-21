package dk.group42;

import java.util.List;
import java.util.Map;

public class App {

    public static void main(String[] args) {

        // ---- initial prioritised belief base ----
        BeliefBase base = new BeliefBase(List.of(
                new Belief("p -> q", 1),
                new Belief("p",      1),
                new Belief("q -> r", 2)
        ));

        System.out.println("Initial formulas:   " + base.formulas());
        System.out.println("Initial entails q:  " + base.entails("q"));
        System.out.println("Initial consistent: " + base.isConsistent());

        // ---- revise by !q ----
        BeliefRevisionAgent agent = new BeliefRevisionAgent(base);
        BeliefBase revised = agent.revise("!q", 3);

        System.out.println();
        System.out.println("After revision with !q:");
        System.out.println("  Formulas:    " + revised.formulas());
        System.out.println("  Entails !q:  " + revised.entails("!q"));
        System.out.println("  Consistent:  " + revised.isConsistent());

        // ---- AGM postulate checks ----
        // NOTE: beta is chosen to be logically equivalent to alpha but
        // syntactically different, so Extensionality is actually exercised.
        // (!q) is equivalent to (q -> (p & !p)) because the RHS is false.
        BeliefBase agmInitial = new BeliefBase(List.of(
                new Belief("p -> q", 1),
                new Belief("p",      1)
        ));
        Map<String, Boolean> checks =
                AGMChecks.runAll(agmInitial, "!q", "q -> (p & !p)");

        System.out.println();
        System.out.println("AGM checks:");
        for (Map.Entry<String, Boolean> e : checks.entrySet()) {
            System.out.printf("  %-15s %s%n", e.getKey() + ":", e.getValue());
        }
    }
}
