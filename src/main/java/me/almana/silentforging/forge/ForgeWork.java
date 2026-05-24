package me.almana.silentforging.forge;

import java.util.List;

public record ForgeWork(String recipeId, int target, int value, List<ForgeAction> recent, int strikes) {
    public static ForgeWork started(String recipeId, int target) {
        return new ForgeWork(recipeId, target, 0, List.of(), 0);
    }

    public ForgeWork withAction(ForgeAction action) {
        List<ForgeAction> next = switch (recent.size()) {
            case 0 -> List.of(action);
            case 1 -> List.of(recent.get(0), action);
            case 2 -> List.of(recent.get(0), recent.get(1), action);
            default -> List.of(recent.get(1), recent.get(2), action);
        };
        return new ForgeWork(recipeId, target, clamp(value + action.power()), next, strikes + 1);
    }

    public boolean isComplete(int range) {
        return Math.abs(value - target) <= range;
    }

    public boolean isInRange(int range) {
        return Math.abs(value - target) <= range;
    }

    public ForgeAction last() {
        return recent.isEmpty() ? null : recent.get(recent.size() - 1);
    }

    public ForgeAction secondLast() {
        return recent.size() < 2 ? null : recent.get(recent.size() - 2);
    }

    public ForgeAction thirdLast() {
        return recent.size() < 3 ? null : recent.get(recent.size() - 3);
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
