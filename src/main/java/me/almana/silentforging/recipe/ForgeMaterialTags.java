package me.almana.silentforging.recipe;

import net.minecraft.world.item.ItemStack;
import net.silentchaos512.gear.gear.material.MaterialInstance;

public final class ForgeMaterialTags {
    private ForgeMaterialTags() {
    }

    public static boolean isForgeMaterial(ItemStack stack) {
        return !stack.isEmpty() && MaterialInstance.from(stack) != null;
    }
}
