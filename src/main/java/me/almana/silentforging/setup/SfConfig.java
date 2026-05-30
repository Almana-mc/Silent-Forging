package me.almana.silentforging.setup;

import me.almana.silentforging.forge.CraftDifficulty;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class SfConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.EnumValue<CraftDifficulty> DIFFICULTY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        DIFFICULTY = builder
                .comment("Tool forge craft tolerance. EASY = +/-5, NORMAL = +/-3, HARD = +/-1")
                .defineEnum("craftDifficulty", CraftDifficulty.NORMAL);
        SPEC = builder.build();
    }

    public static int range() {
        return DIFFICULTY.get().range();
    }

    private SfConfig() {
    }
}
