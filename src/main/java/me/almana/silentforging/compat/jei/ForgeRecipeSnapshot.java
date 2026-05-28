package me.almana.silentforging.compat.jei;

import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class ForgeRecipeSnapshot {
    private static RecipeMap recipes = RecipeMap.EMPTY;

    private ForgeRecipeSnapshot() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ForgeRecipeSnapshot::onRecipesReceived);
    }

    private static void onRecipesReceived(RecipesReceivedEvent event) {
        recipes = event.getRecipeMap();
    }

    public static RecipeMap recipes() {
        return recipes;
    }
}