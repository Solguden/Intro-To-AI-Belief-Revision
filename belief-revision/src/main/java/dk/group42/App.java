package dk.group42;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/// Our application provides an CLI for using the {@link BeliefRevisionAgent}
public class App {

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Usage: belief-revision <command> --belief-base <file> --formula <formula> [--out <file>] [--agmchecks]");
      return;
    }

    String command = args[0];
    boolean agmChecks = false;
    String beliefBaseFile = null;
    String formula = null;
    String outFile = null;

    for (int i = 1; i < args.length; i++) {
      switch (args[i]) {
        case "--agmchecks":
          agmChecks = true;
          break;
        case "--belief-base":
          if (i + 1 < args.length) beliefBaseFile = args[++i];
          break;
        case "--formula":
          if (i + 1 < args.length) formula = args[++i];
          break;
        case "--out":
          if (i + 1 < args.length) outFile = args[++i];
          break;
        default:
          System.err.println("Unknown option: " + args[i]);
          return;
      }
    }

    if (beliefBaseFile == null || formula == null) {
      System.err.println("Missing required options: --belief-base and --formula");
      return;
    }

    if ((command.equals("revise") || command.equals("expand") || command.equals("contract")) && outFile == null) {
      System.err.println("Missing --out for command: " + command);
      return;
    }

    try {
      BeliefBase base = loadBeliefBase(beliefBaseFile);
      BeliefRevisionAgent agent = new BeliefRevisionAgent(base);
      BeliefBase newBase = null;

      switch (command) {
        case "revise":
          newBase = agent.revise(formula);
          break;
        case "expand":
          newBase = agent.expand(formula);
          break;
        case "contract":
          newBase = agent.contract(formula);
          break;
        case "entails":
          boolean entails = base.entails(formula);
          System.out.println(entails);
          if (agmChecks) {
            Map<String, Boolean> checks = AGMChecks.runAll(base, formula, formula);
            checks.forEach((k, v) -> System.out.println(k + ": " + v));
          }
          return;
        default:
          System.err.println("Unknown command: " + command);
          return;
      }

      if (newBase != null) {
        saveBeliefBase(newBase, outFile);
        if (agmChecks) {
          Map<String, Boolean> checks = AGMChecks.runAll(base, formula, formula);
          checks.forEach((k, v) -> System.out.println(k + ": " + v));
        }
      }
    } catch (Exception e) {
      System.err.println("Error: " + e.getMessage());
    }
  }

  private static BeliefBase loadBeliefBase(String file) throws IOException {
    List<String> lines = Files.readAllLines(Path.of(file));
    List<Belief> beliefs = lines.stream()
      .filter(line -> !line.trim().isEmpty())
      .map(line -> {
        String[] parts = line.split(":", 2);
        if (parts.length != 2) {
          throw new IllegalArgumentException("Invalid belief format: " + line + ". Expected: formula:priority");
        }
        return new Belief(parts[0].trim(), Integer.parseInt(parts[1].trim()));
      })
      .toList();
    return new BeliefBase(beliefs);
  }

  private static void saveBeliefBase(BeliefBase base, String file) throws IOException {
    List<String> lines = base.beliefs().stream()
      .map(b -> b.formula() + ":" + b.priority())
      .toList();
    Files.write(Path.of(file), lines);
  }
}
