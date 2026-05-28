package me.almana.silentforging.compat.jei;

import me.almana.silentforging.forge.ForgeRule;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public record ForgingDisplay(
        Ingredient blueprint,
        List<Ingredient> extras,
        int materialCost,
        int target,
        int range,
        List<ForgeRule> rules,
        CraftingRecipe recipe
) {
}
