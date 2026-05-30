package me.almana.silentforging.forge;

public enum CraftDifficulty {
    EASY(5),
    NORMAL(3),
    HARD(1);

    private final int range;

    CraftDifficulty(int range) {
        this.range = range;
    }

    public int range() {
        return range;
    }
}
