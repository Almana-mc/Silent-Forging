package me.almana.silentforging.forge;

import me.almana.silentforging.recipe.ForgingRecipe;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public record ForgeStrikeBenchmark(int minimumStrikes) {
    private static final int FAIL_EXTRA_STRIKES = 5;

    public static ForgeStrikeBenchmark from(ForgingRecipe recipe) {
        Queue<State> queue = new ArrayDeque<>();
        Set<SearchKey> seen = new HashSet<>();
        State start = new State(0, List.of(), 0);
        queue.add(start);
        seen.add(new SearchKey(start.value(), start.recent()));

        while (!queue.isEmpty()) {
            State state = queue.remove();
            ForgeWork work = new ForgeWork(recipe.sourceRecipe().toString(), recipe.target(), state.value(), state.recent(), state.strikes());
            if (state.strikes() > 0 && work.isComplete(recipe.range())) {
                return new ForgeStrikeBenchmark(state.strikes());
            }

            for (ForgeAction action : ForgeAction.values()) {
                ForgeWork nextWork = work.withAction(action);
                State next = new State(nextWork.value(), nextWork.recent(), nextWork.strikes());
                if (seen.add(new SearchKey(next.value(), next.recent()))) {
                    queue.add(next);
                }
            }
        }

        throw new IllegalStateException("No forging path for " + recipe.sourceRecipe());
    }

    public int failAtStrikes() {
        return minimumStrikes + FAIL_EXTRA_STRIKES + 1;
    }

    public boolean hasFailed(int strikes) {
        return strikes >= failAtStrikes();
    }

    public ForgeQuality qualityFor(int strikes) {
        return ForgeQuality.fromExtraStrikes(strikes - minimumStrikes);
    }

    private record State(int value, List<ForgeAction> recent, int strikes) {
    }

    private record SearchKey(int value, List<ForgeAction> recent) {
    }
}
