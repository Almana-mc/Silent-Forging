package me.almana.silentforging.forge;

public enum Difficulty {
    EASY(70, 92),
    NORMAL(76, 86),
    HARD(80, 84);

    private final int targetMin;
    private final int targetMax;

    Difficulty(int targetMin, int targetMax) {
        this.targetMin = targetMin;
        this.targetMax = targetMax;
    }

    public int targetMin() {
        return targetMin;
    }

    public int targetMax() {
        return targetMax;
    }
}
