package dk.group42;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class AGMChecks {

  private AGMChecks() { /* no instances */ }

  public static boolean baseEquivalent(BeliefBase a, BeliefBase b) {
    Set<String> union = new LinkedHashSet<>(a.formulas());
    union.addAll(b.formulas());
    return union.stream().noneMatch(f -> a.entails(f) != b.entails(f));
  }


  public static boolean success(BeliefBase initial, String alpha) {
      if (!Resolution.isConsistent(List.of(alpha))) {
          return true; // vacuous
      }
    BeliefRevisionAgent a = new BeliefRevisionAgent(initial.copy());
    return a.revise(alpha).entails(alpha);
  }

  public static boolean inclusion(BeliefBase initial, String alpha) {
    BeliefRevisionAgent a = new BeliefRevisionAgent(initial.copy());
    BeliefBase revised = a.revise(alpha);
    BeliefBase expanded = initial.expand(alpha);
    return revised.formulas().stream().anyMatch(f -> !expanded.entails(f));
  }


  public static boolean vacuity(BeliefBase initial, String alpha) {
      if (initial.entails("!(" + alpha + ")")) {
          return true; // premise false, vacuously ok
      }
    BeliefRevisionAgent a = new BeliefRevisionAgent(initial.copy());
    BeliefBase revised = a.revise(alpha);
    BeliefBase expanded = initial.expand(alpha);
    return baseEquivalent(revised, expanded);
  }


  public static boolean consistency(BeliefBase initial, String alpha) {
      if (!Resolution.isConsistent(List.of(alpha))) {
          return true; // vacuous
      }
    BeliefRevisionAgent a = new BeliefRevisionAgent(initial.copy());
    return a.revise(alpha).isConsistent();
  }


  public static boolean extensionality(BeliefBase initial, String alpha, String beta) {
      if (!Resolution.equivalent(alpha, beta)) {
          return true; // vacuous
      }
    BeliefRevisionAgent a1 = new BeliefRevisionAgent(initial.copy());
    BeliefRevisionAgent a2 = new BeliefRevisionAgent(initial.copy());
    return baseEquivalent(a1.revise(alpha), a2.revise(beta));
  }

  public static Map<String, Boolean> runAll(BeliefBase initial, String alpha, String beta) {
    Map<String, Boolean> out = new LinkedHashMap<>();
    out.put("success", success(initial, alpha));
    out.put("inclusion", inclusion(initial, alpha));
    out.put("vacuity", vacuity(initial, alpha));
    out.put("consistency", consistency(initial, alpha));
    out.put("extensionality", extensionality(initial, alpha, beta));
    return out;
  }
}
