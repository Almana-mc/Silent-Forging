package me.almana.silentforging.recipe;

import me.almana.silentforging.Silentforging;
import me.almana.silentforging.forge.ForgeRule;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class DefaultForgeProfiles {
    public static final List<Entry> ENTRIES = List.of(
            part("adornment", 24, 5, ForgeRule.TEMPER_LAST),
            part("binding", 34, 5, ForgeRule.BEND_LAST),
            part("coating", 28, 5, ForgeRule.TEMPER_LAST),
            part("cord", 40, 5, ForgeRule.DRAW_LAST),
            part("fletching", 30, 5, ForgeRule.DRAW_LAST),
            part("grip", 36, 5, ForgeRule.HIT_LAST),
            part("lining", 56, 5, ForgeRule.FOLD_LAST),
            part("rod", 44, 5, ForgeRule.EXTRUDE_LAST),
            part("tip", 32, 5, ForgeRule.DRAW_LAST),
            gear("arrow_head", 48, 5, ForgeRule.DRAW_LAST),
            gear("axe_head", 82, 5, ForgeRule.HIT_LAST),
            gear("boot_plates", 72, 5, ForgeRule.BEND_LAST),
            gear("bow_main", 58, 5, ForgeRule.DRAW_LAST),
            gear("bracelet_main_only", 42, 5, ForgeRule.TEMPER_LAST),
            gear("chestplate_plates", 94, 5, ForgeRule.HIT_LAST),
            gear("crossbow_main", 68, 5, ForgeRule.FOLD_LAST),
            gear("dagger_head", 52, 5, ForgeRule.DRAW_LAST),
            gear("elytra_wings", 64, 5, ForgeRule.TEMPER_LAST),
            gear("excavator_head", 94, 5, ForgeRule.EXTRUDE_LAST),
            gear("fishing_rod_main", 54, 5, ForgeRule.DRAW_LAST),
            gear("hammer_head", 88, 5, ForgeRule.HIT_LAST),
            gear("helmet_plates", 76, 5, ForgeRule.BEND_LAST),
            gear("hoe_head", 66, 5, ForgeRule.BEND_LAST),
            gear("katana_head", 86, 5, ForgeRule.DRAW_LAST),
            gear("knife_head", 50, 5, ForgeRule.DRAW_LAST),
            gear("legging_plates", 90, 6, ForgeRule.HIT_LAST),
            gear("mace_core", 78, 5, ForgeRule.FOLD_LAST),
            gear("machete_head", 74, 5, ForgeRule.BEND_LAST),
            gear("mattock_head", 84, 5, ForgeRule.BEND_LAST),
            gear("necklace_main_only", 46, 5, ForgeRule.TEMPER_LAST),
            gear("paxel_head", 94, 5, ForgeRule.EXTRUDE_LAST),
            gear("pickaxe_head", 82, 5, ForgeRule.HIT_LAST),
            gear("prospector_hammer_head", 82, 5, ForgeRule.HIT_LAST),
            gear("ring_main_only", 38, 5, ForgeRule.TEMPER_LAST),
            gear("saw_head", 70, 5, ForgeRule.EXTRUDE_LAST),
            gear("shears_head", 58, 5, ForgeRule.HIT_LAST),
            gear("shield_plate", 88, 5, ForgeRule.HIT_LAST),
            gear("shovel_head", 64, 5, ForgeRule.BEND_LAST),
            gear("sickle_head", 62, 5, ForgeRule.DRAW_LAST),
            gear("slingshot_main", 44, 5, ForgeRule.DRAW_LAST),
            gear("spear_head", 66, 5, ForgeRule.DRAW_LAST),
            gear("sword_head", 74, 5, ForgeRule.DRAW_LAST),
            gear("trident_head", 92, 6, ForgeRule.EXTRUDE_LAST)
    );

    private DefaultForgeProfiles() {
    }

    private static Entry gear(String path, int target, int range, ForgeRule... rules) {
        return entry("gear/" + path, target, range, rules);
    }

    private static Entry part(String path, int target, int range, ForgeRule... rules) {
        return entry("part/" + path, target, range, rules);
    }

    private static Entry entry(String sourcePath, int target, int range, ForgeRule... rules) {
        return new Entry(sourcePath, target, range, List.of(rules));
    }

    public record Entry(String sourcePath, int target, int range, List<ForgeRule> rules) {
        public Identifier profileId() {
            return Identifier.fromNamespaceAndPath(Silentforging.MODID, "forging/" + sourcePath);
        }

        public Identifier sourceId() {
            return Identifier.fromNamespaceAndPath("silentgear", sourcePath);
        }

        public ForgingRecipe recipe() {
            return new ForgingRecipe(sourceId(), target, range, rules);
        }
    }
}
